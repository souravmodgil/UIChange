package com.mobileoid2.celltone.Module.Main.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mobileoid2.celltone.Module.Music.Fragment.FragmentSongs;
import com.mobileoid2.celltone.Module.Music.Fragment.FragmentMusicUpload;
import com.mobileoid2.celltone.Module.Music.Fragment.FragmentVideo;
import com.mobileoid2.celltone.Module.Profile.FragmentMyProfile;

/**
 * Created by mobileoid2 on 9/11/17.
 */

public class AdapterHomeOptions extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    int tabCount;

    //Constructor to the class
    public AdapterHomeOptions(FragmentManager fm) {
        super(fm);
        //Initializing tab count

    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new FragmentSongs();
            case 1:
                return new FragmentVideo();
            case 2:
                return new FragmentMusicUpload();
            case 3:
                return new FragmentMyProfile();
        }
        return null;
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return 4;
    }

}


