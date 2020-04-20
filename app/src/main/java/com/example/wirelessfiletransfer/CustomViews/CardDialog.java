package com.example.wirelessfiletransfer.CustomViews;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.wirelessfiletransfer.R;


public class CardDialog {

    public static void showAlertDialog(Context context, String title, String message){
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.card_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        FrameLayout okButton = dialog.findViewById(R.id.okButton);
        TextView titleTV = dialog.findViewById(R.id.titleTV);
        TextView messageTV = dialog.findViewById(R.id.messageTV);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        titleTV.setText(title);
        messageTV.setText(message);
        dialog.show();

    }
}
