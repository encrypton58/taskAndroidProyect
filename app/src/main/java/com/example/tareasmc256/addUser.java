package com.example.tareasmc256;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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

public class addUser extends AppCompatActivity {

    //      widgets
    EditText editText;
    Button addUser,selectImage;
    RelativeLayout cons;
    ImageView imageUser, imagenData;
    //      context
    Context context;
    //      variables primitives
    final int REQUEST_GALLERY = 999;
    boolean imageIsSet = false;
    //      intance of class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        //** hooks
        context = this;
        cons = findViewById(R.id.constrainAddUsers);
        editText = findViewById(R.id.inputNameUser);
        addUser = findViewById(R.id.addUserButton);
        selectImage = findViewById(R.id.openImage);
        imageUser = findViewById(R.id.imageUser);
        imagenData = findViewById(R.id.imageP);
        imagenData.setVisibility(View.INVISIBLE);

        //instance of class




        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(addUser.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_GALLERY);
            }
        });

        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqlite con = new sqlite(getApplicationContext());
                if (!checarInput() && imageIsSet) {
                    con.insertDataUsuarios(editText.getText().toString(), imageViewToByte(imagenData));
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else if (!imageIsSet) {
                    AlertDialog.Builder buider = new AlertDialog.Builder(context);
                    buider.setTitle("Error en imagen");
                    buider.setMessage("No Puedes Dejar La imagen Vacia");
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


            });
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
                rd.setCornerRadius(bit.getHeight());
                imageUser.setImageDrawable(rd);
                imagenData.setImageBitmap(bit);
                imageIsSet = true;
            }catch(FileNotFoundException fi){
                System.out.println(fi.getMessage());
            }

        }else{
            imageIsSet = false;
            AlertDialog.Builder buider = new AlertDialog.Builder(context);
            buider.setTitle("Error en imagen");
            buider.setMessage("No Puedes Dejar La imagen Vacia");
            buider.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = buider.create();
            dialog.show();
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean checarInput(){
        if(editText.getText().toString().equals("")){
            AlertDialog.Builder buider = new AlertDialog.Builder(context);
            buider.setTitle("Error En Entrada");
            buider.setMessage("No puedes Dejar el nombre vacio");
            buider.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = buider.create();
            dialog.show();
            return true;

        }else{

            return false;
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


}