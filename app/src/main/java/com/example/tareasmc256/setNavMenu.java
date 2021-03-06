package com.example.tareasmc256;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class setNavMenu implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    NavigationView navView;
    Context context;
    AppCompatActivity app;
    Menu menu;
    static int sizeBeforeBD;

    public setNavMenu(Context context, AppCompatActivity app, Activity activiti, int id){
        this.context = context;
        this.app = app;
         this.navView = app.findViewById(R.id.nav_view);
        Toolbar toolbar = app.findViewById(R.id.toolbar);
        this.drawerLayout = app.findViewById(R.id.drawaverLayout);
        app.setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(activiti ,drawerLayout,toolbar,R.string.open_nav, R.string.close_nav);
        navView.bringToFront();
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        this.menu = navView.getMenu();
        navView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) context);
        navView.setCheckedItem(id);

        View headView = navView.getHeaderView(0);

        sqlite sqlite = new sqlite(context);
        Cursor cursor = sqlite.queryAllRegistersUsers();

        if(cursor.moveToFirst()){
            byte[] image = cursor.getBlob(2);
            Bitmap convertImage = BitmapFactory.decodeByteArray(image, 0 ,image.length);
            RoundedBitmapDrawable rdUserImagen = RoundedBitmapDrawableFactory.create(context.getResources(), convertImage);
            rdUserImagen.setCornerRadius(convertImage.getHeight() * convertImage.getWidth());
            ImageView imageProfile = headView.findViewById(R.id.image_user_nav);
            TextView nameProfile = headView.findViewById(R.id.name_user_nav);
            imageProfile.setImageDrawable(rdUserImagen);
            nameProfile.setText(cursor.getString(1));
        }

        Cursor cTaskDone = sqlite.queryAllRegistersDoneTareas();
        if(cTaskDone.moveToFirst()){
            changeIconTaskDone(cTaskDone.getCount());
        }

    }

    public DrawerLayout getDrawerLayout(){
        return this.drawerLayout;
    }

    public void changeIconTaskDone(int size){
        if(size > 0){
            menu.findItem(R.id.nav_tasksDo).setActionView(R.layout.notify_layout_task_done);
        }else{
            menu.findItem(R.id.nav_tasksDo).setActionView(null);
        }
    }

    //TODO: metodos override

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()){

            case R.id.nav_home:
                Intent intentMain = new Intent(context.getApplicationContext(), MainActivity.class);
                app.startActivity(intentMain);
                break;

            case R.id.editProfile_nav:
                Intent intentEdit = new Intent(context.getApplicationContext(), editUser.class);
                app.startActivity(intentEdit);
                break;
            case R.id.about_nav:
                Toast.makeText(context, "about nav", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_tasksDo:
                Intent intentTasksDone = new Intent(context,taskDone.class);
                app.startActivity(intentTasksDone);

                break;
            default:
                Toast.makeText(context, "Se ha pulsado un navItem", Toast.LENGTH_SHORT).show();
                break;
        }

        return true;
    }
}
