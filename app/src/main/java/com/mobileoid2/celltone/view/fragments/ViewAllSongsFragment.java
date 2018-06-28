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
import com.mobileoid2.celltone.network.model.feedback.FeedBackModel;
import com.mobileoid2.celltone.network.model.treadingMedia.Song;
import com.mobileoid2.celltone.pojo.CategeoryRequest;
import com.mobileoid2.celltone.pojo.QUERYREQUEST;
import com.mobileoid2.celltone.utility.SharedPrefrenceHandler;
import com.mobileoid2.celltone.utility.Utils;
import com.mobileoid2.celltone.view.SeparatorDecoration;
import com.mobileoid2.celltone.view.activity.ChangeToolBarTitleListiner;
import com.mobileoid2.celltone.view.adapter.CategoriesSongsRecyclerViewAdapter;
import com.mobileoid2.celltone.view.adapter.QueryListAdapter;
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

public class ViewAllSongsFragment extends Fragment implements OnSongsClickLisner, IncomingOutgoingListener, SeekBar.OnSeekBarChangeListener,
        View.OnClickListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, NetworkCallBack {


    private RecyclerView listSongs;
    private VideoView videoView;
    private  CategoriesSongsRecyclerViewAdapter  categoriesSongsRecyclerViewAdapter;
    private ImageView preview, previous, playButton, playNext, iconAddTone;
    private TextView txtSongDuration, txtCurrentTime, txtTitle, txtArtistName;
    private ImageButton imageButtonMute;
    private SeekBar seekBar;
    private LinearLayout llMediaButton;
    private int noCount =1;
    private boolean loading = true;
    private int isVideoPlaying = 0;
    private Boolean isMute = false;
    StringBuilder mFormatBuilder;
    private ProgressBar mediaPlayerProgressBar, progressBar;
    Formatter mFormatter;
    private List<Song> songList;
    private int isAudio;
    private String type;
    private int isMediaCompleted = 0;
    private String categoryId;
    private Context context;
    private int postion;
    private String videoPath = "";
    private String clipArt = "";
    private int isPause = 0;
    private Handler threadHandler = new Handler();
    private String title;
    private int currentVolume;
    private int songPostion = 0;
    private String artistName;
    private UpdateSeekBarThread updateSeekBarThread;
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
    private  ChangeToolBarTitleListiner changeToolBarTitleListiner;
    int skip =0;
    int limit =10;
    private int noOfPages =0;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    public static ViewAllSongsFragment newInstance(Context context, List<Song> songList, int isAudio,
                                                   String type, String categoryId, int postion,
                                                   int isEdit, String mobileNo, String name, int isIncoming,
                                                   ContactEntity contactEntity, ChangeToolBarTitleListiner changeToolBarTitleListiner) {
        ViewAllSongsFragment fragment = new ViewAllSongsFragment();
        fragment.context = context;
        fragment.songList = songList;
        fragment.isAudio = isAudio;
        fragment.categoryId = categoryId;
        fragment.type = type;
        fragment.postion = postion;
        fragment.isEdit = isEdit;
        fragment.mobileNo = mobileNo;
        fragment.isIncoming = isIncoming;
        fragment.name = name;
        fragment.changeToolBarTitleListiner=changeToolBarTitleListiner;
        fragment.contactEntity = contactEntity;
        return fragment;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = getView() != null ? getView() : inflater.inflate(R.layout.fragment_view_all, container, false);
        apiInterface = ((ApiInterface) APIClient.getClient().create(ApiInterface.class));
        appDatabase = AppDatabase.getAppDatabase(getActivity());
        listSongs = view.findViewById(R.id.list_songs);
        videoView = view.findViewById(R.id.video_view);
        previous = view.findViewById(R.id.previous);
        playNext = view.findViewById(R.id.play_next);
        preview = view.findViewById(R.id.preview);
        txtArtistName = view.findViewById(R.id.txt_artist_name);
        playButton = view.findViewById(R.id.play_button);
        playNext = view.findViewById(R.id.play_next);
        iconAddTone = view.findViewById(R.id.icon_add_tone);
        llMediaButton = view.findViewById(R.id.ll_media_button);
        txtSongDuration = view.findViewById(R.id.txt_song_duration);
        txtCurrentTime = view.findViewById(R.id.txt_current_time);
        imageButtonMute = view.findViewById(R.id.image_button_mute);
        txtTitle = view.findViewById(R.id.txt_title);
        seekBar = view.findViewById(R.id.seek_bar);
        mediaPlayerProgressBar = view.findViewById(R.id.media_player_progress_bar);
        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        videoPath = songList.get(postion).getSampleFileUrl();
        clipArt = songList.get(postion).getClipArtUrl();

        title = songList.get(postion).getTitle();
        artistName = songList.get(postion).getArtistName();
        if (songPostion == 0 && songList.size() > 0) {
            previous.setEnabled(false);

        }
        initalizeView();

        iconAddTone.setOnClickListener(this);
        previous.setOnClickListener(this);
        playNext.setOnClickListener(this);
        videoView.setOnCompletionListener(this);
        videoView.setOnErrorListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // TODO Auto-generated method stub
                if (isAudio == 1)
                    mediaPlayerProgressBar.setVisibility(View.GONE);
                videoView.start();
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int arg1,
                                                   int arg2) {
                        // TODO Auto-generated method stub
                        mediaPlayerProgressBar.setVisibility(View.GONE);
                        videoView.start();
                    }
                });
            }
        });
        //  listSongs.addOnScrollListener(this);
        playButton.setOnClickListener(this);
        imageButtonMute.setOnClickListener(this);
        return view;
    }
    private void initalizeView()
    {
        SeparatorDecoration separatorDecoration = new SeparatorDecoration(getActivity(), Color.parseColor("#e8e8e8"), 1.5F);
        mLayoutManager = new LinearLayoutManager(getActivity());
        listSongs.setLayoutManager(mLayoutManager);
        listSongs.setItemAnimator(new DefaultItemAnimator());
        listSongs.addItemDecoration(separatorDecoration);
        categoriesSongsRecyclerViewAdapter = new CategoriesSongsRecyclerViewAdapter(context, songList, isAudio, this, this,
                isEdit);
        listSongs.setAdapter(categoriesSongsRecyclerViewAdapter);
        playVideo();
        if (isAudio == 1)
            preview.setVisibility(View.VISIBLE);
        else
            preview.setVisibility(View.GONE);


        listSongs.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (loading & noCount>skip)  {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            skip = skip + limit;
                            getSongList();

                        }
                    }
                }
            }
        });

    }
    private void getSongList()
    {

        CategeoryRequest categeoryRequest = new CategeoryRequest();
        categeoryRequest.setLimit(limit);
        categeoryRequest.setSkip(skip);
        categeoryRequest.setCategoryId(categoryId);
        categeoryRequest.setRequiredActive(true);
        categeoryRequest.setOrderby("createdAt");
        if(isAudio==1)
            categeoryRequest.setMediaType("audio");
        else
            categeoryRequest.setMediaType("video");
        categeoryRequest.setTerm("");

        SendRequest.sendRequest(ApiConstant.VIDEOAPI, apiInterface.getSongs(SharedPrefrenceHandler.getInstance().getUSER_TOKEN(),
                categeoryRequest), this);

    }


    private String millisecondsToString(int milliseconds) {
        int totalSeconds = milliseconds / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }

    }

    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
