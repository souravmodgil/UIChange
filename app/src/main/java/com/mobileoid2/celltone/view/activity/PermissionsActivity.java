package com.mobileoid2.celltone.view.activity;

import android.app.NotificationManager;
import android.net.Uri;
import android.os.Bundle;

import com.mobileoid2.celltone.CustomWidget.TextView.ProximumBoldTextView;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.Service.ServiceCallScreenChanged;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class PermissionsActivity extends Activity {
    private RelativeLayout llLaout;

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE_NOTIFICATION = 6;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE_ACCESS = 7;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE_OVERLAY = 8138;
   private CountDownTimer toastCountDown;

    public static final String TAG = PermissionsActivity.class.getSimpleName();
    private Toast toast;

    String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.VIBRATE,
            Manifest.permission.INTERNET, Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_NOTIFICATION_POLICY
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

//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        Boolean isNotificationAccess = false;
//
//        for (String service : NotificationManagerCompat.getEnabledListenerPackages(this)) {
//            if (service.equals(getPackageName()))
//                isNotificationAccess = true;
//
//
//        }
//
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(PermissionsActivity.this)) {
//            if(toast!=null)
//                toast.cancel();
//            if(toastCountDown!=null)
//                toastCountDown.cancel();
//            showToast("Enable Draw Over Other App to Use Kolbeat");


//
//            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + this.getPackageName()));
//            startActivityForResult(intent, REQUEST_PERMISSIONS_REQUEST_CODE_OVERLAY);
//        }
////        else if (!isAccessibilitySettingsOn(getApplicationContext())) {
////            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
////            startActivityForResult(intent, REQUEST_PERMISSIONS_REQUEST_CODE_ACCESS);
////
////        }
//        else if (!isNotificationAccess)
//
//
//        {
//         if(toastCountDown!=null)
//             toastCountDown.cancel();
//            showToast("Enable Notification Access to Use Kolbeat");
//            startActivity(new Intent(
//                    "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
//        } else {
//            if(toast!=null)
//                toast.cancel();
//            if(toastCountDown!=null)
//                toastCountDown.cancel();
//            startActivity(new Intent(PermissionsActivity.this, HomeActivity.class));
//            PermissionsActivity.this.finish();
//        }
    }


    @Override
    public void onBackPressed() {
//        toast.cancel();
        super.onBackPressed();
    }

    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + ServiceCallScreenChanged.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.e(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.e(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.e(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.e(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.e(TAG, "***ACCESSIBILITY IS DISABLED***");
        }

        return false;
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
        } else if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE_OVERLAY) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                assert notificationManager != null;
                if (!notificationManager.isNotificationPolicyAccessGranted()) {
                    Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    startActivityForResult(intent, REQUEST_PERMISSIONS_REQUEST_CODE_NOTIFICATION);
                }
            }
        }
// else if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE_ACCESS) {
//            if (!isAccessibilitySettingsOn(getApplicationContext())) {
//                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
//                startActivityForResult(intent, REQUEST_PERMISSIONS_REQUEST_CODE_ACCESS);
//            }


        //}
        else if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE_OVERLAY) {
                if(toast!=null)
                    toast.cancel();
            if(toastCountDown!=null)
            toastCountDown.cancel();
            startActivity(new Intent(PermissionsActivity.this, HomeActivity.class));
            PermissionsActivity.this.finish();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
            if(toast!=null)
                toast.cancel();
        if(toastCountDown!=null)
            toastCountDown.cancel();
    }
}
