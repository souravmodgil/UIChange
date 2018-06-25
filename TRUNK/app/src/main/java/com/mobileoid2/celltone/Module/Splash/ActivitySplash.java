package com.mobileoid2.celltone.Module.Splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.mobileoid2.celltone.Module.Base.ActivityBase;
import com.mobileoid2.celltone.view.activity.Login.ActivityLoginOtp;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.Util.AppSharedPref;

/**
 * Splash page
 */
public class ActivitySplash extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setScreenDimensions();

                if (!AppSharedPref.instance.getLoginState())
                    startActivity(new Intent(getApplicationContext(), ActivityLoginOtp.class));
                else
                    startActivity(new Intent(getApplicationContext(), ActivityBase.class));

            }
        }, 3000);


    }


    private void setScreenDimensions() {
        try {
            //DeviceInfo deviceInfo = new DeviceInfo(AppLevelConstraints.getAppContext());
            AppSharedPref.instance.saveScreenWidth(findViewById(R.id.root_layout).getWidth());
            // AppSharedPref.instance.saveScreenHeight(deviceInfo.getScreenHeight());
            AppSharedPref.instance.saveScreenHeight(findViewById(R.id.root_layout).getHeight());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
