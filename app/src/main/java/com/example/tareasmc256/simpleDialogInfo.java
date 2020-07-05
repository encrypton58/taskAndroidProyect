package com.example.tareasmc256;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;


public class simpleDialogInfo extends AlertDialog.Builder {

    static boolean ifReturnAnything;

    public simpleDialogInfo(Context context, String title, String message){
        super(context);
        this.setTitle(title);
        this.setMessage(message);
        this.setCancelable(false);
        this.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog al = this.create();
        al.show();

    }
}
