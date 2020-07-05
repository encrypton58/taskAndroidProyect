package com.example.tareasmc256;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class addTask extends AppCompatActivity implements dialogDesignedTime.interfazDesignado {

    //widgets
    EditText titleTask, descriptionTask;
    Button pickTime, pickDesigned, addTask;
    TextView showTime, showDesigned, title;
    //context
    Context context;

    //Strings
    String titleTaskString = "empty", descriptionTaskString = "empty";
    String designedString = "empty";
    int hourInteger = -1;
    int minuteInteger = -1;
    String dateFormat;
    //boolean variables
    boolean isSetTomorrow;
    //instace of class
    sqlite con;
    Calendar c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        //hooks UI
        titleTask = findViewById(R.id.titleTask);
        descriptionTask = findViewById(R.id.descriptionTask);
        pickTime = findViewById(R.id.pickTime);
        pickDesigned = findViewById(R.id.pickDesigned);
        addTask = findViewById(R.id.addTask);
        showTime = findViewById(R.id.showTime);
        showDesigned = findViewById(R.id.showDesigned);
        title = findViewById(R.id.title2);
        //inicialize variable
        context = this;
        con = new sqlite(context);
        c = Calendar.getInstance();
        //set Methods
        pickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePikerDialog();
            }
        });

        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadDatabase();
            }
        });
        pickDesigned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new dialogDesignedTime(context, addTask.this);
            }
        });

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "valor de isSetTomorrow : " + isSetTomorrow , Toast.LENGTH_SHORT).show();
            }
        });
    }

    // sube la informacion ala base de datos
    private void uploadDatabase(){
        if(!checkDataNotEmpty()){
            setDateFormat();
            con.insertDataTareas(titleTaskString,hourInteger,minuteInteger,
                    descriptionTaskString,dateFormat,designedString);
            startActivity(new Intent(context, MainActivity.class));
        }
    }

    //evalua los imputs para que no haya datos sin setear
    private boolean checkDataNotEmpty(){

        titleTaskString = titleTask.getText().toString();
        descriptionTaskString = descriptionTask.getText().toString();

        if(titleTask.getText().toString().isEmpty() || descriptionTask.getText().toString().isEmpty() || designedString.equals("empty") || hourInteger == -1){
            
            if(titleTask.getText().toString().isEmpty()){
                new simpleDialogInfo(context, "Error de entrada", "No puedes dejar el titulo de la tarea vacio");
            }else if(descriptionTask.getText().toString().isEmpty()){
                new simpleDialogInfo(context, "Error de entrada", "No puedes dejar la descripcion de la tarea Vacia");
            }else if(hourInteger == -1){
                new simpleDialogInfo(context, "Error de tiempo", "no puedes dejar la hora de inicio de la tarea vacia");
            }else if(designedString.equals("empty")){
                new simpleDialogInfo(context, "Error de tiempo", "no puedes dejar la hora designada de la tarea vacia");
            }
            
            return true;
        }else{
            return false;
        }
    }

    //escoje la hora de inicio de la alarma
    private void timePikerDialog(){
        Calendar c = Calendar.getInstance();
        final int hourSystem = c.get(Calendar.HOUR_OF_DAY);
        final int minuteSystem = c.get(Calendar.MINUTE);
        TimePickerDialog tp = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                hourInteger = hour;
                minuteInteger = minute;
                SetTomorrowTask(hour,minute, hourSystem, minuteSystem);
                showTime.setText("la hora selecionada\nes: " + hourInteger + ":" + minuteInteger);
            }
        }, hourSystem,minuteSystem,false);
        tp.show();

    }

    private void SetTomorrowTask(int hour, int minute, int hourSystem, int minuteSystem){
        if(hour <= hourSystem && minute <= minuteSystem){
            AlertDialog.Builder build = new AlertDialog.Builder(context)
                    .setTitle("Aviso")
                    .setMessage("la alarma se establecera a esa hora mañana")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            isSetTomorrow = true;
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton("Denegar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            isSetTomorrow = false;
                            dialogInterface.dismiss();
                            timePikerDialog();
                        }
                    });
            AlertDialog alertDialog = build.create();
            alertDialog.show();
        }else{
            Toast.makeText(context, "No se cumple la condicion establecida", Toast.LENGTH_SHORT).show();
        }
    }

    //establece la fecha de alarama de la tarea
    private void setDateFormat(){

        int day;
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        String dayString = "";
        String mothString = "";

        if(isSetTomorrow){
            day = c.get(Calendar.DAY_OF_MONTH) + 1;
        }else{
            day = c.get(Calendar.DAY_OF_MONTH);
        }

        if(!(day >= 10)){
            dayString = "0"+day;
        }else {
            dayString = String.valueOf(day);
        }
        if(!(month >= 10)){
            mothString = "0"+ month;
        }else{
            mothString = String.valueOf(month);
        }

        dateFormat = dayString + "-" + mothString +"-"+ year;


    }

    // TODO: overRide methos

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void getTime(String time) {
        designedString = time;
        showDesigned.setText("Tiempo designado\nes: " + designedString + "mins");
    }
}