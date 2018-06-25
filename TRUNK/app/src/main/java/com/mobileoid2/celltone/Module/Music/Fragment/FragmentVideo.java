package com.mobileoid2.celltone.Module.Music.Fragment;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.mobileoid2.celltone.CustomWidget.Dialog.BeanDialogsOption;
import com.mobileoid2.celltone.CustomWidget.Dialog.DialogsCustom;
import com.mobileoid2.celltone.CustomWidget.TextView.TextVeiwEuro55Regular;
import com.mobileoid2.celltone.Module.Base.ActivityBase;
import com.mobileoid2.celltone.Module.Contacts.Bean.BeanContacts;
import com.mobileoid2.celltone.Module.Music.Adapter.AdapterMusicList;
import com.mobileoid2.celltone.Module.Music.Bean.Music;
import com.mobileoid2.celltone.Module.Music.Interface.InterfaceSongSelected;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.Util.AppLevelConstraints;
import com.mobileoid2.celltone.Util.AppSharedPref;
import com.mobileoid2.celltone.Util.AppUtils;
import com.mobileoid2.celltone.Util.VideoPlayer;
import com.mobileoid2.celltone.database.AppDatabase;
import com.mobileoid2.celltone.database.AsyncTaskUpdateMusicDb;
import com.mobileoid2.celltone.database.DatabaseConstants;
import com.mobileoid2.celltone.database.InterfaceOperation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;


public class FragmentVideo extends Fragment implements InterfaceSongSelected {


    private Activity activity;
    private View view;
    private String TAG = FragmentSongs.class.toString();
    private List<Music> audioList = new ArrayList<Music>();
    private RecyclerView listSongs;
    private AdapterMusicList adapterSongList;
    private int selectedSongPosition = -1;

    private ImageButton imageButtonMute, imageButtonAssignUser;

    private TextVeiwEuro55Regular textViewSongName;
    private boolean isMute = false;
    private int currentVolume;
    private HashSet<BeanContacts> selectedPositions;

    private VideoView videoView;
    private Music currentMusic;
    private SmoothProgressBar smoothProgressBar;

    private boolean listIsEmpty = false;
    private videofetcher videoFetcher;

