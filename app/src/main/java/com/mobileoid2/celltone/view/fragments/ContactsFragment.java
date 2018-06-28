package com.mobileoid2.celltone.view.fragments;

import android.Manifest;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.Service.ServiceCallScreenChanged;
import com.mobileoid2.celltone.database.AppDatabase;
import com.mobileoid2.celltone.database.ContactEntity;
import com.mobileoid2.celltone.database.RingtoneEntity;
import com.mobileoid2.celltone.network.APIClient;
import com.mobileoid2.celltone.network.ApiInterface;
import com.mobileoid2.celltone.network.ApiConstant;
import com.mobileoid2.celltone.network.NetworkCallBack;
import com.mobileoid2.celltone.network.SendRequest;
import com.mobileoid2.celltone.network.jsonparsing.JsonResponse;
import com.mobileoid2.celltone.network.model.contacts.ContactsMedia;
import com.mobileoid2.celltone.network.model.contacts.Incommingother;
import com.mobileoid2.celltone.network.model.contacts.SaveContactsResponse;
import com.mobileoid2.celltone.network.model.contacts.SendContactsModel;
import com.mobileoid2.celltone.network.model.treadingMedia.Song;
import com.mobileoid2.celltone.pojo.PojoContacts;
import com.mobileoid2.celltone.pojo.SelectContact;
import com.mobileoid2.celltone.utility.ContactFetcher;
import com.mobileoid2.celltone.utility.SharedPrefrenceHandler;
import com.mobileoid2.celltone.utility.Utils;
import com.mobileoid2.celltone.view.SeparatorDecoration;
import com.mobileoid2.celltone.view.adapter.MyContactsRecyclerViewAdapter;
import com.mobileoid2.celltone.view.listener.SelectedContactListner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ContactsFragment extends Fragment implements NetworkCallBack, View.OnClickListener, SelectedContactListner, CompoundButton.OnCheckedChangeListener {


    public static final String TAG = ContactsFragment.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE_NOTIFICATION = 6;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE_ACCESS = 7;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE_OVERLAY = 8138;
    private ApiInterface apiInterface;
    private Song songs;
    private int isOutgoing;
    private int isRequestSend = 0;
    private int isAudio;
    private RecyclerView listSongs;
    private CheckBox cbAllCheck;
    private ProgressBar progressBar;
    private List<SelectContact> selectedContacts = new ArrayList<SelectContact>();
    private AppCompatButton submitButton;
    private TextView txtTotalSelected;
    private Boolean isMute = false;
    StringBuilder mFormatBuilder;
    private int isAllCheckedClicked = 1;
    Formatter mFormatter;
    private List<String> selectedPhoneList = new ArrayList<>();
    private AppDatabase appDatabase;
    private int isEdit = 0;
    String[] PERMISSIONS = {Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS,
    };

    public static ContactsFragment newInstance(Song item, int isOutgoing, int isAudio, int isEdit) {
        ContactsFragment fragment = new ContactsFragment();
        fragment.songs = item;
        fragment.isOutgoing = isOutgoing;
        fragment.isAudio = isAudio;
        fragment.isEdit = isEdit;
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            songs = getArguments().getString(SONGPATH);
        }*/
    }

    List<ContactEntity> contactList = new ArrayList<>();
    MyContactsRecyclerViewAdapter myContactsRecyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts_list, container, false);
        apiInterface = ((ApiInterface) APIClient.getClient().create(ApiInterface.class));
        listSongs = view.findViewById(R.id.list_songs);
        progressBar = view.findViewById(R.id.media_player_progress_bar);
        submitButton = view.findViewById(R.id.submit_button);
        cbAllCheck = view.findViewById(R.id.cb_all_check);

        txtTotalSelected = view.findViewById(R.id.txt_total_selected);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        SeparatorDecoration separatorDecoration = new SeparatorDecoration(getActivity(), Color.parseColor("#e8e8e8"), 1.5F);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        cbAllCheck.setOnCheckedChangeListener(this);
        listSongs.setLayoutManager(mLayoutManager);
        listSongs.setItemAnimator(new DefaultItemAnimator());
        listSongs.addItemDecoration(separatorDecoration);
        progressBar.setVisibility(View.VISIBLE);
        appDatabase = AppDatabase.getAppDatabase(getActivity());
        contactList = getAllContatcs();
        Collections.sort(contactList, new NameComparator());
        submitButton.setVisibility(View.GONE);

        if (isEdit == 1)
            cbAllCheck.setVisibility(View.GONE);
        else
            cbAllCheck.setVisibility(View.VISIBLE);
            if (ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(),
                            Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(PERMISSIONS,
                        1);

                progressBar.setVisibility(View.GONE);
            } else {
                if (contactList != null && contactList.size() > 0) {
                    myContactsRecyclerViewAdapter = new MyContactsRecyclerViewAdapter(getActivity(), contactList, this, isEdit);
                    listSongs.setAdapter(myContactsRecyclerViewAdapter);
                } else {
                    getContact();
                }

            }


        submitButton.setOnClickListener(this);
        // Set the adapter
        return view;
    }

    private List<ContactEntity> getContactList(ContactsMedia contactsMedia, Map<String, String> contactMap)

    {
        List<ContactEntity> contactList = new ArrayList<>();
        if (contactsMedia != null) {
            int length = contactsMedia.getBody().size();
            ContactEntity contactEntity;
            for (int i = 0; i < length; i++) {


                contactEntity = new ContactEntity();
                String mobileno = contactsMedia.getBody().get(i).getMobile();
                //   "[^a-zA-Z]+", " "
                String name = contactMap.get(mobileno);
                contactEntity.setNumber(mobileno);
                if (name != null)
                    contactEntity.setName(name);
                else
                    contactEntity.setName(mobileno);


                if (contactsMedia.getBody().get(i).getIncommingother() instanceof Incommingother &&
                        contactsMedia.getBody().get(i).getIncommingother() != null)

                {
                    contactEntity.setIsIncoming(1);

                    if (contactsMedia.getBody().get(i).getIncommingother().getContentType().equals("audio"))
                        contactEntity.setIsincomingVideo(0);
                    else
                        contactEntity.setIsincomingVideo(1);
                    contactEntity.setIncomingSongName(contactsMedia.getBody().get(i).getIncommingother().getTitle());


                } else
                    contactEntity.setIsIncoming(0);
                if (contactsMedia.getBody().get(i).getOutgoingself() instanceof Incommingother &&
                        contactsMedia.getBody().get(i).getIncommingother() != null)

                {
                    contactEntity.setIsOutgoing(1);
                    if (contactsMedia.getBody().get(i).getOutgoingself().getContentType().equals("audio"))
                        contactEntity.setOutgoingIsVideo(0);
                    else
                        contactEntity.setOutgoingIsVideo(1);
                    contactEntity.setOutgoingSongName(contactsMedia.getBody().get(i).getOutgoingself().getTitle());
                } else
                    contactEntity.setIsOutgoing(0);
                contactList.add(contactEntity);


            }
            appDatabase.daoContacts().insertAll(contactList);


        }
        Collections.sort(contactList, new NameComparator());

        return contactList;
    }

    private void parseContacts(String response) {
        Map<String, String> contactMap = new ContactFetcher(getActivity()).fetchAllContact();
        new AsyncTask<Void, Void, List<ContactEntity>>() {
            @Override
            protected List<ContactEntity> doInBackground(Void... voids) {
                List<ContactEntity> list = new ArrayList<>();
                ContactsMedia contactsMedia;


                if (!response.isEmpty()) {
                    Gson gsonObj = new Gson();
                    contactsMedia = gsonObj.fromJson(response, ContactsMedia.class);
                    list = getContactList(contactsMedia, contactMap);
                    //Collections.sort(list, ContactsComparator);
                }
                return list;
            }

            @Override
            protected void onPostExecute(List<ContactEntity> contacts) {
                super.onPostExecute(contacts);
                contactList.addAll(contacts);
                progressBar.setVisibility(View.GONE);
                myContactsRecyclerViewAdapter = new MyContactsRecyclerViewAdapter(getActivity(), contactList, ContactsFragment.this, isEdit);
                listSongs.setAdapter(myContactsRecyclerViewAdapter);

            }
        }.execute();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (contactList != null) {

                for (int i = 0; i < contactList.size(); i++) {
                    ContactEntity contactEntity = contactList.get(i);
                    if (contactEntity.getIsSelcted() == 0) {
                        contactEntity.setIsSelcted(1);
                        contactList.set(i, contactEntity);
                        SelectContact selectContact = new SelectContact(i, contactEntity.getNumber());
                        if (selectedContacts.size() <= contactList.size())
                            selectedContacts.add(selectContact);
                        selectedPhoneList.add(contactEntity.getNumber());
                    }

                }
                submitButton.setVisibility(View.VISIBLE);
                txtTotalSelected.setVisibility(View.VISIBLE);
                txtTotalSelected.setText(selectedContacts.size() + " Selected");
                myContactsRecyclerViewAdapter.updateAdater(contactList, 0);
            }
        } else {
            if (isAllCheckedClicked == 1) {
                for (int i = 0; i < contactList.size(); i++) {

                    ContactEntity contactEntity = contactList.get(i);
                    contactEntity.setIsSelcted(0);
                    contactList.set(i, contactEntity);
                    selectedContacts.clear();
                    selectedPhoneList.clear();
                    submitButton.setVisibility(View.GONE);
                }
            } else
                isAllCheckedClicked = 1;
            myContactsRecyclerViewAdapter.updateAdater(contactList, 0);


        }

    }


    class NameComparator implements Comparator<ContactEntity> {

        @Override
        public int compare(ContactEntity c1, ContactEntity c2) {
            return c1.getName().compareTo(c2.getName()
            );
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit_button:
                requestPermissions();
                //  progressBar.setVisibility(View.VISIBLE);
                //  sendContact();
                break;

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (isRequestSend == 1)
            requestPermissions();
    }

    private void requestPermissions() {


        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getActivity())) {
            isRequestSend = 1;
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getActivity().getPackageName()));
            startActivityForResult(intent, REQUEST_PERMISSIONS_REQUEST_CODE_OVERLAY);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted()) {
            isRequestSend = 1;
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivityForResult(intent, REQUEST_PERMISSIONS_REQUEST_CODE_NOTIFICATION);

        } else if (!isAccessibilitySettingsOn(getActivity())) {
            isRequestSend = 1;
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivityForResult(intent, REQUEST_PERMISSIONS_REQUEST_CODE_ACCESS);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            sendContact();
        }
    }

    public boolean isAccessibilitySettingsOn(Context mContext) {
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


    @Override
    public void setContacts(String phoneNumber, int isAdded, final int position) {
        if (isAdded == 1) {
            if (selectedContacts != null) {
                ContactEntity contactEntity = contactList.get(position);
                contactEntity.setIsSelcted(1);
                contactList.set(position, contactEntity);
                SelectContact selectContact = new SelectContact(position, phoneNumber);
                selectedContacts.add(selectContact);
                selectedPhoneList.add(phoneNumber);
                submitButton.setVisibility(View.VISIBLE);
                txtTotalSelected.setVisibility(View.VISIBLE);
                txtTotalSelected.setText(selectedContacts.size() + " Selected");
            }

        } else if (selectedContacts != null) {
            if (selectedContacts.size() > 0) {
                ContactEntity contactEntity = contactList.get(position);
                contactEntity.setIsSelcted(0);
                contactList.set(position, contactEntity);
                SelectContact selectContact = new SelectContact(position, phoneNumber);
                selectedContacts.remove(selectContact);
                selectedPhoneList.remove(phoneNumber);
                isAllCheckedClicked = 0;
                cbAllCheck.setChecked(false);
                txtTotalSelected.setVisibility(View.VISIBLE);
                txtTotalSelected.setText(selectedContacts.size() + " Selected");
            } else {
                txtTotalSelected.setVisibility(View.GONE);
                submitButton.setVisibility(View.GONE);
            }
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    private void getContact() {
        // SendRequest.sendRequest(Config_URL.);
        SendRequest.sendRequest(ApiConstant.GET_ALL_CONTACT, apiInterface.getAllContatcs(SharedPrefrenceHandler.getInstance().getUSER_TOKEN()), this);
    }


    private void sendContact() {
        SendContactsModel pojoSetMediaRequest = new SendContactsModel();
        if (isOutgoing == 1) {
            pojoSetMediaRequest.setActionType("self");
            pojoSetMediaRequest.setCallType("outgoing");
        }
        if (isOutgoing == 0) {
            pojoSetMediaRequest.setActionType("other");
            pojoSetMediaRequest.setCallType("incomming");
        }
        pojoSetMediaRequest.setMobile(selectedPhoneList);
        pojoSetMediaRequest.setMediaId(songs.getId());
        SendRequest.sendRequest(ApiConstant.SET_MEDIA_FOR_CONTACT, apiInterface.serMediaForUser(SharedPrefrenceHandler.getInstance().getUSER_TOKEN(),
                pojoSetMediaRequest), this);


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        /*if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                requestOtherPermissionsOrLaunchMainActivity();

            } else {
            }
        }*/


        if (requestCode == 1) {
            if (grantResults.length > 0) {


                boolean isAllPermissionGranted = true;
                for (int i = 0; i < PERMISSIONS.length; i++) {
                    if (ContextCompat.checkSelfPermission(getActivity(), PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), PERMISSIONS[i])) {
                            isAllPermissionGranted = false;
                        }
                    }
                }


                if (isAllPermissionGranted) {

                    if (contactList != null && contactList.size() > 0) {
                        myContactsRecyclerViewAdapter = new MyContactsRecyclerViewAdapter(getActivity(), contactList, this, isEdit);
                        listSongs.setAdapter(myContactsRecyclerViewAdapter);
                    } else {
                        getContact();
                    }
                }
            }
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnListFragmentInteractionListener) {
            this = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    List<ContactEntity> contactEntities = new ArrayList<>();

    public List<ContactEntity> getAllContatcs() {

        new AsyncTask<Void, Void, List<ContactEntity>>() {
            @Override
            protected List<ContactEntity> doInBackground(Void... voids) {
                int status = 0;
                return appDatabase.daoContacts().getAll();
            }

            @Override
            protected void onPostExecute(List<ContactEntity> status) {
                super.onPostExecute(status);
                contactEntities = status;


            }
        }.execute();
        return contactEntities;

    }

    private void parseSaveContactResponse(String response) {
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                int status = 0;
                Gson gsonObj = new Gson();
                SaveContactsResponse saveContactsResponse = gsonObj.fromJson(response, SaveContactsResponse.class);
                status = saveContactsResponse.getStatus();
                if (status == 1000) {
                    ArrayList<ContactEntity> contactEntities = new ArrayList<>();
                    for (int i = 0; i < selectedContacts.size(); i++) {
                        ContactEntity contactEntity = contactList.get(selectedContacts.get(i).getId());
                        if (isOutgoing == 1) {

                            RingtoneEntity ringtoneEntity = new RingtoneEntity();
                            if (isAudio == 1)
                                ringtoneEntity.setContentType("audio");
                            else
                                ringtoneEntity.setContentType("video");
                            ringtoneEntity.setMediaId(songs.getId());
                            ringtoneEntity.setActionType("self");
                            ringtoneEntity.setNumber(selectedContacts.get(i).getPhoneNumber());
                            ringtoneEntity.setSampleFileUrl(songs.getSampleFileUrl());
                            long id = appDatabase.daoRingtone().insertAll(ringtoneEntity);
                            if (id == -1) {
                                appDatabase.daoRingtone().update(ringtoneEntity);
                            }


                            if (isAudio == 0)
                                contactEntity.setOutgoingIsVideo(1);
                            else
                                contactEntity.setOutgoingIsVideo(0);
                            contactEntity.setOutgoingSongName(songs.getTitle());
                            contactEntity.setIsOutgoing(1);
                            contactEntity.setOutgoingArtistName(songs.getArtistName());


                        } else {
                            if (isAudio == 0)
                                contactEntity.setOutgoingIsVideo(1);
                            else
                                contactEntity.setOutgoingIsVideo(0);
                            contactEntity.setIncomingSongName(songs.getTitle());
                            contactEntity.setIsIncoming(1);
                            contactEntity.setInComingArtistName(songs.getArtistName());
                        }
                        appDatabase.daoContacts().update(contactEntity);
                    }


                   // appDatabase.daoContacts().update(contactEntities);


                }
                return status;
            }

            @Override
            protected void onPostExecute(Integer status) {
                super.onPostExecute(status);
                if (status == 1000)
                    new DownloadTask(getActivity().getApplicationContext()).execute(songs.getSampleFileUrl());
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Song set  successfully", Toast.LENGTH_LONG).show();


            }
        }.execute();
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {

            File directoryToZip = new File(Utils.getFilePath(context));
            downloadFiles(directoryToZip, sUrl[0]);
            return null;
        }


        private boolean downloadFiles(File directoryToZip, String filePath) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(ApiConstant.MEDIA_URL + filePath);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return true;
                }
                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();
                // download the file
                input = connection.getInputStream();

                File file = new File(directoryToZip.getPath() + File.separator + "" + filePath.split("/")[0]);
                file.mkdirs();
                File outputFile = new File(file, filePath.split("/")[1]);
                output = new FileOutputStream(outputFile);
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        break;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            } finally {
                try {
                    if (output != null) output.close();
                    if (input != null) input.close();
                } catch (IOException ignored) {
                }

                if (connection != null) connection.disconnect();
            }
            return false;
        }
    }


    @Override
    public void getResponse(JsonResponse response, int type) {
        if (response != null && response.getObject() != null &&
                isAdded() && getActivity() != null) {
            switch (type) {
                case ApiConstant.SET_MEDIA_FOR_CONTACT:
                    parseSaveContactResponse(response.getObject());

                    break;
                case ApiConstant.GET_ALL_CONTACT:
                    parseContacts(response.getObject());
                    break;
            }

        }
        else
            progressBar.setVisibility(View.GONE);

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(PojoContacts item);
    }


    /*
     * */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // What i have added is this
        setHasOptionsMenu(true);
    }


    private void filterList(CharSequence constraint) {
        int length = 1;
        List<ContactEntity> contactFilterList = new ArrayList<>();
        if (constraint != null && constraint.length() > 0 && contactList.size() > 0) {
            length = 0;
            //CHANGE TO UPPER
            constraint = constraint.toString().toUpperCase();
            //STORE OUR FILTERED PLAYERS


            for (int i = 0; i < contactList.size(); i++) {
                //CHECK
                if (contactList.get(i).getName().toUpperCase().contains(constraint)
                        || contactList.get(i).getNumber().contains(constraint)) {
                    //ADD PLAYER TO FILTERED PLAYERS
                    contactFilterList.add(contactList.get(i));
                }
            }
        }
        myContactsRecyclerViewAdapter.updateAdater(contactFilterList, length);
        submitButton.setVisibility(View.VISIBLE);


    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        SearchManager searchManager = (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.white));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Contact");


        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                submitButton.setVisibility(View.GONE);
                filterList(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                submitButton.setVisibility(View.GONE);
                filterList(query);
                return false;
            }
        });
    }


}
