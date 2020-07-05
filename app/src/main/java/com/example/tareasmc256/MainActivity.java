package com.example.tareasmc256;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.tapadoo.alerter.Alerter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.Data;
import androidx.work.WorkManager;


public class MainActivity extends AppCompatActivity implements swipeTareas.recyclerItemTouchHelperListener, NavigationView.OnNavigationItemSelectedListener {


    //widgets
    TextView noWorks;
    TextView wv;
    TextView dia;
    ImageView iu;
    FloatingActionButton addTask;
    RelativeLayout constraintLayout;
    SwipeRefreshLayout swipeRefresh;
    //context
    Context context;
    //String
    String user;
    String date;
    //recyclerView
    public static List<tarea> task;
    public static recyclerAdapterItems taskAdapter;
    RecyclerView recyclerViewTask;
    //Calendar
    Calendar cal = Calendar.getInstance();
    //class from the project
    setNavMenu nav;
    sqlite sql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        this.sql = new sqlite(context);
        recyclerViewTask = findViewById(R.id.recyclerTarea);
        swipeRefresh = findViewById(R.id.swipeRefresLayout);

        recyclerViewTask.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new recyclerAdapterItems(getTareas());
        recyclerViewTask.setAdapter(taskAdapter);


        ItemTouchHelper.SimpleCallback simpleCallback =
                new swipeTareas(0, ItemTouchHelper.LEFT, MainActivity.this, context);

        new ItemTouchHelper(simpleCallback)
                .attachToRecyclerView(recyclerViewTask);

        ItemTouchHelper.SimpleCallback simpleCallbackRight =
                new swipeTareas(0, ItemTouchHelper.RIGHT, MainActivity.this, context);

        new ItemTouchHelper(simpleCallbackRight)
                .attachToRecyclerView(recyclerViewTask);


        wv = findViewById(R.id.welcomeView);
        iu = findViewById(R.id.inputNameUser);
        dia = findViewById(R.id.dia);
        addTask = findViewById(R.id.fb);
        constraintLayout = findViewById(R.id.mainLayout);
        noWorks = findViewById(R.id.noWorksText);

        //implements the nav drawer
        nav = new setNavMenu(context, this,MainActivity.this, R.id.nav_home);

        //coding of program in function
        getDataFromUsers();
        getDate();
        displayYupi();

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerViewTask.setLayoutManager(new LinearLayoutManager(context));
                taskAdapter = new recyclerAdapterItems(getTareas());
                recyclerViewTask.setAdapter(taskAdapter);


                ItemTouchHelper.SimpleCallback simpleCallback =
                        new swipeTareas(0, ItemTouchHelper.LEFT, MainActivity.this, context);

                new ItemTouchHelper(simpleCallback)
                        .attachToRecyclerView(recyclerViewTask);

                ItemTouchHelper.SimpleCallback simpleCallbackRight =
                        new swipeTareas(0, ItemTouchHelper.RIGHT, MainActivity.this, context);

                new ItemTouchHelper(simpleCallbackRight)
                        .attachToRecyclerView(recyclerViewTask);

                displayYupi();

