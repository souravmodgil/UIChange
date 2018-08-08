package com.mobileoid2.celltone.view.activity;

import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.database.ContactEntity;
import com.mobileoid2.celltone.network.model.treadingMedia.Song;
import com.mobileoid2.celltone.view.fragments.BannerSongsListFragment;


import java.util.List;

public class BannerListActivity extends AppCompatActivity implements ChangeToolBarTitleListiner {

    private String type;
    private int isAudio;
    private List<Song> songList;
    private LinearLayout llMain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_song);

        type = getIntent().getStringExtra("type");
        llMain = findViewById(R.id.ll_main);
        songList = (List<Song>) getIntent().getSerializableExtra("songsList");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        Fragment fragment = BannerSongsListFragment.newInstance(this, songList,this,getIntent().getIntExtra("isEdit", 0));

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.addToBackStack(fragment.getClass().getName());
        fragmentTransaction.commit();


    }

    public void backPress() {
        int fragments = getSupportFragmentManager().getBackStackEntryCount();
        if (fragments == 1) {
            finish();
        } else {
            if (getFragmentManager().getBackStackEntryCount() > 1) {
                getFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }

    }

    @Override
    public void onBackPressed() {
        backPress();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                backPress();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void setTitle(String text) {

    }

    @Override
    public void setTitle(String text, String songName) {
        getSupportActionBar().setTitle(text);
        getSupportActionBar().setSubtitle(songName);
    }
}


