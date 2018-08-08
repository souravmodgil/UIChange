package com.mobileoid2.celltone.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.view.fragments.FragmentMusicUpload;


import java.util.List;

public class UploadActivity extends AppCompatActivity {

    String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.VIBRATE, Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.MODIFY_AUDIO_SETTINGS};

    public static final int PERMISSIONS_MULTIPLE_REQUEST = 123;
    public static   int isPopupVisible =0 ;
    FragmentMusicUpload fragmentMusicUpload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        if ( Build.VERSION.SDK_INT >= 23 )
        {
               if( ContextCompat.checkSelfPermission( this, android.Manifest.permission.READ_EXTERNAL_STORAGE )
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.MODIFY_AUDIO_SETTINGS )
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.MODIFY_AUDIO_SETTINGS )
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.RECORD_AUDIO )
                        != PackageManager.PERMISSION_GRANTED ||

                ContextCompat.checkSelfPermission( this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(PERMISSIONS, PERMISSIONS_MULTIPLE_REQUEST);
               else
                   setView();

        }
        else
            setView();

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.upload));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                boolean isAllPermissionGranted = true;
                for (int i = 0; i < PERMISSIONS.length; i++) {
                    System.out.println("PermissionsActivity.requestPermissions----");
                    if (ContextCompat.checkSelfPermission(this, PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                        isAllPermissionGranted =false;
                    }
                }

                if (isAllPermissionGranted)
                    // write your logic code if permission already granted
                    setView();

                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermissions() {
        boolean isAllPermissionGranted = true;
        for (int i = 0; i < PERMISSIONS.length; i++) {
            System.out.println("PermissionsActivity.requestPermissions----");
            if (ContextCompat.checkSelfPermission(this, PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                isAllPermissionGranted =false;
            }
        }


        if (isAllPermissionGranted)
            // write your logic code if permission already granted
           setView();

    }
    private void setView()

    {
        int isRecord = getIntent().getIntExtra("isRecord",0);
        String fileName = getIntent().getStringExtra("filePath");
        fragmentMusicUpload = FragmentMusicUpload.newInstance(getIntent().getIntExtra("isAudio",0),isRecord,fileName);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragmentMusicUpload);
        fragmentTransaction.commit();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBack();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onBack()

    {
        if (getSupportFragmentManager().getBackStackEntryCount() >= 1)
            super.onBackPressed();
        else {
            if(isPopupVisible==1)
            {
                isPopupVisible=0;
                fragmentMusicUpload.hidePopup();

            }
                else
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        onBack();
    }


}
