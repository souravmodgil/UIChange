package com.mobileoid2.celltone.view.fragments;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.database.AppDatabase;
import com.mobileoid2.celltone.database.ContactEntity;
import com.mobileoid2.celltone.database.RingtoneEntity;
import com.mobileoid2.celltone.network.APIClient;
import com.mobileoid2.celltone.network.ApiConstant;
import com.mobileoid2.celltone.network.ApiInterface;
import com.mobileoid2.celltone.network.NetworkCallBack;
import com.mobileoid2.celltone.network.SendRequest;
import com.mobileoid2.celltone.network.jsonparsing.JsonResponse;
import com.mobileoid2.celltone.network.model.category.CategoryModel;
import com.mobileoid2.celltone.network.model.contacts.SaveContactsResponse;
import com.mobileoid2.celltone.network.model.contacts.SendContactsModel;
import com.mobileoid2.celltone.network.model.treadingMedia.Song;
import com.mobileoid2.celltone.pojo.CategeoryRequest;
import com.mobileoid2.celltone.utility.SharedPrefrenceHandler;
import com.mobileoid2.celltone.utility.Utils;
import com.mobileoid2.celltone.view.SeparatorDecoration;
import com.mobileoid2.celltone.view.activity.ChangeToolBarTitleListiner;
import com.mobileoid2.celltone.view.adapter.CategoriesSongsRecyclerViewAdapter;
import com.mobileoid2.celltone.view.listener.IncomingOutgoingListener;
import com.mobileoid2.celltone.view.listener.OnSongsClickLisner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class SearchFragment extends Fragment implements OnSongsClickLisner, IncomingOutgoingListener,
        View.OnClickListener, NetworkCallBack {


    private RecyclerView listSongs;
    private TextView txtSearchCount,txtSongName;
    private CategoriesSongsRecyclerViewAdapter categoriesSongsRecyclerViewAdapter;
    StringBuilder mFormatBuilder;
    private ProgressBar mediaPlayerProgressBar, progressBar;
    Formatter mFormatter;
    private List<Song> songList;
    private int isAudio;
    private String categoryId;
    private Context context;
    private int postion;
    private android.support.v7.widget.LinearLayoutManager mLayoutManager;
    private int isEdit = 0;
    private String mobileNo = "";
    private String name = "";
    private int isIncoming;
    private ApiInterface apiInterface;
    private ContactEntity contactEntity;
    private String mediaId = "";
    private String sampleUrl = "";
    private AppDatabase appDatabase;
    private int currentSongPostion;
    private ChangeToolBarTitleListiner changeToolBarTitleListiner;
    private String type ="";
    private String terms = "";

    public static SearchFragment newInstance(Context context, List<Song> songList, int isAudio,
                                             String type, String categoryId, int postion,
                                             int isEdit, String mobileNo, String name, int isIncoming,
                                             ContactEntity contactEntity, ChangeToolBarTitleListiner changeToolBarTitleListiner,String terms) {
        SearchFragment fragment = new SearchFragment();
        fragment.context = context;
        fragment.songList = songList;
        fragment.isAudio = isAudio;
        fragment.categoryId = categoryId;
        fragment.terms =terms;
        fragment.type = type;
        fragment.postion = postion;
        fragment.isEdit = isEdit;
        fragment.mobileNo = mobileNo;
        fragment.isIncoming = isIncoming;
        fragment.name = name;
        fragment.changeToolBarTitleListiner = changeToolBarTitleListiner;
        fragment.contactEntity = contactEntity;
        return fragment;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = getView() != null ? getView() : inflater.inflate(R.layout.fragemnt_search, container, false);
        apiInterface = ((ApiInterface) APIClient.getClient().create(ApiInterface.class));
        appDatabase = AppDatabase.getAppDatabase(getActivity());
        listSongs = view.findViewById(R.id.list_songs);
        txtSearchCount =view.findViewById(R.id.txt_search_count);
        txtSongName = view.findViewById(R.id.txt_song_name);
        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        getSongList();
       // initalizeView();

          return view;
    }

    private void initalizeView() {
        SeparatorDecoration separatorDecoration = new SeparatorDecoration(getActivity(), Color.parseColor("#e8e8e8"), 1.5F);
        mLayoutManager = new LinearLayoutManager(getActivity());
        listSongs.setLayoutManager(mLayoutManager);
        listSongs.setItemAnimator(new DefaultItemAnimator());
        listSongs.addItemDecoration(separatorDecoration);
        categoriesSongsRecyclerViewAdapter = new CategoriesSongsRecyclerViewAdapter(context, songList, isAudio, this, this,
                isEdit);
        listSongs.setAdapter(categoriesSongsRecyclerViewAdapter);



    }

    private void getSongList() {

        CategeoryRequest categeoryRequest = new CategeoryRequest();
        categeoryRequest.setLimit(0);
        categeoryRequest.setSkip(0);
        categeoryRequest.setCategoryId(categoryId);
        categeoryRequest.setRequiredActive(true);
        categeoryRequest.setOrderby("createdAt");
        if (isAudio == 1)
            categeoryRequest.setMediaType("audio");
        else
            categeoryRequest.setMediaType("video");
        categeoryRequest.setTerm(terms);

        SendRequest.sendRequest(ApiConstant.VIDEOAPI, apiInterface.getSongs(SharedPrefrenceHandler.getInstance().getUSER_TOKEN(),
                categeoryRequest), this);

    }





    @Override
    public void setIncomingOutComingListener(int callType, Song song) {
        setRingTone(callType, song);
    }

    private void sendContact(String id) {
        progressBar.setVisibility(View.VISIBLE);
        SendContactsModel pojoSetMediaRequest = new SendContactsModel();
        if (isIncoming == 0) {
            pojoSetMediaRequest.setActionType("self");
            pojoSetMediaRequest.setCallType("outgoing");
        }
        if (isIncoming == 1) {
            pojoSetMediaRequest.setActionType("other");
            pojoSetMediaRequest.setCallType("incomming");
        }
        List<String> selectedPhoneList = new ArrayList<>();
        selectedPhoneList.add(mobileNo);
        pojoSetMediaRequest.setMobile(selectedPhoneList);
        pojoSetMediaRequest.setMediaId(id);
        SendRequest.sendRequest(ApiConstant.SET_MEDIA_FOR_CONTACT, apiInterface.serMediaForUser(SharedPrefrenceHandler.getInstance().getUSER_TOKEN(),
                pojoSetMediaRequest), this);


    }


    @Override
    public void setMedia(String id, String url, int songPostion) {
        mediaId = id;
        this.sampleUrl = sampleUrl;
        currentSongPostion = songPostion;
        sendContact(id);
    }

    private void parseSong(String response) {
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(getList(response)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(UploadMediaObserver()));

    }

    private DisposableObserver<CategoryModel> UploadMediaObserver() {
        return new DisposableObserver<CategoryModel>() {

            @Override
            public void onNext(CategoryModel modle) {
                if (modle.getBody() != null) {

                    songList = modle.getBody().getData();
                    initalizeView();
                }
                progressBar.setVisibility(View.GONE);


            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    private Observable<CategoryModel> getList(String response) {
        Gson gsonObj = new Gson();
        final CategoryModel categoryModel = gsonObj.fromJson(response, CategoryModel.class);

        return Observable.create(new ObservableOnSubscribe<CategoryModel>() {
            @Override
            public void subscribe(ObservableEmitter<CategoryModel> emitter) throws Exception {
                if (!emitter.isDisposed()) {
                    emitter.onNext(categoryModel);
                    emitter.onComplete();
                }


            }
        });
    }


    @Override
    public void getResponse(JsonResponse response, int type) {
        if (response != null && response.getObject() != null &&
                isAdded() && getActivity() != null) {
            switch (type) {
                case ApiConstant.SET_MEDIA_FOR_CONTACT:
                    parseSaveContactResponse(response.getObject());
                    break;
                case ApiConstant.VIDEOAPI:
                    parseSong(response.getObject());
                    break;


            }

        } else
            progressBar.setVisibility(View.GONE);

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


                    if (isIncoming == 0) {

                        RingtoneEntity ringtoneEntity = new RingtoneEntity();
                        if (isAudio == 1)
                            ringtoneEntity.setContentType("audio");
                        else
                            ringtoneEntity.setContentType("video");
                        ringtoneEntity.setMediaId(mediaId);
                        ringtoneEntity.setActionType("self");
                        ringtoneEntity.setNumber(mobileNo);
                        ringtoneEntity.setSampleFileUrl(sampleUrl);
                        long id = appDatabase.daoRingtone().insertAll(ringtoneEntity);
                        if (id == -1) {
                            appDatabase.daoRingtone().update(ringtoneEntity);
                        }


                        if (isAudio == 0)
                            contactEntity.setOutgoingIsVideo(1);
                        else
                            contactEntity.setOutgoingIsVideo(0);
                        contactEntity.setOutgoingSongName(songList.get(currentSongPostion).getTitle());
                        contactEntity.setIsOutgoing(1);
                        contactEntity.setOutgoingArtistName(songList.get(currentSongPostion).getArtistName());


                    } else {
                        if (isAudio == 0)
                            contactEntity.setOutgoingIsVideo(1);
                        else
                            contactEntity.setOutgoingIsVideo(0);
                        contactEntity.setIncomingSongName(songList.get(currentSongPostion).getTitle());
                        contactEntity.setIsIncoming(1);
                        contactEntity.setInComingArtistName(songList.get(currentSongPostion).getArtistName());

                    }
                }


                appDatabase.daoContacts().update(contactEntity);


                return status;
            }

            @Override
            protected void onPostExecute(Integer status) {
                super.onPostExecute(status);
                if (status == 1000)
                    new DownloadTask(getActivity().getApplicationContext()).execute(sampleUrl);
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







    private void showPopup(View view)

    {
        Song song = songList.get(postion);
        PopupMenu popup = new PopupMenu(getActivity(), view);
        /** Adding menu items to the popumenu */
        popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
        /** Defining menu item click listener for the popup menu */
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //Toast.makeText(mcontext, "You selected the action : " + item.getTitle(), Toast.LENGTH_SHORT).show();

                switch (item.getItemId()) {
                    case R.id.nav_incoming:
                        setRingTone(0, song);
                        break;
                    case R.id.nav_outgoing:
                        setRingTone(1, song);
                        break;
                }
                return true;
            }
        });
        /** Showing the popup menu */
        popup.show();
    }

    private void setRingTone(int callType, Song song) {

        // Intent intent = new Intent(getActivity(), ContactActivity.class);
        if (callType == 1)
            changeToolBarTitleListiner.setTitle("Set Ringtone" + "(Outgoing)", song.getTitle());
        else
            changeToolBarTitleListiner.setTitle("Set Ringtone" + "(Incoming)", song.getTitle());
        Fragment fragment = ContactsFragment.newInstance(song, callType, isAudio, 0);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.addToBackStack(fragment.getClass().getName()).commitAllowingStateLoss();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.icon_add_tone:
                showPopup(view);
                break;

        }

    }



    @Override
    public void playSong(int poistion, int isPlay, String type) {

        }

    }
