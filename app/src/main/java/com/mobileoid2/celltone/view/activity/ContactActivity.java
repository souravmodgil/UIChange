package com.mobileoid2.celltone.view.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.network.model.treadingMedia.Song;
import com.mobileoid2.celltone.view.fragments.ContactsFragment;
import com.mobileoid2.celltone.view.fragments.OfferFragment;


public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.contact_list));
        Intent intent = getIntent();
        Fragment fragment = ContactsFragment.newInstance((Song) intent.getSerializableExtra("song"), intent.getIntExtra("isOutgoing", 0),
                intent.getIntExtra("isAudio", 0), intent.getIntExtra("isEdit", 0));
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.addToBackStack(fragment.getClass().getName()).commitAllowingStateLoss();

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
        if (getSupportFragmentManager().getBackStackEntryCount() > 1)
            super.onBackPressed();
        else
            finish();
    }

    @Override
    public void onBackPressed() {
        onBack();
    }
}
