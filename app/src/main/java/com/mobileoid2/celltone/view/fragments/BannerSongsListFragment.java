package com.mobileoid2.celltone.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.database.AppDatabase;
import com.mobileoid2.celltone.network.APIClient;
import com.mobileoid2.celltone.network.ApiConstant;
import com.mobileoid2.celltone.network.ApiInterface;
import com.mobileoid2.celltone.network.model.treadingMedia.Song;
import com.mobileoid2.celltone.view.SeparatorDecoration;
import com.mobileoid2.celltone.view.activity.ContactActivity;
import com.mobileoid2.celltone.view.adapter.CategoriesSongsRecyclerViewAdapter;
import com.mobileoid2.celltone.view.listener.IncomingOutgoingListener;
import com.mobileoid2.celltone.view.listener.OnSongsClickLisner;

import java.util.Formatter;
import java.util.List;
import java.util.Locale;

public class BannerSongsListFragment extends Fragment implements OnSongsClickLisner, IncomingOutgoingListener, SeekBar.OnSeekBarChangeListener,
        View.OnClickListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {


    private RecyclerView listSongs;
    private VideoView videoView;
    private ImageView preview, previous, playButton, playNext, iconAddTone;
    private TextView txtSongDuration, txtCurrentTime, txtTitle, txtArtistName;
    private ImageButton imageButtonMute;
    private SeekBar seekBar;
    private int postion =0;
    private LinearLayout llMediaButton;
    private int isVideoPlaying = 0;
    private Boolean isMute = false;
    StringBuilder mFormatBuilder;
    private ProgressBar mediaPlayerProgressBar, progressBar;
    Formatter mFormatter;
    private List<Song> songList;
    private int isAudio;
    private int isMediaCompleted = 0;

    private Context context;

    private String videoPath = "";
    private String clipArt = "";
    private int isPause = 0;
    private Handler threadHandler = new Handler();
    private String title;
    private int currentVolume;
    private int songPostion = 0;
    private String artistName;
    private UpdateSeekBarThread updateSeekBarThread;
    private RecyclerView.LayoutManager mLayoutManager;


    private ApiInterface apiInterface;
    private String mediaId = "";
    private String sampleUrl = "";
    private AppDatabase appDatabase;
    private int currentSongPostion;

    public static BannerSongsListFragment newInstance(Context context, List<Song> songList
                                                   ) {
        BannerSongsListFragment fragment = new BannerSongsListFragment();
        fragment.context = context;
        fragment.songList = songList;


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
        isAudio = songList.get(postion).getIsAudio();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        SeparatorDecoration separatorDecoration = new SeparatorDecoration(getActivity(), Color.parseColor("#e8e8e8"), 1.5F);
        mLayoutManager = new LinearLayoutManager(context);
        listSongs.setLayoutManager(mLayoutManager);
        listSongs.setItemAnimator(new DefaultItemAnimator());
        listSongs.addItemDecoration(separatorDecoration);
        listSongs.setAdapter(new CategoriesSongsRecyclerViewAdapter(context, songList, isAudio, this, this,
                0));
        videoPath = songList.get(postion).getSampleFileUrl();
        clipArt = songList.get(postion).getClipArtUrl();

        title = songList.get(postion).getTitle();
        artistName = songList.get(postion).getArtistName();

        if (songPostion == 0 && songList.size() > 0) {
            previous.setEnabled(false);

        }
        playVideo();
        if (isAudio == 1)
            preview.setVisibility(View.VISIBLE);
        else
            preview.setVisibility(View.GONE);

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



    @Override
    public void setMedia(String id, String url, int songPostion) {
        mediaId = id;
        this.sampleUrl = sampleUrl;
        currentSongPostion = songPostion;
       // sendContact(id);
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

        Intent intent = new Intent(getActivity(), ContactActivity.class);
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