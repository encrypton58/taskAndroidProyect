package com.example.tareasmc256;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
    TextView showTime, showDesigned;

    //context
    Context context;

    //Strings
    String titleTaskString, descriptionTaskString;
    String designedString = "empty";
    int hourInteger = 0;
    int minuteInteger = 0;
    String dateFormat;

    //boolean variables
    boolean isSetTomorrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        titleTask = findViewById(R.id.titleTask);
        descriptionTask = findViewById(R.id.descriptionTask);
        pickTime = findViewById(R.id.pickTime);
        pickDesigned = findViewById(R.id.pickDesigned);
        addTask = findViewById(R.id.addTask);
        showTime = findViewById(R.id.showTime);
        showDesigned = findViewById(R.id.showDesigned);

        context = this;

        //SharedPreferences sp = getSharedPreferences("ingreso", Context.MODE_PRIVATE);
        //if(!sp.getBoolean("InputLogin", false)){

        //}

        pickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTime();
            }
        });

        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInputsAndUploadDataBase();
            }
        });
        pickDesigned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new dialogDesignedTime(context, com.example.tareasmc256.addTask.this);
            }
        });

    }

    @SuppressLint("SimpleDateFormat")
    private void getTime(){
        Calendar c = Calendar.getInstance();
        final int hourSystem = c.get(Calendar.HOUR_OF_DAY);
        final int minuteSystem = c.get(Calendar.MINUTE);
        Toast.makeText(context, hourSystem + ":" + minuteSystem, Toast.LENGTH_SHORT).show();

        TimePickerDialog piker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                showTime.setText("La Hora Seleccionada\nes: " + hour + ":" + minute);
                if (hour <= hourSystem && minute < minuteSystem){
                   createDialog(context, "Hora no permitida","Quieres agregar la tarea para mañana a esa hora", true );
                }
                hourInteger = hour;
                minuteInteger = minute;

                Toast.makeText(context, hour + ":" + minute, Toast.LENGTH_SHORT).show();

            }
        }, hourSystem,minuteSystem,false);
        piker.show();
    }

    private void checkInputsAndUploadDataBase(){
        taskAboutTask();
        checkAsignateTommorrow();
        if(!getTextFromEditText() && !getDataFromButtons()){
            sqlite con = new sqlite(getApplicationContext());
            con.insertDataTareas(titleTaskString, hourInteger,minuteInteger,descriptionTaskString, dateFormat,designedString);
            SharedPreferences sp = getSharedPreferences("ingreso", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("entro",true);
            editor.apply();
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }
    }

    boolean error;

    private boolean getTextFromEditText(){

        if(titleTask.getText().toString().equals("") || descriptionTask.getText().toString().equals("")){
            createDialog(context,"Error", "No puedes Dejar Vacio Los cuadros de texto", false);
            error = true;
        }else{
            titleTaskString = titleTask.getText().toString();
            descriptionTaskString = descriptionTask.getText().toString();
            error = false;
        }

        return error;
    }

    private void taskAboutTask(){
        Cursor c = new sqlite(context).queryAllRegistersTareas();
        while(c.moveToNext()){
            if(hourInteger == c.getInt(2)){
                Toast.makeText(context, "Hay una alarma establecida en esa hora", Toast.LENGTH_SHORT).show();
                int a = c.getInt(3) + Integer.parseInt(c.getString(6));
                int b = c.getInt(2);
                if(a >= 60){
                    a = a - 60;
                    b = b + 1;

                }
            }
            String taskDesigned = c.getString(6);
            if(taskDesigned.equals(designedString)){
                createDialog(context,"Error de tiempo", "No puedes crear una tarea al mismo tiempo que otra", false);
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void checkAsignateTommorrow(){
        String pattern = "dd-MM-yyyy";
        if(isSetTomorrow){
            Calendar c = Calendar.getInstance();
            Date date = new Date();
            date.setDate(c.get(Calendar.DAY_OF_MONTH) + 1 );
            SimpleDateFormat sp = new SimpleDateFormat(pattern);
            dateFormat = sp.format(date);
            SharedPreferences sharedPreferences = getSharedPreferences("setTomorrow", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putBoolean("isSetTomorrow", isSetTomorrow);
            edit.apply();
            Toast.makeText(context, dateFormat, Toast.LENGTH_SHORT).show();
        }else{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            dateFormat = simpleDateFormat.format(new Date());
        }
    }

    private void createDialog(final Context context, String title, final String message, boolean moreOptions){

        if(moreOptions){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    getTime();
                }
            });
            builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    isSetTomorrow = true;
                    Toast.makeText(context, "Se ha establecido para mañana", Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            });
            builder.setCancelable(false);
            AlertDialog dialog = builder.create();
            dialog.show();

        }else{
            AlertDialog.Builder buider = new AlertDialog.Builder(context);
            buider.setTitle(title);
            buider.setMessage(message);
            buider.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = buider.create();
            dialog.show();
        }

    }

    private boolean getDataFromButtons(){
        if(hourInteger == 0 || minuteInteger == 0) {
            error = true;
            createDialog(context, "Error", "No puedes Dejar vacio la Hora de inicio ", false);
        }else{
            error = false;
        }
        if(designedString.equals("empty")){
            error = true;
            createDialog(context, "Error", "No puedes dejar vacio el tiempo desginado para la tarea", false);
        }else{
            error = false;
        }

        return error;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void getTime(String time) {
        showDesigned.setText("el Tiempo designado\nes: " + time + " mins.");
        designedString = time;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}