//            int visibleItemCount = mLayoutManager.getChildCount();
//            int totalItemCount = mLayoutManager.getItemCount();
//            int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
//
//            if (!isLoading && !isLastPage) {
//                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
//                        && firstVisibleItemPosition >= 0
//                        && totalItemCount >= PAGE_SIZE) {
//                    loadMoreItems();
//                }
//            }
        }
    };

    private void playVideo() {
        Glide.with(context)
                .load(ApiConstant.MEDIA_URL + clipArt)
                .into(preview);

        mediaPlayerProgressBar.setVisibility(View.VISIBLE);
        // The duration in milliseconds
        txtTitle.setText(title);
        txtArtistName.setText(artistName);
        Uri vidUri = Uri.parse(ApiConstant.MEDIA_URL + videoPath);
        videoView.setVideoURI(vidUri);
        // videoView.setVideoPath(Config_URL.MEDIA_URL + videoPath);
        // this.videoView.start();
        int duration = videoView.getDuration();
        playButton.setImageResource(R.drawable.pause_icon);
        int currentPosition = videoView.getCurrentPosition();
        seekBar.setMax(duration);
        String maxTimeString = this.millisecondsToString(duration);
        txtSongDuration.setText(maxTimeString);


        // Create a thread to update position of SeekBar.
        updateSeekBarThread = new UpdateSeekBarThread();
        threadHandler.postDelayed(updateSeekBarThread, 50);


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
    private void  parseSong(String response)
    {
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(getSongList(response)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(UploadMediaObserver()));

    }
    private DisposableObserver<CategoryModel> UploadMediaObserver() {
        return new DisposableObserver<CategoryModel>() {

            @Override
            public void onNext(CategoryModel modle) {
                if(modle.getBody()!=null) {

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    noCount = modle.getBody().getCount();
                    categoriesSongsRecyclerViewAdapter.updateAdapter(modle.getBody().getData());

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

    private Observable<CategoryModel> getSongList(String response) {
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


    class UpdateSeekBarThread implements Runnable {

        public void run() {
            try {


                int currentPosition = videoView.getCurrentPosition();
                String currentPositionStr = millisecondsToString(currentPosition);

                seekBar.setProgress(currentPosition);
                seekBar.setMax(videoView.getDuration());
                txtCurrentTime.setText(currentPositionStr);
                txtSongDuration.setText(millisecondsToString(videoView.getDuration()));

                // Delay thread 50 milisecond.
                threadHandler.postDelayed(this, 50);
            } catch (Exception ex) {

            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        threadHandler.removeCallbacks(updateSeekBarThread);
        //  threadHandler.postDelayed(updateSeekBarThread, 5000);
        if (videoView != null)
            videoView.stopPlayback();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            // We're not interested in programmatically generated changes to
            // the progress bar's position.
            return;
        }

        long duration = videoView.getDuration();
        long newposition = (progress);
        videoView.seekTo((int) newposition);
        if (txtCurrentTime != null)
            txtCurrentTime.setText(millisecondsToString((int) newposition));

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        playButton.setImageResource(R.drawable.play_icon);

        int position = videoView.getCurrentPosition();
        int duration = videoView.getDuration();
        if (seekBar != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 1000L * position / duration;
                seekBar.setProgress((int) pos);
            }
            int percent = videoView.getBufferPercentage();
            seekBar.setSecondaryProgress(percent * 10);
        }

        if (txtSongDuration != null)
            txtSongDuration.setText(millisecondsToString(duration));
        if (txtCurrentTime != null)
            txtCurrentTime.setText(millisecondsToString(position));


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
        if(callType==1)
        changeToolBarTitleListiner.setTitle("Set Ringtone"+"(Outgoing)",song.getTitle());
        else
            changeToolBarTitleListiner.setTitle("Set Ringtone"+"(Incoming)",song.getTitle());
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
            case R.id.play_next:
                if (songPostion <= songList.size() - 1 && songList.size() > 0) {
                    isMediaCompleted = 0;
                    playNext.setEnabled(true);
                    previous.setEnabled(true);
                    songPostion = songPostion + 1;
                    Song song = songList.get(songPostion);
                    videoPath = song.getSampleFileUrl();
                    clipArt = song.getClipArtUrl();
                    title = song.getTitle();
                    artistName = song.getArtistName();

                    if (videoView.isPlaying())
                        videoView.stopPlayback();
                    if (isAudio == 0)
                        preview.setVisibility(View.GONE);
                    else
                        preview.setVisibility(View.VISIBLE);
                    isVideoPlaying = 0;
                    playVideo();


                } else {
                    playNext.setEnabled(false);
                    previous.setEnabled(true);
                }

                break;
            case R.id.previous:
                if (songPostion > 0 && songList.size() > 0) {
                    isMediaCompleted = 0;
                    previous.setEnabled(true);
                    songPostion = songPostion - 1;
                    Song song = songList.get(songPostion);
                    videoPath = song.getSampleFileUrl();
                    clipArt = song.getClipArtUrl();
                    title = song.getTitle();
                    artistName = song.getArtistName();

                    if (videoView.isPlaying())
                        videoView.stopPlayback();
                    if (isAudio == 0)
                        preview.setVisibility(View.GONE);
                    else
                        preview.setVisibility(View.VISIBLE);

                    isVideoPlaying = 0;
                    playVideo();


                } else {
                    playNext.setEnabled(true);
                    previous.setEnabled(false);
                }
                break;
            case R.id.play_button:
                if (isPause == 0) {
                    isPause = 1;
                    playButton.setImageResource(R.drawable.pause_icon);
                    if (isMediaCompleted == 0)
                        videoView.resume();
                    else {
                        isMediaCompleted = 0;
                        videoView.start();
                    }


                } else {
                    isPause = 0;
                    playButton.setImageResource(R.drawable.play_icon);
                    videoView.pause();
                }
                break;
            case R.id.image_button_mute:
                if (!isMute) {
                    AudioManager mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                    currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    isMute = true;
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                    imageButtonMute.setImageDrawable(getResources().getDrawable(R.mipmap.mute_off));
                } else {
                    imageButtonMute.setImageDrawable(getResources().getDrawable(R.mipmap.mute));
                    AudioManager mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                    isMute = false;
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
                }
                break;
        }

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playButton.setImageResource(R.drawable.play_icon);
        videoView.stopPlayback();
        isPause = 0;
        isMediaCompleted = 1;

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return true;
    }


    @Override
    public void playSong(int poistion, int isPlay, String type) {
        postion = poistion;
        videoPath = songList.get(poistion).getSampleFileUrl();
        clipArt = songList.get(poistion).getClipArtUrl();
        title = songList.get(poistion).getTitle();
        artistName = songList.get(poistion).getArtistName();
        playButton.setImageResource(R.drawable.pause_icon);
        isPause = 0;

        if (isAudio == 0) {
            if (videoView.isPlaying() || videoView != null)
                videoView.stopPlayback();
            preview.setVisibility(View.GONE);
            isVideoPlaying = 0;
            isMediaCompleted = 0;
            threadHandler.removeCallbacks(updateSeekBarThread);
            threadHandler.postDelayed(updateSeekBarThread, 5000);
            playVideo();


        } else {
            if (videoView.isPlaying() || videoView != null)
                videoView.stopPlayback();
            isMediaCompleted = 0;
            preview.setVisibility(View.VISIBLE);
            threadHandler.removeCallbacks(updateSeekBarThread);
            threadHandler.postDelayed(updateSeekBarThread, 5000);
            Glide.with(context)
                    .load(ApiConstant.MEDIA_URL + clipArt)
                    .into(preview);
            playVideo();


        }

    }
}
