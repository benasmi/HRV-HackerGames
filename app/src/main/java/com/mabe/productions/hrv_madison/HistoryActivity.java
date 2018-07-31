package com.mabe.productions.hrv_madison;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class HistoryActivity extends AppCompatActivity {

    private View img_back_arrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initialiseViews();

    }

    private void initialiseViews(){

        img_back_arrow = (ImageView) findViewById(R.id.img_back_arrow);
        img_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HistoryActivity.this.finish();
            }
        });
    }

}
