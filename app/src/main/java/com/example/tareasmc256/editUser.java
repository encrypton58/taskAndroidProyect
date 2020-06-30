package com.example.tareasmc256;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

public class editUser extends AppCompatActivity {

    //widgets
    EditText inputNewData;
    Button openGallery, updateUser;
    ImageView newImage, newDataImage;
    ProgressDialog alertProgress;
    RelativeLayout cons;
    //      context
    Context context;
    //      variables primitives
    final int REQUEST_GALLERY = 999;
    boolean imageIsSet = false;
    //      intance of class
    sqlite con;
    //Strings
    String user;
    //Rouunded drawable
    RoundedBitmapDrawable rdUserIMagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        //hooks with UI
        context = this;
        inputNewData = findViewById(R.id.inputNewName);
        openGallery = findViewById(R.id.selectNewImage);
        updateUser = findViewById(R.id.editUserAccept);
        newImage = findViewById(R.id.newImageUser);
        newDataImage = findViewById(R.id.newDataImage);
        cons = findViewById(R.id.relativeEditUser);
        //instance of class
        con = new sqlite(context);
        //configuration widgets
        newDataImage.setVisibility(View.INVISIBLE);
        alertProgress = new ProgressDialog(editUser.this);

        initialMessage();

        openGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(editUser.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_GALLERY);
            }
        });

        newImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup();
            }
        });

        updateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checarInput();
                if(!imageIsSet){
                    Cursor c = con.queryAllRegistersUsers();
                    if(c.moveToFirst()){
                        byte[] image = c.getBlob(2);
                        Bitmap convertImage = BitmapFactory.decodeByteArray(image, 0 ,image.length);
                        newDataImage.setImageBitmap(convertImage);
                    }
                }
                alertProgress.show();
                alertProgress.setContentView(R.layout.progress_layout);
                Objects.requireNonNull(alertProgress.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

                async a = new async();
                a.execute();

            }
        });

    }//end of method on create

    private void popup(){
        Cursor c = con.queryAllRegistersUsers();
        if(c.moveToFirst()){
            byte[] image = c.getBlob(2);
            Bitmap convertImage = BitmapFactory.decodeByteArray(image, 0 ,image.length);
            rdUserIMagen = RoundedBitmapDrawableFactory.create(getResources(), convertImage);
            rdUserIMagen.setCornerRadius(convertImage.getHeight() * convertImage.getWidth());
            user = c.getString(1);
        }
        new popupDataUser(context, rdUserIMagen, user);
    }

    private void initialMessage(){
        AlertDialog.Builder build = new AlertDialog.Builder(context);
        build.setTitle("Informacion de la app");
        build.setMessage("Presiona la imagen para ver tus datos actuales \nsi deseas dejar algunos de tus datos actuales solo dejalo vacio");
        build.setCancelable(false);
        build.setNegativeButton("No volver a mostrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        build.setPositiveButton("aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alert = build.create();
        alert.show();
    }

    private byte[] imageViewToByte(ImageView image){
        Bitmap bit = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.PNG,100,stream);

        return stream.toByteArray();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_GALLERY){
            if(grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent  = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_GALLERY);
            }else{
                showSnakeBar();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == REQUEST_GALLERY && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();

            try{
                assert uri != null;
                InputStream is = getContentResolver().openInputStream(uri);
                Bitmap bit = BitmapFactory.decodeStream(is);
                RoundedBitmapDrawable rd = RoundedBitmapDrawableFactory.create(getResources(),bit);
                rd.setCornerRadius((bit.getHeight() * bit.getWidth()));
                newImage.setImageDrawable(rd);
                newDataImage.setImageBitmap(bit);
                imageIsSet = true;
            }catch(FileNotFoundException fi){
                System.out.println(fi.getMessage());
            }

        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void checarInput(){
        if(inputNewData.getText().toString().equals("")){
            Cursor c = con.queryAllRegistersUsers();
            if (c.moveToFirst()) {
                user = c.getString(1);
            }

        }else{
            user = inputNewData.getText().toString();
        }
    }

    public void showSnakeBar(){
        Snackbar snake = Snackbar.make(cons,"Se han negado los Permisos Vuelve a intertarlo", Snackbar.LENGTH_SHORT);
        snake.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }

    @SuppressLint("StaticFieldLeak")
    private class async extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            con.updateDataUsers(user, imageViewToByte(newDataImage));
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            alertProgress.dismiss();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }


}