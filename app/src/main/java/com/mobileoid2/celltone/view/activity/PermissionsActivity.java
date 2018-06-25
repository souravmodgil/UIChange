package com.mobileoid2.celltone.view.activity;

import android.app.NotificationManager;
import android.net.Uri;
import android.os.Bundle;

import com.mobileoid2.celltone.Service.ServiceCallScreenChanged;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;


public class PermissionsActivity extends Activity {



    public static final String TAG = PermissionsActivity.class.getSimpleName();

    String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.VIBRATE,
            Manifest.permission.INTERNET, Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.READ_CONTACTS,
            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(PERMISSIONS, PERMISSIONS_MULTIPLE_REQUEST);
        } else {
            requestOtherPermissionsOrLaunchMainActivity();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(PERMISSIONS, PERMISSIONS_MULTIPLE_REQUEST);
        } else {
            requestOtherPermissionsOrLaunchMainActivity();
        }

    }


    private void requestOtherPermissionsOrLaunchMainActivity() {


        startActivity(new Intent(PermissionsActivity.this, HomeActivity.class));
        PermissionsActivity.this.finish();

    }


    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermissions() {
        boolean isAllPermissionGranted = true;
        for (int i = 0; i < PERMISSIONS.length; i++) {
            System.out.println("PermissionsActivity.requestPermissions----");
            if (ContextCompat.checkSelfPermission(PermissionsActivity.this, PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(PermissionsActivity.this, PERMISSIONS[i])) {
                    isAllPermissionGranted = false;

                }
            }
        }


        if (isAllPermissionGranted) {
            // write your logic code if permission already granted
            requestOtherPermissionsOrLaunchMainActivity();
        } else {
            requestPermissions(PERMISSIONS, PERMISSIONS_MULTIPLE_REQUEST);
        }

    }

    public static final int PERMISSIONS_MULTIPLE_REQUEST = 123;

    /**
     * Callback received when a permissions request has been completed.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        /*if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                requestOtherPermissionsOrLaunchMainActivity();

            } else {
            }
        }*/


        if (requestCode == PERMISSIONS_MULTIPLE_REQUEST) {
            if (grantResults.length > 0) {


                boolean isAllPermissionGranted = true;
                for (int i = 0; i < PERMISSIONS.length; i++) {
                    if (ContextCompat.checkSelfPermission(PermissionsActivity.this, PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(PermissionsActivity.this, PERMISSIONS[i])) {
                            isAllPermissionGranted = false;
                        }
                    }
                }


                if (isAllPermissionGranted) {
                    // write your logic code if permission already granted
                    requestOtherPermissionsOrLaunchMainActivity();
                } else {
                    requestPermissions(PERMISSIONS, PERMISSIONS_MULTIPLE_REQUEST);
                }
            }
        }

    }


}
