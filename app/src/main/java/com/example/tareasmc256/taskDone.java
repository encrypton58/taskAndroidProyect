package com.example.tareasmc256;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class taskDone extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //widgets
    Button clearTaskDone;
    TextView noTasks;
    //context
    Context context;
    //recyclerView
    public static List<tarea> task;
    public static recyclerAdapterTaskDone taskAdapter;
    RecyclerView tasksDone;
    //instance class
    setNavMenu nav;
    sqlite sql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_done);
        context = this;
        sql = new sqlite(context);
        initialItems();
        initialAdapter();



    }//end method create

    private void initialItems(){
       this.tasksDone = findViewById(R.id.recyclerTasksDone);
       this.clearTaskDone = findViewById(R.id.clearTaskDone);
       this.noTasks = findViewById(R.id.noTask);
       this.clearTaskDone.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               sql.deleteAllRegisterTaskDone();
               initialAdapter();
           }
       });

        this.nav = new setNavMenu(context,this,taskDone.this, R.id.nav_tasksDo);
        View menuView = nav.menu.findItem(R.id.nav_tasksDo).getActionView();
        if(!(menuView == null)){
            nav.menu.findItem(R.id.nav_tasksDo).setActionView(null);
        }


    }

    private void initialAdapter(){
        this.tasksDone.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new recyclerAdapterTaskDone(getTareas());
        this.tasksDone.setAdapter(taskAdapter);
    }

    private void displayNoTareas(){
        if(task.size() <= 0){
            noTasks.setVisibility(View.VISIBLE);
        }else{
            noTasks.setVisibility(View.INVISIBLE);
        }
    }

    //get the register of the table tasks
    @SuppressLint("Recycle")
    public List<tarea> getTareas() {

        task = new ArrayList<>();

        Cursor c = sql.queryAllRegistersDoneTareas();
        if (c.moveToFirst()) {
            task.add(new tarea(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4)
            , c.getString(5)));

            while (c.moveToNext()) {
                task.add(new tarea(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4)
                        , c.getString(5)));
            }
        } else {
            Toast.makeText(context, "No hay Registros", Toast.LENGTH_SHORT).show();
        }

        displayNoTareas();

        return task;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        nav.onNavigationItemSelected(menuItem);
        return true;
    }

}