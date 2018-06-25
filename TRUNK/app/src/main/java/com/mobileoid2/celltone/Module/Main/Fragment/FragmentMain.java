package com.mobileoid2.celltone.Module.Main.Fragment;


import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobileoid2.celltone.Module.Base.ActivityBase;
import com.mobileoid2.celltone.Module.DashBoard.FragmentDashBoard;
import com.mobileoid2.celltone.Module.Main.Adapter.AdapterHomeOptions;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.Util.AppUtils;
import com.mobileoid2.celltone.Util.AudioPlayer;
import com.mobileoid2.celltone.Util.VideoPlayer;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMain extends Fragment implements TabLayout.OnTabSelectedListener {


    private View view;
    private Activity activity;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AdapterHomeOptions adapter;
    private String type = "";


    public static FragmentMain newInstance(String type) {
        FragmentMain fragment = new FragmentMain();
        fragment.type = type;
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        try {
            view = inflater.inflate(R.layout.fragment_main, container, false);


            viewPager = (ViewPager) view.findViewById(R.id.pager);
            tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
            tabLayout.setTabTextColors(getResources().getColor(R.color.colorPrimaryLight), getResources().getColor(R.color.colorPrimary));
            tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimary));
            tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.audio_tab)));
            tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.video_tab)));
            tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.upload_own)));
            tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.my_profile_tab)));
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            //tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

            adapter = new AdapterHomeOptions(getChildFragmentManager());

            viewPager.setAdapter(adapter);

            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.setOnTabSelectedListener(this);

            wrapTabIndicatorToTitle(tabLayout, 50, 0);
            setTypeInterfaceTabLayout(tabLayout);

            if (type == null) type = "";
            if (type.equals("video")) viewPager.setCurrentItem(1);
            if (type.equals("upload")) viewPager.setCurrentItem(2);
            if (type.equals("profile")) viewPager.setCurrentItem(3);


        } catch (Exception e) {
            Log.e("FragmentMain", AppUtils.instance.getExceptionString(e));
        }
        return view;

    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    public void wrapTabIndicatorToTitle(TabLayout tabLayout, int externalMargin, int internalMargin) {
        View tabStrip = tabLayout.getChildAt(0);
        if (tabStrip instanceof ViewGroup) {
            ViewGroup tabStripGroup = (ViewGroup) tabStrip;
            int childCount = ((ViewGroup) tabStrip).getChildCount();
            for (int i = 0; i < childCount; i++) {
                View tabView = tabStripGroup.getChildAt(i);
                //set minimum width to 0 for instead for small texts, indicator is not wrapped as expected
                tabView.setMinimumWidth(0);
                // set padding to 0 for wrapping indicator as title
                tabView.setPadding(0, tabView.getPaddingTop(), 0, tabView.getPaddingBottom());
                // setting custom margin between tabs
                if (tabView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) tabView.getLayoutParams();
                    if (i == 0) {
                        // left
                        settingMargin(layoutParams, externalMargin, internalMargin);
                    } else if (i == childCount - 1) {
                        // right
                        settingMargin(layoutParams, internalMargin, externalMargin);
                    } else {
                        // internal
                        settingMargin(layoutParams, internalMargin, internalMargin);
                    }
                }
            }

            tabLayout.requestLayout();
        }
    }

    private void settingMargin(ViewGroup.MarginLayoutParams layoutParams, int start, int end) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.setMarginStart(start);
            layoutParams.setMarginEnd(end);
        } else {
            layoutParams.leftMargin = start;
            layoutParams.rightMargin = end;
        }
    }


    public void setTypeInterfaceTabLayout(View view) {
        if (!(view instanceof ViewGroup)) {
            return;
        }
        ViewGroup group = (ViewGroup) view;
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child instanceof TextView) {
                ((TextView) child).setTypeface(Typeface.createFromAsset(getResources().getAssets(), "fonts/eurof55.ttf"));
            } else {
                setTypeInterfaceTabLayout(child);
            }
        }
    }

}
