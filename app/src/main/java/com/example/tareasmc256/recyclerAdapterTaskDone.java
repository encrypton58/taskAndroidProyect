package com.example.tareasmc256;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

public class recyclerAdapterTaskDone extends RecyclerView.Adapter<recyclerAdapterTaskDone.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tituloView, descripView,horaView,designadoView,fechaView,idView, positionView;
        public TextView eliminarTextoItem, editarTextItem;
        private View view;
        public ConstraintLayout layoutABorrar;
        public RelativeLayout borrarEditar;
        ImageView editarIcon, borrarIcon;
        String titulo,descipcion ,designado, fecha;
        int hora, minuto;
        int id;

        ViewHolder( View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.changeColor);
            borrarEditar = itemView.findViewById(R.id.editar_borrar);
            editarTextItem = itemView.findViewById(R.id.editarTextoItem);
            eliminarTextoItem = itemView.findViewById(R.id.eliminarTextoItem);
            borrarIcon = itemView.findViewById(R.id.imagen_eliminar_swipe);
            editarIcon = itemView.findViewById(R.id.imagen_editar_swipe);
            tituloView = itemView.findViewById(R.id.titulo_item2);
            descripView = itemView.findViewById(R.id.descripcion_item2);
            horaView = itemView.findViewById(R.id.hora_item2);
            designadoView = itemView.findViewById(R.id.tiempoDesignadoItem);
            fechaView = itemView.findViewById(R.id.fecha_item2);
            idView = itemView.findViewById(R.id.id_tarea);
            positionView = itemView.findViewById(R.id.positionView);
            layoutABorrar = itemView.findViewById(R.id.layoutABorrar);
            final ImageButton opcionesTarea = itemView.findViewById(R.id.opcionesItems);

            idView.setVisibility(View.INVISIBLE);
            positionView.setVisibility(View.INVISIBLE);

            Drawable dview = itemView.getContext().getDrawable(R.drawable.border_colors);
            Drawable dboton = itemView.getContext().getDrawable(R.drawable.background_icons_tareas);
            int r = (int) (Math.random() * 255); int g = (int) (Math.random() * 255); int b = (int) (Math.random() * 255);
            int color1 = Color.rgb(r,g,b);
            int r2 = (int) (Math.random() * 255);  int g2  = (int) (Math.random() * 255); int b2 = (int) (Math.random() * 255);
            int color2 = Color.rgb(r2,g2,b2);
            ColorFilter c = new LightingColorFilter(color1 , color2);
            Objects.requireNonNull(dview).setColorFilter(c);
            Objects.requireNonNull(dboton).setColorFilter(c);
            view.setBackground(dview);
            opcionesTarea.setBackground(dboton);

        }
    }

    public void removeItem(int position){
        tareaList.remove(position);
        notifyItemRemoved(position);
    }

    public List<tarea> tareaList;

    recyclerAdapterTaskDone(List<tarea> tareaList){
        this.tareaList = tareaList;
    }

    @NonNull
    @Override
    public recyclerAdapterTaskDone.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_recycler_view,parent,false);
        return new recyclerAdapterTaskDone.ViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(recyclerAdapterTaskDone.ViewHolder holder, int position) {
        holder.positionView.setText(String.valueOf(position));
        holder.idView.setText(String.valueOf(tareaList.get(position).getId()));
        holder.tituloView.setText(tareaList.get(position).getTitulo());
        holder.descripView.setText(tareaList.get(position).getDescripcion());
        holder.fechaView.setText(tareaList.get(position).getFecha());
        holder.horaView.setText( "Inicia: " + tareaList.get(position).getHoraStr());
        holder.designadoView.setText("Tiempo Designado\n" + (tareaList.get(position).getHorasDesignadas()) + " mins.");

        holder.id = tareaList.get(position).getId();
        holder.titulo = tareaList.get(position).getTitulo();
        holder.hora = tareaList.get(position).getHora();
        holder.minuto = tareaList.get(position).getMinuto();
        holder.descipcion = tareaList.get(position).getDescripcion();
        holder.designado = tareaList.get(position).getHorasDesignadas();
        holder.fecha = tareaList.get(position).getFecha();
    }

    @Override
    public int getItemCount() {
        return tareaList.size();
    }


}