                swipeRefresh.setRefreshing(false);
            }
        });

        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask();
            }
        });

        iu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sql.insertDataDoneTareas("prueba","prueba","prueba","prueba","prueba");
                nav = null;
                nav = new setNavMenu(context, MainActivity.this,MainActivity.this, R.id.nav_home);
               //se usa pa pruebas que necsiten un detionador
            }
        });

    }//end of method  create

    @SuppressLint("Recycle")
    //method check if is aviables registers in the bd
    public void getDataFromUsers(){
        Cursor c = sql.queryAllRegistersUsers();
        if(c.moveToFirst()){
            user = c.getString(1);
            SharedPreferences sp = getSharedPreferences("InputLogin", Context.MODE_PRIVATE);
            if(!sp.getBoolean("inputLogin", false)){
                SharedPreferences.Editor edit = sp.edit();
                edit.putBoolean("inputLogin", true);
                edit.apply();
                showAlert("Se ha Añadido El usuario :D", "Se ha añadido correctamente el usuario " + user);
            }
            wv.setText(user);
            byte[] image = c.getBlob(2);
            Bitmap convertImage = BitmapFactory.decodeByteArray(image, 0 ,image.length);
            RoundedBitmapDrawable rdUserIMagen = RoundedBitmapDrawableFactory.create(getResources(), convertImage);
            rdUserIMagen.setCornerRadius(convertImage.getHeight() * convertImage.getWidth());
            iu.setImageDrawable(rdUserIMagen);

        }else{
            crearDialog(context);
        }

    }

    //show a textview with funy message
    public void displayYupi() {
        if (task.size() > 0) {
            noWorks.setVisibility(View.INVISIBLE);

        } else {
            noWorks.setVisibility(View.VISIBLE);
        }
    }

    //crea un dialog que se muestra al usuario solo si no hay datos en la bbdd
    public void crearDialog(Context context) {
        AlertDialog.Builder build = new AlertDialog.Builder(context);
        build.setTitle("Hola Nuevo Usuario¡");
        build.setMessage("Se ha detectado que no tienes registrado tu nombre ni imagen " +
                "Agregalos para continuar");
        build.setCancelable(false);
        build.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), addUser.class);
                startActivity(intent);
            }
        });
        AlertDialog al = build.create();
        al.show();

    }

    //show a alert froe the user
    public void showAlert(String title, String message) {
        Alerter.create(this)
                .setTitle(title)
                .setText(message)
                .setBackgroundColorRes(R.color.colorAccent)
                .setIcon(R.drawable.imagen_users)
                .setDuration(5000)
                .enableVibration(true)
                .enableSwipeToDismiss()
                .show();
    }

    //get a date for show in the textView
    @SuppressLint("SetTextI18n")
    private void getDate() {
        Calendar calendar = Calendar.getInstance();
        String dayName = "";
        int diaN = calendar.get(Calendar.DAY_OF_WEEK);
        switch (diaN) {
            case 2:
                dayName = "Lunes";
                break;
            case 3:
                dayName = "Martes";
                break;
            case 4:
                dayName = "Miercoles";
                break;
            case 5:
                dayName = "Jueves";
                break;
            case 6:
                dayName = "Viernes";
                break;
            case 7:
                dayName = "Sabado";
                break;
            case 1:
                dayName = "Domingo";
                break;
        }
        dia.setText("El dia de hoy es " + dayName);

    }

    //call a Activity add Task
    public void addTask() {
        Intent intent = new Intent(getApplicationContext(), addTask.class);
        startActivity(intent);
    }

    //get the register of the table tasks
    @SuppressLint("Recycle")
    public List<tarea> getTareas() {

        task = new ArrayList<>();

        Cursor c;

        c = sql.queryAllRegistersTareas();
        if (c.moveToFirst()) {
            task.add(new tarea(c.getInt(0), c.getString(1),c.getInt(2), c.getInt(3), c.getString(4)
                    ,c.getString(5), c.getString(6) ));
            checkDataBase(c.getInt(0), c.getString(1), c.getInt(2),
                    c.getInt(3), c.getString(4), c.getString(6), c.getString(5));

            while (c.moveToNext()) {
                task.add(new tarea(c.getInt(0), c.getString(1),c.getInt(2), c.getInt(3), c.getString(4)
                        ,c.getString(5), c.getString(6) ));
                checkDataBase(c.getInt(0), c.getString(1), c.getInt(2),
                        c.getInt(3), c.getString(4), c.getString(6), c.getString(5));
            }
        } else {
            Toast.makeText(context, "No hay Registros", Toast.LENGTH_SHORT).show();
        }

        return task;
    }

    //generate a new WorkManager
    @SuppressLint("SimpleDateFormat")
    private void checkDataBase(int id, String titulo, int hora, int minuto, String descripcion, String tiempoDesignado, String fecha){

        setCalendar(fecha,hora, minuto);

        String tag = String.valueOf(id);
        long alertTime = cal.getTimeInMillis() - System.currentTimeMillis();
        int random = (int) (Math.random() * 50 + 1);
        Data data = saveData(titulo,descripcion, random, tiempoDesignado);
        workerManager.saveNoti(alertTime, data, tag);
        Toast.makeText(getApplicationContext(), alertTime + " milis , hourOfdataDase " + hora + ":" + minuto , Toast.LENGTH_SHORT).show();

    }

    //Create a new Object Data
    private Data saveData(String title, String details, int id, String timeDesignate){
        return new Data.Builder()
                .putString("titleTask", title)
                .putString("descriptionTask",details)
                .putInt("idTask",id)
                .putString("designado", timeDesignate).build();
    }

    //delete Register from bd
    private void deleteRegisters(String titulo) {
        Cursor c = sql.querySpecialTareas(titulo);
        if (c.moveToFirst()) {
            sql.deleteTareas(c.getInt(0));
        }else {
            Toast.makeText(context, "No hay Registros oh ocurrio un error en la consulta", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteWorkManagerFromTask(String tag, String title){
        WorkManager.getInstance(this).cancelAllWorkByTag(tag);
    }

    private void setCalendar(String fecha, int hora, int minuto){

        String subStrignDay = fecha.substring(0, 1);
        String subStrignMonth = fecha.substring(3,4);

        String subStrignyear = fecha.substring(6,10);

        if(subStrignDay.equals("0")){
            subStrignDay = fecha.substring(1,2);
        }else{
            subStrignDay = fecha.substring(0,2);
        }

        if(subStrignMonth.equals("0")){
            subStrignMonth = fecha.substring(4,5);
        }else{
            subStrignMonth = fecha.substring(3,5);
        }

        int day = Integer.parseInt(subStrignDay);
        int month = Integer.parseInt(subStrignMonth);
        int year = Integer.parseInt(subStrignyear);

        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hora);
        cal.set(Calendar.MINUTE, minuto);

    }


    // TODO: methods override we a override
    @Override
    public void onBackPressed() {
        if(nav.getDrawerLayout().isDrawerOpen(GravityCompat.START)){
            nav.getDrawerLayout().closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof recyclerAdapterItems.ViewHolder) {

            if(direction == ItemTouchHelper.LEFT){
                String name = getTareas().get(viewHolder.getAdapterPosition()).getTitulo();
                String id = String.valueOf(getTareas().get(viewHolder.getAdapterPosition()).getId());
                taskAdapter.removeItem(viewHolder.getAdapterPosition());
                deleteWorkManagerFromTask(id, name);
                deleteRegisters(name);
                if(serviceTimer.instancia != null){
                    new serviceTimer().stopBecauseDeleteTask(context);
                }
                stopService(new Intent(getApplicationContext(), serviceTimer.class));
                getTareas();
                displayYupi();

            }else if(direction == ItemTouchHelper.RIGHT){

                int id_tarea = getTareas().get(viewHolder.getAdapterPosition()).getId();
                String titulo_tarea = getTareas().get(viewHolder.getAdapterPosition()).getTitulo();
                int hora = getTareas().get(viewHolder.getAdapterPosition()).getHora();
                int minuto = getTareas().get(viewHolder.getAdapterPosition()).getMinuto();
                String des_tarea = getTareas().get(viewHolder.getAdapterPosition()).getDescripcion();
                String designado_tarea = getTareas().get(viewHolder.getAdapterPosition()).getHorasDesignadas();
                String fecha_tarea = getTareas().get(viewHolder.getAdapterPosition()).getFecha();
                deleteWorkManagerFromTask(String.valueOf(id_tarea), titulo_tarea);
                Intent i = new Intent(context, editTasks.class);
                i.putExtra("id_tarea", id_tarea);
                i.putExtra("titulo", titulo_tarea);
                i.putExtra("hora", hora);
                i.putExtra("minuto", minuto);
                i.putExtra("des", des_tarea);
                i.putExtra("fecha", fecha_tarea);
                i.putExtra("designado", designado_tarea);
                startActivity(i);

            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        nav.onNavigationItemSelected(menuItem);
        return true;
    }
}