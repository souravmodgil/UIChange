package com.mobileoid2.celltone.view.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.network.model.feedback.FeedBackList;
import com.mobileoid2.celltone.view.fragments.ComposeQueryFragment;
import com.mobileoid2.celltone.view.fragments.QueryReplyListFragment;

public class ComposeQueryActivity extends AppCompatActivity {
    ComposeQueryFragment composeQueryFragment ;
    private LinearLayout llSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_query);
        llSend = findViewById(R.id.ll_send);
        llSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeQueryFragment.composeQuery();
            }
        });

        setFragment(new ComposeQueryFragment());
    }
    private void setFragment(ComposeQueryFragment fragment) {
        composeQueryFragment =fragment;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();

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
        else
            finish();
    }

    @Override
    public void onBackPressed() {
        onBack();
    }


}
