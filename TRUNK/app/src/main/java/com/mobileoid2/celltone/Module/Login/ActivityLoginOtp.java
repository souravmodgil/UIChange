package com.mobileoid2.celltone.view.activity.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;

import com.mobileoid2.celltone.R;

/**
 * A login screen that offers login via email/password.
 */
public class ActivityLoginOtp extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);


        findViewById(R.id.edittext_get_otp).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), ActivityLoginVerify.class));
                ActivityLoginOtp.this.finish();
            }
        });

    }


}

