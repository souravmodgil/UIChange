package com.mobileoid2.celltone.view.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.mobileoid2.celltone.R;
import  com.mobileoid2.celltone.view.fragments.PlanFragment;


import java.util.List;

public class PlanActivity extends AppCompatActivity implements ChangeToolBarTitleListiner {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.my_plan));
        Fragment fragment = PlanFragment.newInstance(this);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
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
    public void setTitle(String text) {
        getSupportActionBar().setTitle(text);

    }

    @Override
    public void setTitle(String text, String songName) {

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
        if( getSupportFragmentManager().getBackStackEntryCount()>=1)
            super.onBackPressed();
        else
            finish();
    }
    @Override
    public void onBackPressed() {
       onBack();
    }
}