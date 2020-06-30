package com.example.tareasmc256;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class popupDataUser extends DialogFragment {

    public popupDataUser(Context context, RoundedBitmapDrawable rd, String name){

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.popup_data_user);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        TextView user = dialog.findViewById(R.id.dialogUserName);
        ImageView image = dialog.findViewById(R.id.dialogUserImage);
        Button isOk = dialog.findViewById(R.id.dialogButtonOk);

        image.setImageDrawable(rd);
        user.setText(name);

        isOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

}
