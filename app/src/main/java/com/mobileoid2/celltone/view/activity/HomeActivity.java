package com.mobileoid2.celltone.view.activity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.mobileoid2.celltone.database.ContactEntity;
import com.mobileoid2.celltone.network.model.treadingMedia.Song;
import com.mobileoid2.celltone.view.activity.Login.LoginOtpActivity;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.view.fragments.AbousUsFragment;
import com.mobileoid2.celltone.view.fragments.FAQFragment;
import com.mobileoid2.celltone.view.fragments.FragmentMusicUpload;
import com.mobileoid2.celltone.view.fragments.HomeFragment;
import com.mobileoid2.celltone.view.fragments.LanguageFragment;
import com.mobileoid2.celltone.view.fragments.OfferFragment;
import com.mobileoid2.celltone.view.fragments.PlanFragment;
import com.mobileoid2.celltone.view.fragments.ProfileFragment;
import com.mobileoid2.celltone.view.fragments.QueryListFragment;
import com.mobileoid2.celltone.view.listener.NavigationLisitner;
import com.mobileoid2.celltone.network.APIClient;
import com.mobileoid2.celltone.network.ApiConstant;
import com.mobileoid2.celltone.network.ApiInterface;
import com.mobileoid2.celltone.network.NetworkCallBack;
import com.mobileoid2.celltone.network.SendRequest;
import com.mobileoid2.celltone.network.jsonparsing.JsonResponse;
import com.mobileoid2.celltone.pojo.Contact;
import com.mobileoid2.celltone.pojo.FCMMODELREQUEST;
import com.mobileoid2.celltone.pojo.PojoContactsUpload;
import com.mobileoid2.celltone.utility.ContactFetcher;
import com.mobileoid2.celltone.utility.SharedPrefrenceHandler;
import com.mobileoid2.celltone.utility.Utils;
import com.mobileoid2.celltone.view.listener.QueryLisiner;
import com.splunk.mint.Mint;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        NavigationLisitner, NetworkCallBack, ChangeToolBarTitleListiner, QueryLisiner {

    public static final String TAG = HomeActivity.class.getSimpleName();


    private Boolean mSlideState = false;
    private VideoView videoView;
    private ImageView matNav;
    private DrawerLayout drawer;
    private int isEdit = 0;
    private String mobileNo = "";
    private String name = "";
    private int isIncoming = -1;
    private ContactEntity contactEntity = null;
    private View view;
    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homemain);
        Mint.initAndStartSession(this.getApplication(), "f9ede8e8");
        view = findViewById(R.id.mainView);
        apiInterface = (ApiInterface) APIClient.getClient().create(ApiInterface.class);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        Intent intent = getIntent();
        if (intent.getIntExtra("isEdit", 0) == 1) {
            isEdit = 1;
            mobileNo = intent.getStringExtra("mobile_no");
            name = intent.getStringExtra("contact_name");
            isIncoming = intent.getIntExtra("isIncoming", -1);
            contactEntity = (ContactEntity) getIntent().getSerializableExtra("contact_entity");


        } else
            refreshMediaSetByOther();
        if (!SharedPrefrenceHandler.getInstance().isFcpSend()) {

            FCMMODELREQUEST fcmmodelrequest = new FCMMODELREQUEST();
            fcmmodelrequest.setFcpId(SharedPrefrenceHandler.getInstance().getFCPTCOKEN());
            SendRequest.sendRequest(ApiConstant.FCM_API, apiInterface.uploadFcm(SharedPrefrenceHandler.getInstance().getUSER_TOKEN(), fcmmodelrequest), this);
        }

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                mSlideState = true;
                //  matNav.setVisibility(View.GONE);

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                mSlideState = false;

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (navigationView.getMenu() != null) {
            for (int i = 0; i < navigationView.getMenu().size(); i++) {
                navigationView.getMenu().getItem(i).setActionView(R.layout.menu_right_icon);
            }
        }


        uploadContacts();

        if (isEdit == 1) {
            String msg = "";

            if (isIncoming == 1)
                msg = getString(R.string.update_incoming_media_text) + name;
            else
                msg = getString(R.string.update_outgoing_media_text) + name;

            Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_INDEFINITE);
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
        setFragment(HomeFragment.newInstance(this, isEdit, mobileNo, name, isIncoming, contactEntity), HomeFragment.class.getSimpleName());


    }

    private void uploadContacts() {
        new AsyncTask<Void, Void, PojoContactsUpload>() {
            List<Contact> listContacts = new ContactFetcher(HomeActivity.this).fetchAll();


            @Override
            protected PojoContactsUpload doInBackground(Void... voids) {
                PojoContactsUpload pojoContactsUpload = new PojoContactsUpload();
                pojoContactsUpload.setContacts(new ArrayList<String>());
                //List<PojoContacts> list = new ArrayList<>();
                for (int i = 0; i < listContacts.size(); i++) {
                    if (listContacts.get(i).numbers.size() == 0) {
                        Contact contact = listContacts.remove(i);
                        continue;
                    }


                    listContacts.get(i).numbers.get(0).number = listContacts.get(i).numbers.get(0).number.replaceAll("\\s+", "");
                    String mobileNumber = listContacts.get(i).numbers.get(0).number;
                    if (mobileNumber.substring(0, 1).equals("+"))
                        pojoContactsUpload.getContacts().add(mobileNumber);
                    else if (mobileNumber.substring(0, 1).equals("0")) {
                        // text.substring(1)
                        String number = mobileNumber.substring(1);
                        pojoContactsUpload.getContacts().add(SharedPrefrenceHandler.getInstance().getCOUTRYCODE() + number);
                    } else
                        pojoContactsUpload.getContacts().add(SharedPrefrenceHandler.getInstance().getCOUTRYCODE() + mobileNumber);

                }


                return pojoContactsUpload;
            }

            @Override
            protected void onPostExecute(PojoContactsUpload contacts) {
                super.onPostExecute(contacts);

                // System.out.println("HomeActivity.onPostExecute" + contacts.getContacts().size());

                SendRequest.sendRequest(ApiConstant.SYNC_CONTACT_API, apiInterface.syncContact(SharedPrefrenceHandler.getInstance().getUSER_TOKEN(), contacts), HomeActivity.this);


            }
        }.execute();
    }

    private void setFragment(Fragment homeFragment, String TAG) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, homeFragment).addToBackStack(TAG);
        ft.commit();
    }


    private void onBack()

    {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (getSupportFragmentManager().getBackStackEntryCount() >= 1)
            super.onBackPressed();
        else
            finish();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else
            onBack();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //  getMenuInflater().inflate(R.menu.homemain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            refreshMediaSetByOther();
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                onBack();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void refreshMediaSetByOther() {

        Utils.getMediForMe(apiInterface, this);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        switch (item.getItemId()) {

            case R.id.nav_offer:
                getSupportActionBar().setTitle(getString(R.string.offer));
                setFragment(OfferFragment.newInstance(), OfferFragment.class.getSimpleName());
                //startActivity(new Intent(this, OfferActivity.class));
                break;
            case R.id.nav_plan:
                getSupportActionBar().setTitle(getString(R.string.my_plan));
                setFragment(PlanFragment.newInstance(this), PlanFragment.class.getSimpleName());
                break;
            case R.id.nav_upload_own:
                getSupportActionBar().setTitle(getString(R.string.upload));
                //  fragmentMusicUpload = FragmentMusicUpload.newInstance(getIntent().getIntExtra("isAudio",0));
                setFragment(FragmentMusicUpload.newInstance(2), FragmentMusicUpload.class.getSimpleName());
                // startActivity(new Intent(this, UploadActivity.class));
                break;
            case R.id.nav_faq:
                getSupportActionBar().setTitle(getString(R.string.faq));
                setFragment(new FAQFragment(), FAQFragment.class.getSimpleName());
                //  startActivity(new Intent(this, FAQActivity.class));
                break;


            case R.id.nav_profile:
//                startActivity(new Intent(this, ProfileActivity.class));
                getSupportActionBar().setTitle(getString(R.string.my_profile));
                setFragment(ProfileFragment.newInstance(this), ProfileFragment.class.getSimpleName());
                break;


            case R.id.nav_contacts:
                Intent intent = new Intent(this, ContactActivity.class);
                intent.putExtra("isOutgoing", -1);
                intent.putExtra("isAudio", -1);
                intent.putExtra("song", new Song());
                intent.putExtra("isEdit", 1);
                startActivity(intent);
                break;

            case R.id.nav_contact_us:
//                Intent contactUs = new Intent(this, AbousUs.class);
//                contactUs.putExtra("typeName", getString(R.string.contact_us));
//                contactUs.putExtra("type", "legal/one/contact");
//                startActivity(contactUs);

                getSupportActionBar().setTitle(getString(R.string.contact_us));
                setFragment(AbousUsFragment.newInstance("legal/one/contact"), AbousUsFragment.class.getSimpleName());
                break;


            case R.id.nav_about_us:
//                Intent aboutUs = new Intent(this, AbousUs.class);
//                aboutUs.putExtra("typeName", getString(R.string.about_us));
//                aboutUs.putExtra("type", "legal/one/about");
//                startActivity(aboutUs);

                getSupportActionBar().setTitle(getString(R.string.about_us));
                setFragment(AbousUsFragment.newInstance("legal/one/about"), AbousUsFragment.class.getSimpleName());
                break;
            case R.id.nav_privacy:
//                Intent privacy = new Intent(this, AbousUs.class);
//                privacy.putExtra("typeName", getString(R.string.privacy_policy));
//                privacy.putExtra("type", "legal/one/privacy");
//                startActivity(privacy);
                getSupportActionBar().setTitle(getString(R.string.privacy_policy));
                setFragment(AbousUsFragment.newInstance("legal/one/privacy"), AbousUsFragment.class.getSimpleName());
                break;
            case R.id.nav_query:
                getSupportActionBar().setTitle(getString(R.string.query));
                setFragment(QueryListFragment.newInstance(this), QueryListFragment.class.getSimpleName());

                //  startActivity(new Intent(this, QueryActivity.class));
                break;
            case R.id.nav_language:
                // startActivity(new Intent(this, LanguageActivity.class));
                setFragment(new LanguageFragment(), LanguageFragment.class.getSimpleName());
                getSupportActionBar().setTitle(getString(R.string.choose_language));
                break;


            case R.id.nav_logout:
                try {
                    //
                    //  getApplicationContext().deleteDatabase(DATABASE_NAME);
                    SharedPrefrenceHandler.getInstance().clearData();
                    deleteProductDirectory();

                    startActivity(new Intent(this, LoginOtpActivity.class));
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;


        }
        drawer.closeDrawer(GravityCompat.START);


        return false;
    }

    private void deleteProductDirectory() {
        try {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("DIR_PRODUCT_VIDEO", Context.MODE_PRIVATE);
            if (directory.isDirectory()) {
                String[] children = directory.list();
                for (int i = 0; i < children.length; i++) {
                    new File(directory, children[i]).delete();
                }
            }
            directory = cw.getDir("DIR_PRODUCT_PDF", Context.MODE_PRIVATE);
            if (directory.isDirectory()) {
                String[] children = directory.list();
                for (int i = 0; i < children.length; i++) {
                    new File(directory, children[i]).delete();
                }
            }
            directory = cw.getDir("DIR_PRODUCT_IMAGE", Context.MODE_PRIVATE);
            if (directory.isDirectory()) {
                String[] children = directory.list();
                for (int i = 0; i < children.length; i++) {
                    new File(directory, children[i]).delete();
                }
            }
        } catch (Exception e) {

        }
    }

    private void displaySelectedScreen(Fragment fragment, String tag) {

        //creating fragment object

        //replacing the fragment
        if (fragment != null) {
            setFragment(fragment, TAG);
        }

        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void setNavigation() {
        if (mSlideState) {
            mSlideState = false;
            drawer.closeDrawer(Gravity.START);
        } else {
            mSlideState = true;
            drawer.openDrawer(Gravity.START);
        }
    }

    @Override
    public void onMenuClick() {
        refreshMediaSetByOther();

    }

    @Override
    public void getResponse(JsonResponse response, int type) {
        if (response.getObject() != null) {
            switch (type) {
                case ApiConstant.FCM_API:
                    SharedPrefrenceHandler.getInstance().setFCMID_SEND(true);
                    break;
                case ApiConstant.MEDIA_SET_API:
                    Utils utils = new Utils();
                    utils.parseRequest(response.getObject(), this);
                    break;
                case ApiConstant.SYNC_CONTACT_API:
                    String ss = response.getObject();
                    break;
            }
        }
        //   {"status":1000,"body":true,"message":"success"}
    }

    @Override
    public void setTitle(String text) {
        getSupportActionBar().setTitle(text);
    }

    @Override
    public void setTitle(String text, String songName) {

    }

    @Override
    public void setFragmnet(String text, Fragment fragment, int isHide) {
        setFragment(fragment, "");
        if (isHide == 1)
            getActionBar().hide();
        else
            getActionBar().show();

    }

    /*-------------------------*/


}
