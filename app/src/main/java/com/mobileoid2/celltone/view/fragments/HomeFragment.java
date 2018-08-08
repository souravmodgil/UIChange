package com.mobileoid2.celltone.view.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.mobileoid2.celltone.CustomWidget.Dialog.CustomDialogUploadOwn;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.Service.ServiceCallScreenChanged;
import com.mobileoid2.celltone.Util.CelltoneApplication;
import com.mobileoid2.celltone.Util.RealPathUtil;
import com.mobileoid2.celltone.celltoneDB.CellToneRoomDatabase;
import com.mobileoid2.celltone.database.AppDatabase;
import com.mobileoid2.celltone.database.ContactEntity;
import com.mobileoid2.celltone.database.RingtoneEntity;
import com.mobileoid2.celltone.network.APIClient;
import com.mobileoid2.celltone.network.ApiInterface;
import com.mobileoid2.celltone.network.ApiConstant;
import com.mobileoid2.celltone.network.NetworkCallBack;
import com.mobileoid2.celltone.network.SendRequest;
import com.mobileoid2.celltone.network.jsonparsing.JsonResponse;
import com.mobileoid2.celltone.network.model.banner.BannerBody;
import com.mobileoid2.celltone.network.model.banner.BannerModel;
import com.mobileoid2.celltone.network.model.banner.Medium;
import com.mobileoid2.celltone.network.model.contacts.ContactsMedia;
import com.mobileoid2.celltone.network.model.contacts.Incommingother;
import com.mobileoid2.celltone.network.model.treadingMedia.Category;
import com.mobileoid2.celltone.network.model.treadingMedia.MediaModel;
import com.mobileoid2.celltone.network.model.treadingMedia.OwnMedium;
import com.mobileoid2.celltone.network.model.treadingMedia.Song;
import com.mobileoid2.celltone.pojo.audio.PojoGETALLMEDIA_Request;
import com.mobileoid2.celltone.pojo.getmedia.Outgoing;
import com.mobileoid2.celltone.pojo.mediapojo.CategoriesSongs;
import com.mobileoid2.celltone.pojo.mediapojo.MediaPojo;
import com.mobileoid2.celltone.utility.Config_URL;
import com.mobileoid2.celltone.utility.ContactFetcher;
import com.mobileoid2.celltone.utility.SharedPrefrenceHandler;
import com.mobileoid2.celltone.utility.Utils;
import com.mobileoid2.celltone.view.activity.BannerListActivity;
import com.mobileoid2.celltone.view.activity.HomeActivity;
import com.mobileoid2.celltone.view.activity.Login.LoginOtpActivity;
import com.mobileoid2.celltone.view.activity.MainActivity;
import com.mobileoid2.celltone.view.activity.OverlayActivity;
import com.mobileoid2.celltone.view.activity.SearchActivity;
import com.mobileoid2.celltone.view.activity.UploadActivity;
import com.mobileoid2.celltone.view.activity.ViewAllSongActivity;
import com.mobileoid2.celltone.view.adapter.MyContactsRecyclerViewAdapter;
import com.mobileoid2.celltone.view.listener.NavigationLisitner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    private ApiInterface apiInterface;
    private int noOfAPiHint = 0;
    private int isAudio = 1;
    private String filemanagerstring;
    private AppDatabase appDatabase;
    private ImageView mtNav, mtSearch;
    private RelativeLayout tutLayout;
    private EditText mtEditText;
    private Button buttonSkip;
    private String firsItem = "";
    String secondItem = "Choose from Gallery";
    private final int REQUEST_TAKE_GALLERY_VIDEO = 1000;
    private final int REQUEST_TAKE_GALLERY_AUDIO = 1001;
    String cancel = "Cancel";
    private ImageView mtClear;
    private FloatingActionButton fab;
    TextView mtPlaceholder;
    private ProgressBar loadingSpinner;
    private NavigationLisitner navigationLisitner;
    private int isEdit = 0;
    private String mobileNo = "";
    private String name = "";
    private int isIncoming = -1;
    private ContactEntity contactEntity;
    private SliderLayout mDemoSlider;
    private List<BannerBody> bannerBodyList;
    private int totalApiToBeHint;
    private ContactsMedia contactsOutgoingMedia;
    private Utils utils;

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
    public static HomeFragment newInstance(NavigationLisitner navigationLisitner, int isEdit, String mobileNo,
                                           String name,
                                           int isIncoming, ContactEntity contactEntity) {
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

    private void getContact() {
        // SendRequest.sendRequest(Config_URL.);
        SendRequest.sendRequest(ApiConstant.GET_ALL_CONTACT, apiInterface.getAllContatcs(SharedPrefrenceHandler.getInstance().getUSER_TOKEN()), this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = getView() != null ? getView() : inflater.inflate(R.layout.fragment_home, container, false);

        loadingSpinner = view.findViewById(R.id.loading_spinner);
        apiInterface = APIClient.getClient().create(ApiInterface.class);
        tutLayout = view.findViewById(R.id.tut_layout);

        buttonSkip = view.findViewById(R.id.button_skip);
        viewPager = view.findViewById(R.id.viewpager);
        fab = view.findViewById(R.id.fab);
        mtPlaceholder = view.findViewById(R.id.mt_placeholder);
        setupViewPager(viewPager);
        utils = new Utils();
        appDatabase = AppDatabase.getAppDatabase(getActivity());
        mtNav = view.findViewById(R.id.mt_nav);
        mtSearch = view.findViewById(R.id.mt_search);
        mtEditText = view.findViewById(R.id.mt_editText);
        mtClear = view.findViewById(R.id.mt_clear);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF"));
        tabLayout.setSelectedTabIndicatorHeight((int) (2 * getResources().getDisplayMetrics().density));
        //    tabLayout.setTabTextColors(Color.parseColor("#adadad"), Color.parseColor("#000000"));


        tabLayout.setupWithViewPager(viewPager);
        setCustomFont();
        getContact();
        mDemoSlider = view.findViewById(R.id.slider);
        //   mtNav.setOnClickListener(this);
        mtNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationLisitner.setNavigation();
            }
        });
        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tutLayout.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
                checkPremission();
            }
        });
        mtClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideCancelIcon();
                mtEditText.setText("");
                hideKeyBoard();
            }
        });
        mtEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCancelIcon();
            }
        });
        mtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mtEditText.length() > 0) {
                    Intent intent = new Intent(getActivity(), SearchActivity.class);
                    intent.putExtra("isEdit", isEdit);
                    intent.putExtra("mobile_no", mobileNo);
                    intent.putExtra("contact_name", name);
                    intent.putExtra("isIncoming", isIncoming);
                    intent.putExtra("terms", mtEditText.getText().toString());
                    intent.putExtra("contact_entity", contactEntity);
                    startActivity(intent);
                }

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertBox();
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                // Check if this is the page you want.
                if (position == 0)
                    isAudio = 1;
                else
                    isAudio = 0;

            }
        });
        mtEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    showCancelIcon();
                } else {
                    hideCancelIcon();
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return view;
    }

    private void showAlertBox() {

//
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_dailog_chooser);
        LinearLayout firstLayout = dialog.findViewById(R.id.ll_first);
        LinearLayout secondLayout = dialog.findViewById(R.id.second_layout);
        TextView txtSecondItem = dialog.findViewById(R.id.txt_second_item);
        TextView txtFirstItem = dialog.findViewById(R.id.txt_first_item);
        TextView txtClose = dialog.findViewById(R.id.txt_close);
        ImageView imageViewFirst = dialog.findViewById(R.id.first_image_view);
        if (isAudio == 0) {
//            title = "Add video!";
            txtFirstItem.setText("Upload Video from Vidoe Camera");
            imageViewFirst.setImageResource(R.drawable.ic_menu_camera);
            txtSecondItem.setText("Choose from Gallery");
            secondLayout.setVisibility(View.VISIBLE);

        } else {
//            title = "Add Audio!";
            txtFirstItem.setText("Upload Audio from Audio Recording");
            imageViewFirst.setImageResource(R.drawable.audio_icon);
            secondLayout.setVisibility(View.GONE);


        }
        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.setCancelable(false);

        secondLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAudio == 0)
                    videoIntent("video/*", REQUEST_TAKE_GALLERY_VIDEO);
