package com.mobileoid2.celltone.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.view.activity.Login.LoginOtpActivity;
import com.mobileoid2.celltone.view.adapter.CustomPagerAdapter;

public class Tutorial extends AppCompatActivity {
    private ViewPager tutPager;
    private AppCompatButton buttonSkip;
    private  CustomPagerAdapter mCustomPagerAdapter;
    int[] mResources = {
            R.drawable.tut1,
            R.drawable.tut2,
            R.drawable.tut3,
            R.drawable.tut4,
            R.drawable.tut5,

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        tutPager = findViewById(R.id.tut_pager);
        buttonSkip = findViewById(R.id.button_skip);
        mCustomPagerAdapter = new CustomPagerAdapter(this,mResources);
        tutPager.setAdapter(mCustomPagerAdapter);
        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginOtpActivity.class));


            }
        });



    }

}
