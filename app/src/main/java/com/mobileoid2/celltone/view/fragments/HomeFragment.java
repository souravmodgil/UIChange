package com.mobileoid2.celltone.view.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.gson.Gson;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.Util.CelltoneApplication;
import com.mobileoid2.celltone.celltoneDB.CellToneRoomDatabase;
import com.mobileoid2.celltone.database.ContactEntity;
import com.mobileoid2.celltone.network.APIClient;
import com.mobileoid2.celltone.network.ApiInterface;
import com.mobileoid2.celltone.network.ApiConstant;
import com.mobileoid2.celltone.network.NetworkCallBack;
import com.mobileoid2.celltone.network.SendRequest;
import com.mobileoid2.celltone.network.jsonparsing.JsonResponse;
import com.mobileoid2.celltone.network.model.banner.BannerBody;
import com.mobileoid2.celltone.network.model.banner.BannerModel;
import com.mobileoid2.celltone.network.model.banner.Medium;
import com.mobileoid2.celltone.network.model.treadingMedia.Category;
import com.mobileoid2.celltone.network.model.treadingMedia.MediaModel;
import com.mobileoid2.celltone.network.model.treadingMedia.Song;
import com.mobileoid2.celltone.pojo.audio.PojoGETALLMEDIA_Request;
import com.mobileoid2.celltone.pojo.mediapojo.CategoriesSongs;
import com.mobileoid2.celltone.pojo.mediapojo.MediaPojo;
import com.mobileoid2.celltone.utility.Config_URL;
import com.mobileoid2.celltone.utility.SharedPrefrenceHandler;
import com.mobileoid2.celltone.view.activity.BannerListActivity;
import com.mobileoid2.celltone.view.activity.ViewAllSongActivity;
import com.mobileoid2.celltone.view.listener.NavigationLisitner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements NetworkCallBack, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private ApiInterface apiInterface;
    private int noOfAPiHint = 0;
    private int totalApiToBeHint = 0;
    private String audioResponse = "";
    private String videoResponse = "";
    private ImageView mtNav, mtMenu;
    private ProgressBar loadingSpinner;
    private NavigationLisitner navigationLisitner;
    private int isEdit = 0;
    private String mobileNo = "";
    private String name = "";
    private int isIncoming = -1;
    private ContactEntity contactEntity;
    private SliderLayout mDemoSlider;
    private List<BannerBody> bannerBodyList;

    public static final String TAG = HomeFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(NavigationLisitner navigationLisitner, int isEdit, String mobileNo, String name, int isIncoming, ContactEntity contactEntity) {
        HomeFragment fragment = new HomeFragment();
        fragment.navigationLisitner = navigationLisitner;
        fragment.isEdit = isEdit;
        fragment.mobileNo = mobileNo;
        fragment.name = name;
        fragment.isIncoming = isIncoming;
        fragment.contactEntity = contactEntity;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            /*mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);*/
        }

        System.out.println("HomeFragment.onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = getView() != null ? getView() : inflater.inflate(R.layout.fragment_home, container, false);
        loadingSpinner = view.findViewById(R.id.loading_spinner);
        apiInterface = ((ApiInterface) APIClient.getClient().create(ApiInterface.class));
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        mtNav = view.findViewById(R.id.mt_nav);
        mtMenu = view.findViewById(R.id.mt_menu);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF"));
        tabLayout.setSelectedTabIndicatorHeight((int) (2 * getResources().getDisplayMetrics().density));
        //    tabLayout.setTabTextColors(Color.parseColor("#adadad"), Color.parseColor("#000000"));


        tabLayout.setupWithViewPager(viewPager);
        setCustomFont();

        mDemoSlider = (SliderLayout) view.findViewById(R.id.slider);
        mtNav.setOnClickListener(this);
        mtMenu.setOnClickListener(this);


        return view;
    }

    public void setCustomFont() {

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();

        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);

            int tabChildsCount = vgTab.getChildCount();

            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    //Put your font in assests folder
                    //assign name of the font here (Must be case sensitive)
                    ((TextView) tabViewChild).setTypeface(Typeface.createFromAsset(getActivity().getAssets(),
                            "fonts/ProximaNova-Regular.otf"));
                }
            }
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        /*if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }*/
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("HomeFragment.onResume");
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("HomeFragment.onStart");
    }

    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
      /*  System.out.println("HomeFragment.onViewCreated"+viewPager);



    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        System.out.println("HomeFragment.onAttach");
       /* if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }

    private void parseAudio(MediaModel model) {

    }

    @Override
    public void getResponse(JsonResponse response, int type) {

        if (response != null && response.getObject() != null && isAdded() && getActivity() != null) {
            this.noOfAPiHint += 1;
            switch (type) {

                case ApiConstant.AUDIOAPI:
                    SharedPrefrenceHandler.getInstance().setAudioResponse(response.getObject());

                    break;
                case ApiConstant.VIDEOAPI:
                    SharedPrefrenceHandler.getInstance().setVedioResponse(response.getObject());
                    break;
                case ApiConstant.BANNER_API:
                    SharedPrefrenceHandler.getInstance().setBannerResponse(response.getObject());
                    break;


            }
            if (noOfAPiHint == 3) {
                setViewPager();
                loadingSpinner.setVisibility(View.GONE);
            }


        } else
            loadingSpinner.setVisibility(View.GONE);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mt_menu:
                PopupMenu popup = new PopupMenu(getActivity(), v);

                /** Adding menu items to the popumenu */
                popup.getMenuInflater().inflate(R.menu.home_menu, popup.getMenu());

                /** Defining menu item click listener for the popup menu */
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        //Toast.makeText(mcontext, "You selected the action : " + item.getTitle(), Toast.LENGTH_SHORT).show();

                        switch (item.getItemId()) {
                            case R.id.nav_refresh:
                                navigationLisitner.onMenuClick();

                                break;

                        }
                        return true;
                    }
                });
                /** Showing the popup menu */
                popup.show();
                break;
            case R.id.mt_nav:
                navigationLisitner.setNavigation();
                break;
        }

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /*
     *
     * */

    private void setupViewPager(ViewPager viewPager) {
        totalApiToBeHint = 2;


        if (!SharedPrefrenceHandler.getInstance().getAudioRespose().isEmpty() &&
                !SharedPrefrenceHandler.getInstance().getVideoRespose().isEmpty()) {
            setViewPager();
            getALLAUDIO();


        } else {

            getALLAUDIO();


        }
    }

    private List<BannerBody> parseBaners(String response) {


        Gson gsonObj = new Gson();
        BannerModel mediaModel = gsonObj.fromJson(response, BannerModel.class);

        return mediaModel.getBody();

    }

    private List<Category> parseaudio(String response) {
        List<Category> categoriesLsit = new ArrayList<>();

        if (!response.isEmpty()) {
            Gson gsonObj = new Gson();
            MediaModel mediaModel = gsonObj.fromJson(response, MediaModel.class);
            if (mediaModel.getStatus() != 1000) {

                return categoriesLsit;
            }
            int length = mediaModel.getBody().getTrending().size();

            for (int i = 0; i < length; i++) {
                if (mediaModel.getBody().getTrending().get(i).getSongs() != null &&
                        mediaModel.getBody().getTrending().get(i).getSongs().size() > 0) {
                    Category category = new Category();
                    category.setType("trending");
                    category.setTitle(mediaModel.getBody().getTrending().get(i).getTitle());
                    category.setId(mediaModel.getBody().getTrending().get(i).getId());
                    category.setSongs(mediaModel.getBody().getTrending().get(i).getSongs());
                    categoriesLsit.add(category);
                }


            }
            length = mediaModel.getBody().getCategory().size();

            for (int i = 0; i < length; i++) {
                if (mediaModel.getBody().getCategory().get(i).getSongs() != null &&
                        mediaModel.getBody().getCategory().get(i).getSongs().size() > 0) {
                    Category category = new Category();
                    category.setType("category");
                    category.setTitle(mediaModel.getBody().getCategory().get(i).getTitle());
                    category.setId(mediaModel.getBody().getCategory().get(i).getId());
                    category.setSongs(mediaModel.getBody().getCategory().get(i).getSongs());
                    categoriesLsit.add(category);
                }
            }


        }
        return categoriesLsit;
    }

    private List<CategoriesSongs> setCategoriesList(int length, List<Song> songs) {
        List<CategoriesSongs> categoriesSongs = new ArrayList<>();
        for (int j = 0; j < length; j++) {
            CategoriesSongs song = new CategoriesSongs();
            song.setTitle(songs.get(j).getTitle());
            song.setArtistName(songs.get(j).getArtistName());
            song.setClipArtUrl(songs.get(j).getClipArtUrl());
            song.setOriginalFileUrl(songs.get(j).getOriginalFileUrl());
            song.setSampleFileUrl(songs.get(j).getSampleFileUrl());
            song.setContentType(songs.get(j).getContentType());
            categoriesSongs.add(song);


        }
        return categoriesSongs;

    }

    private void setViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());


        new AsyncTask<Void, Void, List<MediaPojo>>() {
            @Override
            protected List<MediaPojo> doInBackground(Void... voids) {

                List<MediaPojo> list = new ArrayList<>();
                MediaPojo mediaPojo = new MediaPojo();
                mediaPojo.setCategoryList(parseaudio(SharedPrefrenceHandler.getInstance().getAudioRespose()));
                list.add(mediaPojo);
                mediaPojo = new MediaPojo();
                mediaPojo.setCategoryList(parseaudio(SharedPrefrenceHandler.getInstance().getVideoRespose()));
                list.add(mediaPojo);
                bannerBodyList = parseBaners(SharedPrefrenceHandler.getInstance().getBannerResponse());


                return list;
            }

            @Override
            protected void onPostExecute(List<MediaPojo> lists) {
                super.onPostExecute(lists);
                int isAudio = 1;
                try {

                    mDemoSlider.removeAllSliders();
                } catch (Exception ex) {

                }


                for (BannerBody bannerBody : bannerBodyList) {
                    TextSliderView textSliderView = new TextSliderView(getContext());
                    // initialize a SliderLayout
                    textSliderView.description(bannerBody.getTitle()).image(ApiConstant.MEDIA_URL + bannerBody.getBanner()).setScaleType(BaseSliderView.ScaleType.Fit).setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(BaseSliderView slider) {

                        }
                    });
                    //add your extra information
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle().putString("extra", name);

                    textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(BaseSliderView slider) {
                            ArrayList<Song> songList = new ArrayList<>();
                            for (Medium medium : bannerBody.getMedia()) {
                                Song song = new Song();
                                song.setClipArtUrl(medium.getClipArtUrl());
                                song.setTitle(medium.getTitle());
                                song.setArtistName("");
                                song.setSampleFileUrl(medium.getSampleFileUrl());
                                song.setOriginalFileUrl(medium.getOriginalFileUrl());
                                if (medium.getContentType().equals("video"))
                                    song.setIsAudio(0);
                                else
                                    song.setIsAudio(1);
                                songList.add(song);


                            }

                            Intent intent = new Intent(getActivity(), BannerListActivity.class);
                            intent.putExtra("songsList", songList);
                            startActivity(intent);


                            //    Toast.makeText(getActivity(), "ONClick:" + mDemoSlider.getCurrentPosition(), Toast.LENGTH_LONG).show();
                        }
                    });

                    mDemoSlider.addSlider(textSliderView);

                }
                mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
                mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                mDemoSlider.setCustomAnimation(new DescriptionAnimation());
                mDemoSlider.setDuration(5000);


                if (lists != null && lists.size() >= 2) {
                    adapter.addFragment(HomeVideoFragment.newInstance(getContext(), lists.get(0).getCategoryList(), 1, isEdit, mobileNo, name, isIncoming, contactEntity), getString(R.string.audio));
                    adapter.addFragment(HomeVideoFragment.newInstance(getContext(), lists.get(1).getCategoryList(), 0, isEdit, mobileNo, name, isIncoming, contactEntity), getString(R.string.video));
                    viewPager.setAdapter(adapter);
                }
            }
        }.execute();


    }

    private void getALLAUDIO() {
        totalApiToBeHint = 3;
        SendRequest.sendRequest(ApiConstant.AUDIOAPI, apiInterface.getAllAudio(SharedPrefrenceHandler.getInstance().getUSER_TOKEN()), this);
        SendRequest.sendRequest(ApiConstant.VIDEOAPI, apiInterface.getAllVideo(SharedPrefrenceHandler.getInstance().getUSER_TOKEN()), this);
        SendRequest.sendRequest(ApiConstant.BANNER_API, apiInterface.getAllBannes(SharedPrefrenceHandler.getInstance().getUSER_TOKEN()), this);


    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}
