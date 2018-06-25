package com.mobileoid2.celltone.Module.DashBoard;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobileoid2.celltone.Module.Base.ActivityBase;
import com.mobileoid2.celltone.Module.Music.Fragment.FragmentMusicUpload;
import com.mobileoid2.celltone.Module.Music.Fragment.FragmentSongsGerne;
import com.mobileoid2.celltone.Module.Music.Fragment.FragmentVideo;
import com.mobileoid2.celltone.Module.Profile.FragmentMyProfile;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.Util.AppUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDashBoard extends Fragment {


    View view;
    Activity activity;

    public static FragmentDashBoard newInstance() {
        FragmentDashBoard fragment = new FragmentDashBoard();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {
            view = inflater.inflate(R.layout.fragment_dash_board, container, false);

            view.findViewById(R.id.card_view_songs).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ActivityBase) activity).launchDoctorFragmentByReplacing(FragmentSongsGerne.newInstance(), FragmentSongsGerne.class.toString());
                }
            });


            view.findViewById(R.id.card_view_videos).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ActivityBase) activity).launchDoctorFragmentByReplacing(FragmentVideo.newInstance(null, null), FragmentVideo.class.toString());
                }
            });


            view.findViewById(R.id.card_view_upload).setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(View v) {
                    ((ActivityBase) activity).launchDoctorFragmentByReplacing(FragmentMusicUpload.newInstance(), FragmentMusicUpload.class.toString());
                }
            });

            view.findViewById(R.id.card_view_profile).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ActivityBase) activity).launchDoctorFragmentByReplacing(FragmentMyProfile.newInstance(), FragmentMyProfile.class.toString());
                }
            });

        } catch (Exception e) {
            Log.e("Dashboard", AppUtils.instance.getExceptionString(e));
        }
        return view;
    }

}
