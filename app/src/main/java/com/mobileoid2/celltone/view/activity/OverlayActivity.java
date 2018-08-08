package com.mobileoid2.celltone.view.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobileoid2.celltone.R;

public class OverlayActivity extends AppCompatActivity {
    private RelativeLayout llLaout;
    private TextView tostText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overlay);
        llLaout = findViewById(R.id.ll_laout);
        tostText =findViewById(R.id.tost_text);
        tostText.setText(getIntent().getStringExtra("overlay_text"));


        llLaout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
    }
}