    public static FragmentVideo newInstance(Music music, HashSet<BeanContacts> selectedPositions) {
        FragmentVideo fragment = new FragmentVideo();
        fragment.selectedPositions = selectedPositions;
        fragment.currentMusic = music;
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
            view = inflater.inflate(R.layout.fragment_video, container, false);
            setHeader();
            smoothProgressBar = (SmoothProgressBar) view.findViewById(R.id.custom_progressbar);

            listSongs = view.findViewById(R.id.list_songs);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            listSongs.setLayoutManager(mLayoutManager);
            listSongs.setItemAnimator(new DefaultItemAnimator());


            DividerItemDecoration divider = new DividerItemDecoration(activity.getApplicationContext(), DividerItemDecoration.VERTICAL);
            divider.setDrawable(ContextCompat.getDrawable(activity.getApplicationContext(), R.drawable.divider_list));
            listSongs.addItemDecoration(divider);


            imageButtonMute = view.findViewById(R.id.image_button_mute);
            imageButtonAssignUser = view.findViewById(R.id.image_button_assign_user);

            textViewSongName = view.findViewById(R.id.textview_selected_music_name);


            videoView = view.findViewById(R.id.video_view);

            VideoPlayer.getInstance(activity, videoView);


            VideoPlayer.getMediaController().setPrevNextListeners(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //next button clicked
                    if (selectedSongPosition + 1 < audioList.size()) {
                        selectedSongPosition = selectedSongPosition + 1;
                        playVideo();
                    }
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //previous button clicked
                    if (selectedSongPosition - 1 > -1) {
                        selectedSongPosition = selectedSongPosition - 1;
                        playVideo();
                    }
                }
            });


            imageButtonAssignUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedSongPosition != -1)
                        onSongSelected(selectedSongPosition, true);
                }
            });


            imageButtonMute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!isMute) {
                        AudioManager mAudioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
                        currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                        isMute = true;
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                        imageButtonMute.setImageDrawable(getResources().getDrawable(R.mipmap.mute_off));
                    } else {
                        imageButtonMute.setImageDrawable(getResources().getDrawable(R.mipmap.mute));
                        AudioManager mAudioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
                        isMute = false;
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
                    }
                }
            });


            setProgressbar(true);


            listSongs.setOnFlingListener(new RecyclerView.OnFlingListener() {
                @Override
                public boolean onFling(int velocityX, int velocityY) {

                    VideoPlayer.getMediaController().hide();
                    return false;
                }
            });

            new AsyncTask() {

                @Override
                protected Object doInBackground(Object[] objects) {

                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);

                    videoFetcher = new videofetcher();
                    videoFetcher.execute();
                }
            }.execute();


        } catch (Exception e) {
            Log.e(TAG, AppUtils.instance.getExceptionString(e));
        }
        return view;
    }

    public void setProgressbar(boolean isVisible) {
        if (smoothProgressBar == null) return;
        if (isVisible) {
            smoothProgressBar.setVisibility(View.VISIBLE);
            smoothProgressBar.setSmoothProgressDrawableInterpolator(new AnticipateInterpolator());
            smoothProgressBar.setSmoothProgressDrawableColors(getResources().getIntArray(R.array.gplus_colors));
            smoothProgressBar.setSmoothProgressDrawableUseGradients(true);
        } else {
            smoothProgressBar.setVisibility(View.GONE);
        }

    }

    public void setCurrentMusic(Music music) {
        currentMusic = music;
        if (currentMusic != null)
            if (audioList.contains(currentMusic)) {
                selectedSongPosition = audioList.indexOf(currentMusic);
                playVideo();
            } else
                Toast.makeText(activity, R.string.text_selected_music_no_longer, Toast.LENGTH_SHORT).show();
    }

    private void setHeader() throws Exception {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, AppSharedPref.instance.getScreenHeight() * 2 / 5);
        ((RelativeLayout) view.findViewById(R.id.header)).setLayoutParams(params);
    }

    private void setList() {
        if (view == null) return;

        Collections.sort(audioList);
        if (adapterSongList == null) {
            adapterSongList = new AdapterMusicList(getActivity(), audioList, this, false);
            listSongs.setAdapter(adapterSongList);
        } else {
            adapterSongList.refreshList(audioList);
        }
        ((ActivityBase) activity).setCurrentMusicList(audioList);
        ((ActivityBase) activity).setVideoList(audioList);
        setProgressbar(false);
    }

    @Override
    public void onSongSelected(Music music) {

    }

    @Override
    public void onSongSelected(int position, boolean isAssign) {
        selectedSongPosition = position;
        if (isAssign) {
            //assign song to a contact
            selectIncomingOrOutgoing(audioList.get(selectedSongPosition));

        } else {
            listSongs.scrollToPosition(0);
            ((AppBarLayout) view.findViewById(R.id.app_bar)).setExpanded(true);
            playVideo();
        }
    }

    private void playVideo() {

        if (videoView == null) return;
        if (videoView.isPlaying()) videoView.stopPlayback();
        if (audioList.size() == 0) return;
        if (selectedSongPosition == -1) selectedSongPosition = 0;

        videoView.setVideoPath(audioList.get(selectedSongPosition).getSongsPath());
        videoView.start();
        textViewSongName.setText(audioList.get(selectedSongPosition).getSongTitle());
        textViewSongName.setSelected(true);

    }

    @Override
    public void onDetach() {
        if (videoFetcher != null)
            if (videoFetcher.getStatus() == AsyncTask.Status.RUNNING)
                videoFetcher.cancel(true);


        if (adapterSongList != null) adapterSongList.cleanCacheWork();
        super.onDetach();
        VideoPlayer.stop();
        audioList = null;
    }

    class videofetcher extends AsyncTask<String, Void, Void> {

        private List<Music> newList = new ArrayList<Music>();
        private List<Music> oldList = new ArrayList<Music>();

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

            oldList = AppDatabase.getAppDatabase(AppLevelConstraints.getAppContext()).daoMusic().getAll(DatabaseConstants.VALUE_TRUE);

            // database list is blank so return
            if (oldList == null) oldList = new ArrayList<Music>();
            if (oldList.size() > 0) {
                audioList.addAll(oldList);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setList();
                        setCurrentMusic(currentMusic);
                    }
                });

            } else {
                listIsEmpty = true;
            }


            try {
                String cols[] = {MediaStore.Video.Media.ALBUM,
                        MediaStore.Video.Media.TITLE,
                        MediaStore.Video.Media.DATA,
                        MediaStore.Video.Media._ID,
                        MediaStore.Video.Media.DURATION,
                };

                Cursor cursor = AppLevelConstraints.getAppContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, cols, null, null, null);
                getNewVideoListToAdd(cursor);
                if (cursor != null)
                    cursor.close();
                cursor = AppLevelConstraints.getAppContext().getContentResolver().query(MediaStore.Video.Media.INTERNAL_CONTENT_URI, cols, null, null, null);
                getNewVideoListToAdd(cursor);
                if (cursor != null)
                    cursor.close();

            } catch (Exception e) {
                Log.e("Update Video ", AppUtils.instance.getExceptionString(e));
            }
        }

        private List<Music> getNewVideoListToAdd(Cursor cursor) throws Exception {

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    if (this.isCancelled()) {
                        newList.clear();
                        audioList.clear();
                        return null;
                    }

                    Music music = new Music();
                    music.setSongTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));

                    if (!oldList.contains(music)) {
                        music = fillVideoDataIntoMusic(music, cursor);
                        newList.add(music);
                        if (listIsEmpty) {
                            if (newList.size() % 100 == 0) {
                                audioList.clear();
                                audioList.addAll(newList);
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setList();
                                    }
                                });
                                Thread.sleep(100);
                            }
                        }

                    } else {
                        oldList.remove(music);
                    }
                }
            }
            return newList;
        }

        private Music fillVideoDataIntoMusic(Music music, Cursor cursor) throws Exception {

            String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
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
            deleteOldEntries();
        }

        private void deleteOldEntries() {
            if (oldList.size() > 0)
                new AsyncTaskUpdateMusicDb(audioList, new InterfaceOperation() {
                    @Override
                    public void onOperationCompleted() {

                        if (adapterSongList != null) {
                            for (Music deletedMusict : oldList) {
                                int positionOfDeletedMusic = audioList.indexOf(deletedMusict);
                                adapterSongList.removeItem(positionOfDeletedMusic);
                            }
                        }
                        oldList = null;
                        insertNewEntries();
                    }
                }, DatabaseConstants.VALUE_DELETE).execute();
            else
                insertNewEntries();
        }

        private void insertNewEntries() {
            if (newList.size() > 0)
                new AsyncTaskUpdateMusicDb(newList, new InterfaceOperation() {
                    @Override
                    public void onOperationCompleted() {
                        audioList = AppDatabase.getAppDatabase(AppLevelConstraints.getAppContext()).daoMusic().getAll(DatabaseConstants.VALUE_TRUE);
                        setList();
                        oldList = null;
                        newList = null;
                    }
                }, DatabaseConstants.VALUE_INSERT).execute();
        }
    }

    public void selectIncomingOrOutgoing(Music selectedMusic) {

        ArrayList<BeanDialogsOption> option = new ArrayList<BeanDialogsOption>();

        option.add(new BeanDialogsOption(getString(R.string.text_set_as_incoming), activity.getResources().getDrawable(R.mipmap.dialog_music), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogsCustom.instance.cancelDialog();
                if (selectedPositions == null)
                    ((ActivityBase) activity).initiateContactSelection(selectedMusic, true);
                else
                    ((ActivityBase) activity).setPhoneNo(selectedPositions, selectedMusic, true);
            }
        }));


        option.add(new BeanDialogsOption(getString(R.string.text_as_outgoing), activity.getResources().getDrawable(R.mipmap.dialog_video), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogsCustom.instance.cancelDialog();
                if (selectedPositions == null)
                    ((ActivityBase) activity).initiateContactSelection(selectedMusic, false);
                else
                    ((ActivityBase) activity).setPhoneNo(selectedPositions, selectedMusic, false);
            }
        }));

        DialogsCustom.instance.showOptionsDialog(activity, option, getString(R.string.text_set_an_option));


    }

}
