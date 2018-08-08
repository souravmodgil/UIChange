package com.mobileoid2.celltone.view.activity;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
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
import com.mobileoid2.celltone.view.fragments.ProfileFragment;


import java.util.List;

import static com.mobileoid2.celltone.view.activity.Login.LoginVerifyActivity.PERMISSION_READ_STATE;

public class ProfileActivity extends AppCompatActivity implements ChangeToolBarTitleListiner {
    public static final int PERMISSIONS_MULTIPLE_REQUEST = 123;
    String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // toolbar fancy stuff
        if (checkPremissaion() == 1)
            setProfileFragment();

    }

    private void setProfileFragment() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
     //   getSupportActionBar().setTitle("My Profile");
        getSupportActionBar().setTitle(getString(R.string.my_profile));
        Fragment fragment = ProfileFragment.newInstance(this);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commitAllowingStateLoss();

    }

    private int checkPremissaion() {
        int status = 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                status = 0;
                requestPermissions(PERMISSIONS, PERMISSIONS_MULTIPLE_REQUEST);
            }


        }
        return status;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSIONS_MULTIPLE_REQUEST) {
            if (grantResults.length > 0) {


                boolean isAllPermissionGranted = true;
                for (int i = 0; i < PERMISSIONS.length; i++) {
                    if (ContextCompat.checkSelfPermission(ProfileActivity.this, PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this, PERMISSIONS[i])) {
                            isAllPermissionGranted = false;
                        }
                    }
                }
                if (isAllPermissionGranted)
                    setProfileFragment();
            }
        }


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
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (getSupportFragmentManager().getBackStackEntryCount() >= 1)
            super.onBackPressed();
        else
            finish();
    }

    @Override
    public void onBackPressed() {

        onBack();
    }

    @Override
    public void setTitle(String text) {
        getSupportActionBar().setTitle(text);
    }

    @Override
    public void setTitle(String text, String songName) {

    }
}
