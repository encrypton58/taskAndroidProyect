package com.example.tareasmc256;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import java.util.GregorianCalendar;
import java.util.Objects;

public class editTasks extends AppCompatActivity implements dialogDesignedTime.interfazDesignado{


    Bundle datos;

    TextView showTitulo, showDescripcion,showHoras, showDesignado,showFecha;
    EditText editarTitulo, editarDescripcion;
    Button editarHora, editarDesignado,editarTarea;

    int id_tarea;

    public Context context;

    String newTitulo = "empty";
    int newHora = -1;
    int newMinuto = -1;
    String newDescripcion = "empty";
    String newFecha = "empty";
    String newDesignado = "empty";
    String dateForamt = "empty";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tasks);

        context = this;

        showTitulo = findViewById(R.id.typeTitleEditarTarea);
        showDescripcion = findViewById(R.id.descripcionEditarTarea);
        showDesignado = findViewById(R.id.tiempoDesignadoEditarTarea);
        showHoras = findViewById(R.id.horaEditarTarea);
        showFecha = findViewById(R.id.fechaEditarTarea);

        editarTitulo = findViewById(R.id.editarTitulo);
        editarDescripcion = findViewById(R.id.editarDescripcion);
        editarHora = findViewById(R.id.editarHora);
        editarDesignado = findViewById(R.id.editarDesignado);
        editarTarea = findViewById(R.id.editarTareaAccept);

        setDataFromTask();

        editarTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTask();
            }
        });

        editarHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog picker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        newHora = hour;
                        newMinuto = minute;
                        showHoras.setText("Nueva hora: " + newHora + ":" + newMinuto);
                    }
                },datos.getInt("hora"), datos.getInt("minuto"), false);
                picker.show();
            }
        });

        editarDesignado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new dialogDesignedTime(context, editTasks.this);
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void setDataFromTask(){
        datos = getIntent().getExtras();

        int hora = Objects.requireNonNull(datos).getInt("hora");
        int minutos = Objects.requireNonNull(datos).getInt("minuto");
        String horaFormat = hora + ":" + minutos;

        id_tarea = Objects.requireNonNull(datos).getInt("id_tarea");
        showTitulo.setText(datos.getString("titulo"));
        showHoras.setText(horaFormat);
        showDescripcion.setText(datos.getString("des"));
        showFecha.setText(datos.getString("fecha"));
        showDesignado.setText("Tiempo desginado " + datos.getString("designado") + " mins.");

    }

    public void editTask(){
        if(editarDescripcion.getText().toString().isEmpty() || editarTitulo.getText().toString().isEmpty()
                || newHora == -1 || newMinuto == -1 || newDesignado.equals("empty")){

            final AlertDialog.Builder build = new AlertDialog.Builder(context);
            build.setTitle("Alerta De Modificacion");
            build.setMessage("Si dejas vacio Los inputs o los botones se tomaran los valores anteriores \n" +
                    "estas seguro de queres continuar y no editar esos valores");
            build.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            build.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if(editarDescripcion.getText().toString().isEmpty() && editarTitulo.getText().toString().isEmpty()
                            && newHora == 0 && newMinuto == 0 && newDesignado.equals("empty")){
                        Intent it = new Intent(context, MainActivity.class);
                        context.startActivity(it);
                        Toast.makeText(context, "ignorar update no ha sido modificado nada", Toast.LENGTH_SHORT).show();

                    }else{

                        if(editarTitulo.getText().toString().isEmpty()){
                            newTitulo = datos.getString("titulo");
                        }else{
                            newTitulo = editarTitulo.getText().toString();
                        }

                        if(editarDescripcion.getText().toString().isEmpty()){
                            newDescripcion = datos.getString("des");
                        }else{
                            newDescripcion = editarDescripcion.getText().toString();
                        }

                        if(newHora == -1){
                            newHora = datos.getInt("hora");
                        }

                        if(newMinuto == -1) {
                            newMinuto = datos.getInt("minuto");
                        }

                        if(newDesignado.equals("empty")){
                            newDesignado = datos.getString("designado");
                        }
                        uploadDatabase();
                        Intent in = new Intent(context, MainActivity.class);
                        context.startActivity(in);
                        Toast.makeText(context, "update con algunos cambios", Toast.LENGTH_SHORT).show();

                    }
                }
            });

            AlertDialog al = build.create();
            al.show();

        }else{
            newTitulo = editarTitulo.getText().toString();
            newDescripcion = editarDescripcion.getText().toString();
            uploadDatabase();
            Intent in = new Intent(context, MainActivity.class);
            context.startActivity(in);
            Toast.makeText(context, "update Directo todo ha cambiado", Toast.LENGTH_SHORT).show();
        }


    }

    public void uploadDatabase(){
        String pattern = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        dateForamt = simpleDateFormat.format(new Date());
        newFecha = dateForamt;
        sqlite db = new sqlite(getApplicationContext());
        db.updateDataTareas(id_tarea, newTitulo, newHora,newMinuto,newDescripcion,newFecha, newDesignado);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void getTime(String tiempo) {
        newDesignado = tiempo;
        showDesignado.setText("Nuevo tiempo desginado " + newDesignado + " mins.");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent in = new Intent(context, MainActivity.class);
        startActivity(in);
    }
}