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
import com.mobileoid2.celltone.view.fragments.ViewAllSongsFragment;
import com.mobileoid2.celltone.network.model.treadingMedia.Song;
import com.splunk.mint.Mint;

import java.util.List;

public class ViewAllSongActivity extends AppCompatActivity {

    private String type;
    private int isAudio;
    private String categoryId;
    private List<Song> songList;
    private int postion;
    private int isEdit = 0;
    private String mobileNo = "";
    private String name = "";
    private int isIncoming;
    private ContactEntity contactEntity;
    private LinearLayout llMain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_song);
        Mint.initAndStartSession(this.getApplication(), "f9ede8e8");
        type = getIntent().getStringExtra("type");
        isAudio = getIntent().getIntExtra("isAudio", 0);
        llMain = findViewById(R.id.ll_main);
        categoryId = getIntent().getStringExtra("id");
        postion = getIntent().getIntExtra("postion", 0);
        isEdit = getIntent().getIntExtra("isEdit", 0);
        mobileNo = getIntent().getStringExtra("mobile_no");
        name = getIntent().getStringExtra("contact_name");
        isIncoming = getIntent().getIntExtra("isIncoming", 0);
        contactEntity =(ContactEntity) getIntent().getSerializableExtra("contact_entity");
        songList = (List<Song>) getIntent().getSerializableExtra("songsList");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("category"));
        Fragment fragment = ViewAllSongsFragment.newInstance(this, songList, isAudio, type, categoryId, postion,
                isEdit,mobileNo,name,isIncoming,contactEntity);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.addToBackStack(fragment.getClass().getName());
        fragmentTransaction.commit();

        if(isEdit==1) {
            String msg = "";
            if(isIncoming==1)
                msg = getString(R.string.update_incoming_media_text)+name;
            else
                msg =getString(R.string.update_outgoing_media_text)+name;

            Snackbar snackbar = Snackbar.make(llMain, msg, Snackbar.LENGTH_INDEFINITE);
            View snackBarView = snackbar.getView();
            TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(
                    CoordinatorLayout.LayoutParams.WRAP_CONTENT,
                    CoordinatorLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(40, 40, 40, 40);
            params.gravity = Gravity.BOTTOM;
            snackBarView.setLayoutParams(params);
            snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
            snackbar.setText(msg);
            snackbar.show();
        }

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

}
