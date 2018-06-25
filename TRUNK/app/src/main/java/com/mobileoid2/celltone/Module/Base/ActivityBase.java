package com.mobileoid2.celltone.Module.Base;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iceteck.silicompressorr.SiliCompressor;
import com.mobileoid2.celltone.CustomWidget.Dialog.DialogsCustom;
import com.mobileoid2.celltone.Module.Contacts.Bean.BeanContacts;
import com.mobileoid2.celltone.Module.Contacts.Fragment.FragmentContacts;
import com.mobileoid2.celltone.Module.DashBoard.FragmentDashBoard;
import com.mobileoid2.celltone.Module.Music.Adapter.AdapterMusicList;
import com.mobileoid2.celltone.Module.Music.Bean.Music;
import com.mobileoid2.celltone.Module.Music.Fragment.FragmentMusicUpload;
import com.mobileoid2.celltone.Module.Music.Fragment.FragmentSongs;
import com.mobileoid2.celltone.Module.Music.Fragment.FragmentSongsGerne;
import com.mobileoid2.celltone.Module.Music.Fragment.FragmentVideo;
import com.mobileoid2.celltone.Module.Music.Interface.InterfaceSongSelected;
import com.mobileoid2.celltone.Module.Profile.FragmentMyProfile;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.Util.AppLevelConstraints;
import com.mobileoid2.celltone.Util.AppSharedPref;
import com.mobileoid2.celltone.Util.AppUtils;
import com.mobileoid2.celltone.Util.Constant;
import com.mobileoid2.celltone.Util.PermissionsMarshmallow;
import com.mobileoid2.celltone.database.AppDatabase;
import com.mobileoid2.celltone.database.AsyncTaskUpdateContactsDb;
import com.mobileoid2.celltone.database.AsyncTaskUpdateMusicDb;
import com.mobileoid2.celltone.database.DatabaseConstants;
import com.mobileoid2.celltone.database.InterfaceOperation;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ActivityBase extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, InterfaceSongSelected {

    private FrameLayout frameLayout;
    private TextView textView;
    private RelativeLayout rootLayout;
    public FragmentManager manager = getSupportFragmentManager();
    private String currentFragmentName = "", initialFragment = "";
    private Toolbar toolbar;
    private NavigationView navigationView;
    private View header;
    private Fragment currentFragment;


    private List<Music> songList, videoList;
    private List<Music> currentMusicList;
    private RecyclerView listCommonMusic;
    private AdapterMusicList adapterSearchMusic;


    private PermissionsMarshmallow permissionsMarshmallow;
    private UpdaterVideos updaterVideos;
    private UpdaterSongs updaterSongs;
    private UpdaterContacts updaterContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        try {
            getSupportActionBar().hide();
        } catch (Exception e) {

        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        rootLayout = (RelativeLayout) findViewById(R.id.layout_root);
        textView = (TextView) toolbar.findViewById(R.id.toolbar_header);


        toolbar.setNavigationIcon(R.mipmap.menu_icon);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        header = navigationView.getHeaderView(0);

        LinearLayout layoutNavHeaderRoot = (LinearLayout) header.findViewById(R.id.layout_root);

        layoutNavHeaderRoot.setPadding(5, getStatusBarHeight(), 5, 5);


        setNavigationName();
        setNavigationIcon();

        layoutNavHeaderRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile();
            }
        });

        ((ImageButton) header.findViewById(R.id.imageView)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile();
            }
        });

        ((TextView) header.findViewById(R.id.textview_nav_header)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile();
            }
        });

        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);

        launchDoctorFragmentByReplacing(FragmentDashBoard.newInstance(), FragmentDashBoard.class.toString());


        findViewById(R.id.image_button_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!currentFragmentName.equals(FragmentDashBoard.class.toString()))
                    launchDoctorFragmentByReplacing(FragmentDashBoard.newInstance(), FragmentDashBoard.class.toString());
            }
        });

        findViewById(R.id.image_button_contacts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentFragmentName.equals(FragmentContacts.class.toString()))
                    initiateContactSelection(null, false);
            }
        });


        listCommonMusic = findViewById(R.id.list_all_music);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        listCommonMusic.setLayoutManager(layoutManager);
        listCommonMusic.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration divider = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.divider_list));
        listCommonMusic.addItemDecoration(divider);

        setSearch();
    }


    public void setNavigationName() {
        ((TextView) header.findViewById(R.id.textview_nav_header)).setText(AppSharedPref.instance.getName());
    }


    public void setNavigationIcon() {
        if (!AppSharedPref.instance.getProfilePic().equals("")) {

            Bitmap imageBitmap = null;
            try {
                imageBitmap = SiliCompressor.with(getApplicationContext()).getCompressBitmap(AppSharedPref.instance.getProfilePic());

                Bitmap bitmap = AppUtils.instance.getCircularBitmap(imageBitmap);
                bitmap = Bitmap.createScaledBitmap(bitmap, AppSharedPref.instance.getScreenWidth() / 5, AppSharedPref.instance.getScreenWidth() / 5, false);
                ((ImageButton) header.findViewById(R.id.imageView)).setImageBitmap(bitmap);

            } catch (Exception e) {
                Log.e("ActivityBase", AppUtils.instance.getExceptionString(e));
            }
        } else {
            ((ImageButton) header.findViewById(R.id.imageView)).setImageDrawable(getResources().getDrawable(R.mipmap.nav_profile));
        }


    }

    private void setSearch() {
        try {
            SearchView searchView = (SearchView) findViewById(R.id.search_view);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {


                    if (currentMusicList != null)
                        if (currentMusicList.size() > 0)
                            if (query.length() > 2)
                                setSearchResultList((ArrayList<Music>) filter(currentMusicList, query));
                            else
                                setSearchResultList(new ArrayList<Music>());

                    return false;
                }
            });


            searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        if (currentFragmentName.equals(FragmentSongs.class.toString()) || currentFragmentName.equals(FragmentVideo.class.toString())) {

                        } else {
                            currentMusicList = new ArrayList<Music>();
                            if (songList == null) songList = new ArrayList<Music>();
                            if (songList.size() == 0)
                                songList = AppDatabase.getAppDatabase(AppLevelConstraints.getAppContext()).daoMusic().getAll(DatabaseConstants.VALUE_FALSE);
                            currentMusicList.addAll(songList);

                            if (videoList == null) videoList = new ArrayList<Music>();
                            if (videoList.size() == 0)
                                videoList = AppDatabase.getAppDatabase(AppLevelConstraints.getAppContext()).daoMusic().getAll(DatabaseConstants.VALUE_TRUE);

                            currentMusicList.addAll(videoList);
                        }
                    }
                }
            });

        } catch (Exception e) {
            Log.e("ActivityBase", AppUtils.instance.getExceptionString(e));
        }
    }

    private List<Music> filter(List<Music> commonList, String query) {

        if (commonList == null || query == null) return new ArrayList<Music>();
        if (query.equals("")) return new ArrayList<Music>();
        query = query.toLowerCase();
        final List<Music> filteredModelList = new ArrayList<>();
        for (Music model : commonList) {
            final String text = model.getSongTitle().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    private void setSearchResultList(ArrayList<Music> filteredList) {
        if (adapterSearchMusic == null) {
            adapterSearchMusic = new AdapterMusicList(getApplicationContext(), filteredList, this, true);
            listCommonMusic.setAdapter(adapterSearchMusic);
        } else {
            adapterSearchMusic.refreshList(filteredList);
        }
    }

    private void openProfile() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        launchDoctorFragmentByReplacing(FragmentMyProfile.newInstance(), FragmentMyProfile.class.toString());

    }

    @Override
    public void onResume() {
        super.onResume();
        checkForPermissions();

    }

    private void checkForPermissions() {
        if (permissionsMarshmallow == null)
            permissionsMarshmallow = new PermissionsMarshmallow(this);
        if (permissionsMarshmallow.isProceedFurther())
            permissionsMarshmallow.checkAllPermissions();
        else
            updateFirstTimeData();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSongSelected(Music music) {

        ((SearchView) findViewById(R.id.search_view)).clearFocus();
        ((SearchView) findViewById(R.id.search_view)).onActionViewCollapsed();
        setSearchResultList(new ArrayList<Music>());

        if (music.getIsVideo().equals(DatabaseConstants.VALUE_TRUE)) {


            if (currentFragmentName.equals(FragmentVideo.class.toString())) {
                ((FragmentVideo) currentFragment).setCurrentMusic(music);
            } else

                launchDoctorFragmentByReplacing(FragmentVideo.newInstance(music, null), FragmentVideo.class.toString());
        }

        if (music.getIsVideo().equals(DatabaseConstants.VALUE_FALSE)) {

            if (currentFragmentName.equals(FragmentSongs.class.toString())) {
                ((FragmentSongs) currentFragment).setCurrentMusic(music);

            } else
                launchDoctorFragmentByReplacing(FragmentSongs.newInstance(music, null), FragmentSongs.class.toString());
        }

    }

    @Override
    public void onSongSelected(int position, boolean isAssign) {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (currentFragment != null) {
            currentFragment.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onBackPressed() {

        try {
            if (currentFragmentName.equals(FragmentMusicUpload.class.toString())) {
                if (((FragmentMusicUpload) currentFragment).hideRecorder()) {
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!((SearchView) findViewById(R.id.search_view)).isIconified()) {
            ((SearchView) findViewById(R.id.search_view)).onActionViewCollapsed();
            setSearchResultList(new ArrayList<Music>());
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {


            int totalFragments = manager.getBackStackEntryCount();
            if (totalFragments > -1) {


                if (totalFragments == 0) {
                    if (currentFragmentName.equals(FragmentDashBoard.class.toString())) {
                        Snackbar snackbar = Snackbar
                                .make(rootLayout, R.string.text_do_you_want, Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.text_exit)+" " + getApplicationContext().getResources().getString(R.string.icon_power), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ActivityBase.this.finish();
                                        Intent intent = new Intent(Intent.ACTION_MAIN);
                                        intent.addCategory(Intent.CATEGORY_HOME);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                });

                        Button button = (Button) snackbar.getView().findViewById(android.support.design.R.id.snackbar_action);
                        button.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf"));
                        button.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                        TextView textView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));


                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(getResources().getColor(R.color.colorEmptybackground));
                        snackbar.show();
                    } else {
                        launchDoctorFragmentByReplacing(FragmentDashBoard.newInstance(), FragmentDashBoard.class.toString());
                    }
                } else {

                    FragmentManager.BackStackEntry backEntry = manager.getBackStackEntryAt(totalFragments - 1);
                    String str = backEntry.getName();

                    manager.popBackStack(str, FragmentManager.POP_BACK_STACK_INCLUSIVE);


                    totalFragments = manager.getBackStackEntryCount();
                    if (totalFragments > 0) {

                        try {
                            if (totalFragments > 1) {
                                backEntry = manager.getBackStackEntryAt(totalFragments - 2);
                                str = backEntry.getName();
                            } else {
                                str = initialFragment;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            str = initialFragment;
                        }


                        currentFragmentName = str;
                        setFragmentHeader(str);

                    }

                }


            } else {
                finish();
            }


        }
    }

    private void setToolbarHeader(String textHeader) {
        textView.setText(textHeader);
    }

    public void setFragmentHeader(String fragmentName) {


        if (fragmentName.equals(FragmentDashBoard.class.toString())) {
            String version = "";
            try {
                PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
                version = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                version = "";
            }


            setToolbarHeader(getResources().getString(R.string.text_dashboard) + " " + version);
            setAllUnchecked();


            ((ImageButton) findViewById(R.id.image_button_home)).setImageDrawable(getResources().getDrawable(R.mipmap.home_icon_active));
        } else {
            ((ImageButton) findViewById(R.id.image_button_home)).setImageDrawable(getResources().getDrawable(R.mipmap.home_icon));
        }
        if (fragmentName.equals(FragmentSongsGerne.class.toString())) {
            setToolbarHeader(getResources().getString(R.string.text_gerne));
            navigationView.setCheckedItem(R.id.nav_songs);
        }
        if (fragmentName.equals(FragmentSongs.class.toString())) {
            setToolbarHeader(getResources().getString(R.string.text_songs));
            navigationView.setCheckedItem(R.id.nav_songs);
        }
        if (fragmentName.equals(FragmentVideo.class.toString())) {
            setToolbarHeader(getResources().getString(R.string.text_videos));
            navigationView.setCheckedItem(R.id.nav_videos);
        }
        if (fragmentName.equals(FragmentMusicUpload.class.toString())) {
            setToolbarHeader(getResources().getString(R.string.text_upload));
            navigationView.setCheckedItem(R.id.nav_upload);
        }
        if (fragmentName.equals(FragmentMyProfile.class.toString())) {
            setToolbarHeader(getResources().getString(R.string.text_profile));
            setAllUnchecked();
        }
        if (fragmentName.equals(FragmentContacts.class.toString())) {
            setToolbarHeader(getResources().getString(R.string.text_contacts));
            navigationView.setCheckedItem(R.id.nav_contacts);

            ((ImageButton) findViewById(R.id.image_button_contacts)).setImageDrawable(getResources().getDrawable(R.mipmap.profile_icon_active));
        } else {
            ((ImageButton) findViewById(R.id.image_button_contacts)).setImageDrawable(getResources().getDrawable(R.mipmap.profile_icon));
        }
    }

    private void setAllUnchecked() {
        int size = navigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_songs) {

            launchDoctorFragmentByReplacing(FragmentSongsGerne.newInstance(), FragmentSongsGerne.class.toString());

        } else if (id == R.id.nav_logout) {

            AppSharedPref.instance.setLoginState(false);
            AppSharedPref.instance.saveProfilePic("");
            AppSharedPref.instance.saveName("");
            AppSharedPref.instance.saveNumber("");
            finish();

        } else if (id == R.id.nav_videos) {

            launchDoctorFragmentByReplacing(FragmentVideo.newInstance(null, null), FragmentVideo.class.toString());

        } else if (id == R.id.nav_contacts) {

            launchDoctorFragmentByReplacing(FragmentContacts.newInstance(null, false), FragmentContacts.class.toString());

        } else if (id == R.id.nav_upload) {
            launchDoctorFragmentByReplacing(FragmentMusicUpload.newInstance(), FragmentMusicUpload.class.toString());
        }

        navigationView.setCheckedItem(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void launchDoctorFragmentByAdding(Fragment fragment, String incomingFragmentTag) {

        if (currentFragmentName.equals(incomingFragmentTag))
            return;

        if (!((SearchView) findViewById(R.id.search_view)).isIconified()) {
            ((SearchView) findViewById(R.id.search_view)).onActionViewCollapsed();
            setSearchResultList(new ArrayList<Music>());
        }


        currentFragmentName = incomingFragmentTag;

        this.currentFragment = fragment;

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.animator.enter_from_left, R.animator.exit_from_right, 0, 0);
        transaction.add(frameLayout.getId(), fragment, incomingFragmentTag);
        transaction.addToBackStack(incomingFragmentTag);

        transaction.commit();
        manager.executePendingTransactions();

        setFragmentHeader(incomingFragmentTag);

    }

    public void launchDoctorFragmentByReplacing(Fragment fragment, String incomingFragmentTag) {

        if (currentFragmentName.equals(incomingFragmentTag))
            return;

        if (!((SearchView) findViewById(R.id.search_view)).isIconified()) {
            ((SearchView) findViewById(R.id.search_view)).onActionViewCollapsed();
            setSearchResultList(new ArrayList<Music>());
        }

        currentFragmentName = incomingFragmentTag;
        initialFragment = incomingFragmentTag;
        try {
            manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.currentFragment = fragment;

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.animator.enter_from_left, R.animator.exit_from_right, 0, 0);
        transaction.replace(frameLayout.getId(), fragment, incomingFragmentTag);
        transaction.commit();
        manager.executePendingTransactions();

        setFragmentHeader(incomingFragmentTag);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void initiateContactSelection(Music music, boolean isIncoming) {
        launchDoctorFragmentByReplacing(FragmentContacts.newInstance(music, isIncoming), FragmentContacts.class.toString());
    }

    public void setSongList(List<Music> list) {
        songList = list;

    }

    public void setVideoList(List<Music> list) {
        videoList = list;

    }

    public void setCurrentMusicList(List<Music> currentMusicList) {
        this.currentMusicList = currentMusicList;
    }

    String name = "";

    public void setPhoneNo(HashSet<BeanContacts> selectedContacts, Music selectedMusic, boolean isIncoming) {


        DialogsCustom.instance.showMessageDialog(this, getString(R.string.text_assigning_music), "");

        name = "";

        ArrayList<BeanContacts> updatedContacts = new ArrayList<BeanContacts>();

        for (BeanContacts selectedContact : selectedContacts) {
            name = selectedContact.getName();
            if (isIncoming) {
                selectedContact.setMusicIncomingPath(selectedMusic.getSongsPath());
                selectedContact.setMusicIncomingThumbNail(selectedMusic.getThumbUrl());
                selectedContact.setIsIncomingVideo(selectedMusic.getIsVideo());
            } else {
                selectedContact.setMusicOutgoingPath(selectedMusic.getSongsPath());
                selectedContact.setMusicOutgoingThumbNail(selectedMusic.getThumbUrl());
                selectedContact.setIsOutgoingVideo(selectedMusic.getIsVideo());
            }

            DialogsCustom.instance.updateSubjectMessage(selectedContact.getName());
            updatedContacts.add(selectedContact);
        }
        DialogsCustom.instance.updateSubjectMessage(getString(R.string.text_update_database));

        new AsyncTaskUpdateContactsDb(updatedContacts, new InterfaceOperation() {
            @Override
            public void onOperationCompleted() {
                if (selectedContacts.size() > 1)
                    Toast.makeText(getApplicationContext(), getString(R.string.text_song_) + " " + selectedMusic.getSongTitle() + " " + getString(R.string._assigned_to_multiple), Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(), getString(R.string.text_song_) + " " + selectedMusic.getSongTitle() + " " + getString(R.string.text_assigned_to) + " " + name, Toast.LENGTH_LONG).show();

                DialogsCustom.instance.cancelDialog();
            }
        }, DatabaseConstants.VALUE_UPDATE).execute();


        onBackPressed();
    }


    private void updateFirstTimeData() {
        if (!AppSharedPref.instance.getLoginState()) {
            DialogsCustom.instance.showMessageDialog(ActivityBase.this, getString(R.string.text_sync_contacts), "");

            if (!isUpdating) {
                isUpdating = true;
                updaterContacts = new UpdaterContacts();
                updaterContacts.execute();
            }
        }
    }

    boolean isUpdating = false;

    //updateContacts first time
    private class UpdaterContacts extends AsyncTask<Void, Void, Void> {

        private List<BeanContacts> oldList;

        @Override
        protected Void doInBackground(Void... voids) {

            oldList = new ArrayList<BeanContacts>();
            long startMilliSeconds = System.currentTimeMillis();
            ContentResolver cr = AppLevelConstraints.getAppContext().getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            try {
                if (cur != null)
                    if (cur.getCount() > 0) {
                        int cursorSize = cur.getColumnCount();
                        String phoneNo = "";
                        while (cur.moveToNext()) {

                            String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                            Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                            if (pCur != null) {
                                while (pCur.moveToNext()) {
                                    phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                }
                                pCur.close();
                            }

                            phoneNo = AppUtils.instance.fixPhoneNo(phoneNo);

                            if (!phoneNo.equals("")) {
                                BeanContacts contactDetail = new BeanContacts();
                                contactDetail.setId(id);
                                contactDetail.setNumber(phoneNo);
                                contactDetail.setName(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                                contactDetail.setIsIncomingVideo("999");
                                contactDetail.setMusicIncomingPath("");
                                contactDetail.setMusicIncomingThumbNail("");
                                contactDetail.setIsOutgoingVideo("999");
                                contactDetail.setMusicOutgoingPath("");
                                contactDetail.setMusicOutgoingThumbNail("");
                                oldList.add(contactDetail);

                                if (ActivityBase.this != null) {
                                    ActivityBase.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            DialogsCustom.instance.updateSubjectMessage(contactDetail.getName());
                                        }
                                    });
                                }
                            }
                        }
                    }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (cur != null)
                    cur.close();
            }
            Log.e("CONTACTS", "fetching time : " + (System.currentTimeMillis() - startMilliSeconds) + "size of list to be added " + oldList.size());
            return null;
        }

        @Override
        protected void onPostExecute(Void o) {
            super.onPostExecute(o);
            try {

                updaterSongs = new UpdaterSongs();

                if (oldList != null)
                    if (oldList.size() > 0) {

                        DialogsCustom.instance.updateSubjectMessage(getString(R.string.text_update_database));
                        new AsyncTaskUpdateContactsDb(new ArrayList<BeanContacts>(), new InterfaceOperation() {
                            @Override
                            public void onOperationCompleted() {
                                new AsyncTaskUpdateContactsDb(oldList, new InterfaceOperation() {
                                    @Override
                                    public void onOperationCompleted() {
                                        oldList = null;
                                        DialogsCustom.instance.updateHeaderMessage(getString(R.string.text_sync_songs));
                                        updaterSongs.execute();
                                    }
                                }, DatabaseConstants.VALUE_INSERT).execute();
                            }
                        }, DatabaseConstants.VALUE_DELETE).execute();
                    } else {

                        DialogsCustom.instance.updateHeaderMessage(getString(R.string.text_sync_songs));
                        updaterSongs.execute();
                    }
                else {
                    DialogsCustom.instance.updateHeaderMessage(getString(R.string.text_sync_songs));
                    updaterSongs.execute();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // update songs first time
    class UpdaterSongs extends AsyncTask<String, Void, Void> {

        private List<Music> newList;

        @Override
        protected Void doInBackground(String... params) {

            try {
                readSongsFromDevice();
            } catch (Exception e) {
                Log.e("fetch contacts", AppUtils.instance.getExceptionString(e));
            }

            return null;
        }

        // for updating Songs
        private void readSongsFromDevice() throws InterruptedException {

            newList = new ArrayList<Music>();

            try {
                String cols[] = {MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ALBUM_ID,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Genres._ID
                };

                String selection = MediaStore.Audio.Media.DURATION + ">=10000";
                Cursor cursor = AppLevelConstraints.getAppContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cols, selection, null, null);
                newList.addAll(fillNewSongListData(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, newList, cursor));
                if (cursor != null)
                    cursor.close();

                cursor = AppLevelConstraints.getAppContext().getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, cols, selection, null, null);
                newList.addAll(fillNewSongListData(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, newList, cursor));
                if (cursor != null)
                    cursor.close();


                // if the newList has data, means new music is added, if audiolist had data, means it has been removed from device. hence commence operation update
            } catch (Exception e) {
                Log.e("Update", AppUtils.instance.getExceptionString(e));
            }
        }

        private List<Music> fillNewSongListData(Uri contentUri, List<Music> newList, Cursor cursor) throws Exception {
            if (cursor != null && cursor.getCount() > 0) {

                while (cursor.moveToNext()) {
                    Music music = new Music();
                    final String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    music.setSongTitle(name);
                    music = fillDataIntoSongMusic(contentUri, music, cursor);
                    newList.add(music);

                    if (ActivityBase.this != null) {
                        ActivityBase.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogsCustom.instance.updateSubjectMessage(name);
                            }
                        });
                    }

                }
            }

            return newList;
        }

        private Music fillDataIntoSongMusic(Uri contentUri, Music music, Cursor cursor) throws Exception {

            String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            music.setSongsPath(data);
            music.setSongAlbum(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)));
            music.setThumbUrl(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
            music.setIsVideo(DatabaseConstants.VALUE_FALSE);
            music.setId(data);
            MediaMetadataRetriever mr = new MediaMetadataRetriever();
            try {
                Uri trackUri = ContentUris.withAppendedId(contentUri, Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres._ID))));
                if (trackUri != null) {
                    mr.setDataSource(getApplicationContext(), trackUri);
                    String thisGenre = mr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);

                    if (thisGenre == null) thisGenre = "other";
                    music.setGerne(thisGenre);
                }
            } catch (Exception e) {
                Log.e("MUSIC", AppUtils.instance.getExceptionString(e));
                music.setGerne("other");
            }

            return music;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            updaterVideos = new UpdaterVideos();

            if (newList != null)
                if (newList.size() > 0) {
                    DialogsCustom.instance.updateSubjectMessage(getString(R.string.text_update_database));
                    new AsyncTaskUpdateMusicDb(DatabaseConstants.VALUE_FALSE, new InterfaceOperation() {
                        @Override
                        public void onOperationCompleted() {
                            new AsyncTaskUpdateMusicDb(newList, new InterfaceOperation() {
                                @Override
                                public void onOperationCompleted() {
                                    newList = null;
                                    DialogsCustom.instance.showMessageDialog(ActivityBase.this, getString(R.string.text_sync_videos), "");
                                    updaterVideos.execute();
                                }
                            }, DatabaseConstants.VALUE_INSERT).execute();
                        }
                    }, DatabaseConstants.VALUE_DELETE).execute();
                } else {
                    DialogsCustom.instance.showMessageDialog(ActivityBase.this, getString(R.string.text_sync_videos), "");
                    updaterVideos.execute();
                }
            else {
                DialogsCustom.instance.showMessageDialog(ActivityBase.this, getString(R.string.text_sync_videos), "");
                updaterVideos.execute();
            }

        }
    }

    class UpdaterVideos extends AsyncTask<String, Void, Void> {
        private List<Music> newList;

        @Override
        protected Void doInBackground(String... params) {

            try {
                readVideosFromDevice();
            } catch (Exception e) {
                Log.e("fetch Videos", AppUtils.instance.getExceptionString(e));
            }
            return null;
        }

        //read videos from device
        private void readVideosFromDevice() throws InterruptedException {

            newList = new ArrayList<Music>();

            try {
                String cols[] = {MediaStore.Video.Media.ALBUM,
                        MediaStore.Video.Media.TITLE,
                        MediaStore.Video.Media.DATA,
                        MediaStore.Video.Media._ID,
                        MediaStore.Video.Media.DURATION,
                };

                Cursor cursor = AppLevelConstraints.getAppContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, cols, null, null, null);
                newList.addAll(getNewVideoListToAdd(newList, cursor));
                if (cursor != null)
                    cursor.close();
                cursor = AppLevelConstraints.getAppContext().getContentResolver().query(MediaStore.Video.Media.INTERNAL_CONTENT_URI, cols, null, null, null);
                newList.addAll(getNewVideoListToAdd(newList, cursor));
                if (cursor != null)
                    cursor.close();


            } catch (Exception e) {
                Log.e("Update Video ", AppUtils.instance.getExceptionString(e));
            }
        }

        private List<Music> getNewVideoListToAdd(List<Music> newList, Cursor cursor) throws Exception {

            if (cursor != null && cursor.getCount() > 0) {
                int cursorSize = cursor.getCount();

                while (cursor.moveToNext()) {
                    Music music = new Music();
                    final String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                    music.setSongTitle(name);
                    music = fillVideoDataIntoMusic(music, cursor);
                    newList.add(music);
                    if (ActivityBase.this != null) {
                        ActivityBase.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogsCustom.instance.updateSubjectMessage(name);
                            }
                        });
                    }
                }
            }
            return newList;
        }

        private Music fillVideoDataIntoMusic(Music music, Cursor cursor) throws Exception {


            String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            int music_column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            String filename = cursor.getString(music_column_index);
            music.setSongAlbum(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM)));
            music.setSongsPath(data);
            music.setId(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)));
            music.setThumbUrl("");
            music.setIsVideo(DatabaseConstants.VALUE_TRUE);

            return music;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (newList != null)
                if (newList.size() > 0) {
                    DialogsCustom.instance.updateSubjectMessage(getString(R.string.text_update_database));
                    new AsyncTaskUpdateMusicDb(DatabaseConstants.VALUE_TRUE, new InterfaceOperation() {
                        @Override
                        public void onOperationCompleted() {
                            new AsyncTaskUpdateMusicDb(newList, new InterfaceOperation() {
                                @Override
                                public void onOperationCompleted() {
                                    newList = null;
                                    DialogsCustom.instance.cancelDialog();
                                    AppSharedPref.instance.setLoginState(true);
                                }
                            }, DatabaseConstants.VALUE_INSERT).execute();
                        }
                    }, DatabaseConstants.VALUE_DELETE).execute();
                } else {
                    DialogsCustom.instance.cancelDialog();
                    AppSharedPref.instance.setLoginState(true);
                }
            else {
                AppSharedPref.instance.setLoginState(true);
                DialogsCustom.instance.cancelDialog();
            }
        }
    }

}
