package com.mabe.productions.hrv_madison;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;

public class CustomLoadingDialog extends Dialog {

    private String message;

    public CustomLoadingDialog(@NonNull Context context, String message) {
        super(context);
        this.message = message;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        setContentView(R.layout.custom_loading_dialog);

        TextView loading_text = findViewById(R.id.loading_text);
        loading_text.setText(message);

        setCancelable(false);
    }
}