//                else
//                    videoIntent("audio/*", REQUEST_TAKE_GALLERY_AUDIO);
                dialog.dismiss();

            }
        });
        firstLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UploadActivity.class);
                intent.putExtra("isAudio", isAudio);
                intent.putExtra("isRecord", 1);
                intent.putExtra("filePath", "");
                startActivity(intent);
                dialog.dismiss();

            }
        });
        dialog.show();


    }

    private void videoIntent(String type, int flag) {
        Intent intent = new
                Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        //Intent(Intent.ACTION_PICK,MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType(type);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), flag);

    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getActivity().getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(getActivity());
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showCancelIcon() {
        mtClear.setVisibility(View.VISIBLE);
        mtNav.setVisibility(View.GONE);


    }

    private void hideCancelIcon() {
        mtClear.setVisibility(View.GONE);
        mtNav.setVisibility(View.VISIBLE);
        mtPlaceholder.setVisibility(View.VISIBLE);
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
                    ((TextView) tabViewChild).setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

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
        checkPremission();
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_TAKE_GALLERY_VIDEO || requestCode == REQUEST_TAKE_GALLERY_AUDIO) {
//                Uri selectedVieo = data.getData();
//                String[] filePathColumn = { MediaStore.Video.Media.DATA };
//
//
//                Cursor cursor = getActivity().managedQuery(selectedVieo, filePathColumn, null, null, null);
//                if(cursor!=null) {
//                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
//                    cursor.moveToFirst();
//                    filePath =  cursor.getString(column_index);
//                }

                String realPath;
                // SDK < API11
                String filePath = "";
                if (Build.VERSION.SDK_INT < 11)
                    realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(getContext(), data.getData());

                    // SDK >= 11 && SDK < 19
                else if (Build.VERSION.SDK_INT < 19)
                    realPath = RealPathUtil.getRealPathFromURI_API11to18(getContext(), data.getData());

                    // SDK > 19 (Android 4.4)
                else
                    realPath = RealPathUtil.getRealPathFromURI_API19(getContext(), data.getData());


                System.out.println("HomeCreateEventsFragment.onActivityResult" + realPath);
                filePath = realPath;

                try {
                    if (filePath != null) {

                        MediaPlayer mp = MediaPlayer.create(getActivity(), Uri.parse(filePath));
                        int duration = mp.getDuration();
                        mp.release();

//                        if((duration/1000) > 30){
//                            Toast.makeText(getActivity(),"The vidoe you selected has duration more than 30 sec ,please select other video whose duration is less than 30 sec",Toast.LENGTH_LONG).show();
//                        }else{
                        Intent intent = new Intent(getActivity(), UploadActivity.class);
                        intent.putExtra("isAudio", isAudio);
                        intent.putExtra("isRecord", 0);
                        intent.putExtra("filePath", filePath);
                        startActivity(intent);
                        // }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    // UPDATED!
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }


    private void parseContacts(String response) {
        Map<String, String> contactMap = new ContactFetcher(getActivity()).fetchAllContact();

        new AsyncTask<Void, Void, List<ContactEntity>>() {
            @Override
            protected List<ContactEntity> doInBackground(Void... voids) {
                List<ContactEntity> list = new ArrayList<>();


                if (!response.isEmpty()) {
                    Gson gsonObj = new Gson();
                    contactsOutgoingMedia = gsonObj.fromJson(response, ContactsMedia.class);
                    list = utils.getContactList(contactsOutgoingMedia, contactMap, appDatabase);
                    //Collections.sort(list, ContactsComparator);
                }
                return list;
            }

            @Override
            protected void onPostExecute(List<ContactEntity> contacts) {
                super.onPostExecute(contacts);
                utils.downloadOutgoingFiles(getActivity(), contactsOutgoingMedia.getBody());


            }
        }.execute();
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
                case ApiConstant.GET_ALL_CONTACT:
                    parseContacts(response.getObject());
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
            length = mediaModel.getBody().getOwnMedia().size();
            List<Song> songList = new ArrayList<>();

            for (int i = 0; i < length; i++) {
                if (mediaModel.getBody().getOwnMedia() != null &&
                        mediaModel.getBody().getOwnMedia().size() > 0) {
                    Song song = new Song();
                    OwnMedium ownMedium = mediaModel.getBody().getOwnMedia().get(i);
                    song.setTitle(ownMedium.getTitle());
                    song.setArtistName(SharedPrefrenceHandler.getInstance().getName());
                    song.setOriginalFileUrl(ownMedium.getOriginalFileUrl());
                    song.setSampleFileUrl(ownMedium.getSampleFileUrl());
                    songList.add(song);

                }
            }
            if (songList.size() > 0) {
                Category category = new Category();
                category.setType("ownmedia");
                category.setTitle(getContext().getResources().getString(R.string.my_upload));
                //category.setId(mediaModel.getBody().getOwnMedia().get(i).getId());
                category.setSongs(songList);
                categoriesLsit.add(category);
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


                            for (final Medium medium : bannerBody.getMedia()) {
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


                            Intent intent = new Intent(getActivity(), ViewAllSongActivity.class);
                            intent.putExtra("isEdit", isEdit);
                            intent.putExtra("mobile_no", mobileNo);
                            intent.putExtra("contact_name", name);
                            intent.putExtra("isIncoming", isIncoming);
                            intent.putExtra("isAudio", isAudio);
                            intent.putExtra("postion", 0);
                            intent.putExtra("category", bannerBody.getTitle());
                            intent.putExtra("contact_entity", contactEntity);
                            intent.putExtra("songsList", songList);
                            intent.putExtra("IsBannerList", 1);
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
                    viewPager.setCurrentItem(1);
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

    private void checkPremission() {
        int allPremissionGranted = 1;

        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        Boolean isNotificationAccess = false;

        for (String service : NotificationManagerCompat.getEnabledListenerPackages(getActivity())) {
            if (service.equals(getActivity().getPackageName()))
                isNotificationAccess = true;

        }
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(getActivity())) ||
                Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

            if (isNotificationAccess && !isAccessibilitySettingsOn(getActivity())) {
                showPermissionPopup(1, 1, 0);
                allPremissionGranted = 0;
            } else if (!isNotificationAccess && isAccessibilitySettingsOn(getActivity())) {
                showPermissionPopup(0, 1, 1);
                allPremissionGranted = 0;
            } else if(!isNotificationAccess && !isAccessibilitySettingsOn(getActivity())) {
                showPermissionPopup(0, 1, 0);
                allPremissionGranted = 0;
            }


        } else if (isAccessibilitySettingsOn(getActivity())) {
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(getActivity()) ||
                    Build.VERSION.SDK_INT < Build.VERSION_CODES.M) && !isNotificationAccess)

                showPermissionPopup(0, 1, 1);
            else if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getActivity()) ||
                    Build.VERSION.SDK_INT < Build.VERSION_CODES.M) && isNotificationAccess) {
                showPermissionPopup(1, 0, 1);
                allPremissionGranted = 0;
            }
            else if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getActivity()) ||
                    Build.VERSION.SDK_INT < Build.VERSION_CODES.M) && !isNotificationAccess) {
                showPermissionPopup(0, 0, 1);
                allPremissionGranted = 0;
            }






        } else if (isNotificationAccess) {

            if (((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getActivity()))
                    || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) &&
                    isAccessibilitySettingsOn(getActivity())) {
                showPermissionPopup(1, 0, 1);
                allPremissionGranted = 0;
            } else if (((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(getActivity()))
                    || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) &&
                    !isAccessibilitySettingsOn(getActivity())) {
                showPermissionPopup(1, 1, 0);
                allPremissionGranted = 0;
            }
            else if (((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getActivity()))
                    || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) &&
                    !isAccessibilitySettingsOn(getActivity())) {
                showPermissionPopup(1, 0, 0);
                allPremissionGranted = 0;
            }

        }
        else
        {
            allPremissionGranted=0;
            showPermissionPopup(0, 0, 0);

        }

        if (allPremissionGranted == 1) {
            if (SharedPrefrenceHandler.getInstance().getIsFirstTime() == 0) {
                tutLayout.setVisibility(View.VISIBLE);
                SharedPrefrenceHandler.getInstance().setISFirstTimeHome(1);
                tutLayout.postDelayed(new Runnable() {
                    public void run() {
                        tutLayout.setVisibility(View.GONE);
                        fab.setVisibility(View.VISIBLE);

                    }
                }, 10000);

            } else {
                tutLayout.setVisibility(View.GONE);

            }
        }


    }

    private void showPermissionPopup(int isNotificationAccess, int screenPermission, int accessibility) {
        final Dialog dialogPremission = new Dialog(getActivity());
        dialogPremission.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialogPremission.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        dialogPremission.setContentView(R.layout.custom_premission_dailog);
        Button buttonScreenPremission = dialogPremission.findViewById(R.id.button_screen_premission);
        Button buttonNotification = dialogPremission.findViewById(R.id.button_notification);
        Button buttonAccessibility = dialogPremission.findViewById(R.id.button_accessibility);
        TextView txtTick2 = dialogPremission.findViewById(R.id.txt_tick2);
        TextView txtTick = dialogPremission.findViewById(R.id.txt_tick);
        TextView txtTick1 = dialogPremission.findViewById(R.id.txt_tick1);
        if (isNotificationAccess == 1) {
            buttonNotification.setVisibility(View.GONE);
            //  buttonAccessibility.setVisibility(View.GONE);
            txtTick1.setVisibility(View.VISIBLE);
        } else if (isNotificationAccess == 0) {
            buttonNotification.setVisibility(View.VISIBLE);
            txtTick1.setVisibility(View.GONE);
        }

        if (screenPermission == 1) {
            buttonScreenPremission.setVisibility(View.GONE);
            txtTick.setVisibility(View.VISIBLE);
        } else if (screenPermission == 0) {
            buttonScreenPremission.setVisibility(View.VISIBLE);
            txtTick.setVisibility(View.GONE);
        }

        if (accessibility == 1) {
            buttonAccessibility.setVisibility(View.GONE);
            txtTick2.setVisibility(View.VISIBLE);
        } else if (accessibility == 0) {
            buttonAccessibility.setVisibility(View.VISIBLE);
            txtTick2.setVisibility(View.GONE);
        }

        buttonAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAccessibilitySettingsOn(getActivity())) {
                    dialogPremission.dismiss();
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
                    showTrasparentScreen("Enable Accessibility  Service to Use Kolbeat");


                }

            }
        });


        buttonScreenPremission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getActivity())) {
                    dialogPremission.dismiss();
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getActivity().getPackageName()));
                    startActivity(intent);
                    showTrasparentScreen("Enable Draw Over Other App to Use Kolbeat");


                }

            }
        });

        buttonNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPremission.dismiss();

                startActivity(new Intent(
                        "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                showTrasparentScreen("Enable Notification Access to Use Kolbeat");
            }
        });


        dialogPremission.show();
    }


    private void showTrasparentScreen(String text) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //setScreenDimensions();

                Intent intent1 = new Intent(getActivity(), OverlayActivity.class);
                intent1.putExtra("overlay_text", text);
                startActivity(intent1);

            }
        }, 100);
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

    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = getActivity().getPackageName() + "/" + ServiceCallScreenChanged.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.e(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.e(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.e(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.e(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.e(TAG, "***ACCESSIBILITY IS DISABLED***");
        }

        return false;
    }


}
