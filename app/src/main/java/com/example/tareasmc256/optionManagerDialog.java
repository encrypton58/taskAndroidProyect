package com.example.tareasmc256;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

public class optionManagerDialog extends DialogFragment {

    private String titulo_tarea, fecha_tarea,des_tarea,designado_tarea;
    private int hora,minuto;

    private int id_tarea;


    @SuppressLint("SetTextI18n")
    public optionManagerDialog(Context contexto, final int  position_tarea, final int id, final String titulo, final int Hora, final int Minuto , String descripcion, String fecha, String horas_designadas) {

        this.titulo_tarea = titulo;
        this.fecha_tarea = fecha;
        this.des_tarea = descripcion;
        this.hora = Hora;
        this.minuto = Minuto;
        this.designado_tarea = horas_designadas;
        this.id_tarea = id;

        final Dialog dialogo = new Dialog(contexto);
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo.setCancelable(false);
        dialogo.setContentView(R.layout.options_dialog_items);

        TextView tituloView = dialogo.findViewById(R.id.tituloTareaOpcionesDialog);
        TextView fechaView = dialogo.findViewById(R.id.fecha_opcionTareas);
        TextView desView = dialogo.findViewById(R.id.descripcion_opcionTareas);
        TextView horaView = dialogo.findViewById(R.id.hora_opcionTareas);
        TextView designadoView = dialogo.findViewById(R.id.designado_opcionTareas);
        ImageButton editarTarea = dialogo.findViewById(R.id.editarOpcionDialog);
        ImageButton eliminarTarea = dialogo.findViewById(R.id.delTarea);
        ImageView close = dialogo.findViewById(R.id.closer_btn);

        tituloView.setText("Titulo: " + titulo);
        fechaView.setText("creado: " + fecha);
        desView.setText("Descripción: " + descripcion);
        designadoView.setText("Tiempo Designado: " + horas_designadas + " mins.");
        horaView.setText("inicia: " + hora + ":" + minuto);

        editarTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(dialogo.getContext(), editTasks.class);
                i.putExtra("id_tarea", id_tarea);
                i.putExtra("titulo", titulo_tarea);
                i.putExtra("hora", hora);
                i.putExtra("minuto", minuto);
                i.putExtra("des", des_tarea);
                i.putExtra("fecha", fecha_tarea);
                i.putExtra("designado", designado_tarea);
                dialogo.getContext().startActivity(i);

            }
        });


        eliminarTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sqlite con = new sqlite(dialogo.getContext());
                SQLiteDatabase db = con.getReadableDatabase();
                String tituloData = titulo_tarea;
                @SuppressLint("Recycle") Cursor c = db.rawQuery("SELECT * FROM Tareas WHERE titulo = '" + tituloData + "'", null);
                if (c.moveToFirst()) {
                    con.deleteTareas(c.getInt(0));
                    Toast.makeText(dialogo.getContext(), "Se elimino satisfactoriamente", Toast.LENGTH_SHORT).show();
                    dialogo.dismiss();
                    MainActivity.task.remove(position_tarea);
                    MainActivity.taskAdapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(dialogo.getContext(), "No se encotraron Registros", Toast.LENGTH_SHORT).show();
                }
            }
        });


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogo.dismiss();
            }
        });


        dialogo.show();

    }
}
