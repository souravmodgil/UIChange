package com.mobileoid2.celltone.view.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import com.mobileoid2.celltone.view.activity.Login.LoginOtpActivity;
import com.mobileoid2.celltone.view.activity.HomeActivity;
import com.mobileoid2.celltone.utility.SharedPrefrenceHandler;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Splash page
 */
public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //setScreenDimensions();

                if (!SharedPrefrenceHandler.getInstance().getLoginState())
                    startActivity(new Intent(getApplicationContext(), LoginOtpActivity.class));

                else {

                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }

            }
        }, 3000);


    }




}
