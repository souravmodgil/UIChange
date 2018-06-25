package com.mobileoid2.celltone.Module.Music.Fragment;


import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

import com.mobileoid2.celltone.CustomWidget.Dialog.BeanDialogsOption;
import com.mobileoid2.celltone.CustomWidget.Dialog.DialogsCustom;
import com.mobileoid2.celltone.CustomWidget.TextView.TextVeiwEuro55Regular;
import com.mobileoid2.celltone.Module.Base.ActivityBase;
import com.mobileoid2.celltone.Module.Contacts.Bean.BeanContacts;
import com.mobileoid2.celltone.Module.Contacts.Fragment.FragmentContacts;
import com.mobileoid2.celltone.Module.Music.Adapter.AdapterMusicList;
import com.mobileoid2.celltone.Module.Music.Bean.Music;
import com.mobileoid2.celltone.Module.Music.Interface.InterfaceSongSelected;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.Util.AppLevelConstraints;
import com.mobileoid2.celltone.Util.AppSharedPref;
import com.mobileoid2.celltone.Util.AppUtils;
import com.mobileoid2.celltone.Util.AudioPlayer;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSongs extends Fragment implements InterfaceSongSelected {

    private Activity activity;
    private View view;
    private String TAG = FragmentSongs.class.toString();
    private List<Music> audioList = new ArrayList<Music>();
    private RecyclerView listSongs;
    private AdapterMusicList adapterSongList;
    private int selectedSongPosition = -1;
    private SmoothProgressBar smoothProgressBar;


    private ImageButton imageButtonMute;
    private ImageButton imageButtonThumbnail;
    private TextVeiwEuro55Regular textViewSongName;

    private boolean isMute = false;
    private int currentVolume;
    private Music currentMusic;
    private HashSet<BeanContacts> selectedPositions;

    private boolean listIsEmpty = false;
    private readSongsFromDevice songUpdater;


    public static FragmentSongs newInstance(Music music, HashSet<BeanContacts> selectedPositions) {
        FragmentSongs fragment = new FragmentSongs();
        fragment.currentMusic = music;
        fragment.selectedPositions = selectedPositions;
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        VideoPlayer.stop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            view = inflater.inflate(R.layout.fragment_songs, container, false);
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
            ImageButton imageButtonAssignUser = view.findViewById(R.id.image_button_assign_user);
            textViewSongName = view.findViewById(R.id.textview_selected_music_name);
            imageButtonThumbnail = view.findViewById(R.id.image_button_thumbnail);

            imageButtonThumbnail.setImageDrawable(getResources().getDrawable(R.mipmap.no_album_big));

            imageButtonThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AudioPlayer.showController();
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


            listSongs.setOnFlingListener(new RecyclerView.OnFlingListener() {
                @Override
                public boolean onFling(int velocityX, int velocityY) {

                    AudioPlayer.getmMediaController().hide();
                    return false;
                }
            });


            AudioPlayer.getInstance(activity, view.findViewById(R.id.header), null, new Handler());
            AudioPlayer.getmMediaController().setPrevNextListeners(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //next button clicked
                    if (selectedSongPosition + 1 < audioList.size()) {
                        selectedSongPosition = selectedSongPosition + 1;
                        playMusic();
                    }
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //previous button clicked
                    if (selectedSongPosition - 1 > -1) {
                        selectedSongPosition = selectedSongPosition - 1;
                        playMusic();
                    }
                }
            });

            ((AppBarLayout) view.findViewById(R.id.app_bar)).addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                    if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                        //  Collapsed
                        AudioPlayer.setControllerVisibility(false);
                    } else {
                        //Expanded
                        AudioPlayer.setControllerVisibility(true);
                    }
                }
            });

            setProgressbar(true);

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

                    songUpdater = new readSongsFromDevice();
                    songUpdater.execute();
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
                playMusic();
            } else
                Toast.makeText(activity, R.string.text_selected_music_no_longer, Toast.LENGTH_SHORT).show();

    }

    private void playMusic() {
        try {
            if (audioList.size() == 0) return;
            if (selectedSongPosition == -1) selectedSongPosition = 0;
            AudioPlayer.getInstance(activity, view.findViewById(R.id.header), audioList.get(selectedSongPosition).getSongsPath(), new Handler());
            AudioPlayer.play();
            textViewSongName.setText(audioList.get(selectedSongPosition).getSongTitle());
            textViewSongName.setSelected(true);
            imageButtonThumbnail.setImageDrawable(getResources().getDrawable(R.mipmap.no_album_big));
            try {
                Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                Uri uri = ContentUris.withAppendedId(sArtworkUri, Long.parseLong(audioList.get(selectedSongPosition).getThumbUrl()));
                //  Uri uri = Uri.fromFile(new File(c.getThumbUrl()));
                imageButtonThumbnail.setImageURI(uri);
            } catch (Exception ex) {
                Log.e("ADAPTER : SONGS", AppUtils.instance.getExceptionString(ex));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        ((ActivityBase) activity).setSongList(audioList);
        setProgressbar(false);
    }

    @Override
    public void onSongSelected(Music music) {

    }

    @Override
    public void onSongSelected(int position, boolean isAssign) {
        selectedSongPosition = position;
        if (isAssign) {

            selectIncomingOrOutgoing(audioList.get(selectedSongPosition));

        } else {
            // play the music
            //  listSongs.scrollToPosition(0);
            //  ((AppBarLayout) view.findViewById(R.id.app_bar)).setExpanded(true);
            playMusic();
        }
    }

    @Override
    public void onDetach() {
        if (songUpdater != null)
            if (songUpdater.getStatus() == AsyncTask.Status.RUNNING)
                songUpdater.cancel(true);
        super.onDetach();
        try {
            AudioPlayer.stop();
            AudioPlayer.setControllerVisibility(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        audioList = null;

    }


    private class readSongsFromDevice extends AsyncTask<Void, Void, Void> {

        private List<Music> oldList = new ArrayList<Music>();
        private List<Music> newList = new ArrayList<Music>();

        @Override
        protected Void doInBackground(Void... voids) {

            oldList = AppDatabase.getAppDatabase(AppLevelConstraints.getAppContext()).daoMusic().getAll(DatabaseConstants.VALUE_FALSE);

            // database list is blank so initialize it in case of null
            if (oldList == null) oldList = new ArrayList<Music>();

            //if oldList size is greater than 0, than set the list in adapter
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
                String cols[] = {MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ALBUM_ID,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Genres._ID
                };

                String selection = MediaStore.Audio.Media.DURATION + ">=10000";
                Cursor cursor = AppLevelConstraints.getAppContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cols, selection, null, null);
                fillNewSongListData(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursor);
                if (cursor != null)
                    cursor.close();

                cursor = AppLevelConstraints.getAppContext().getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, cols, selection, null, null);
                fillNewSongListData(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, cursor);
                if (cursor != null)
                    cursor.close();

            } catch (Exception e) {
                Log.e("Update", AppUtils.instance.getExceptionString(e));
            }

            return null;

        }

        private List<Music> fillNewSongListData(Uri contentUri, Cursor cursor) throws Exception {
            if (cursor != null && cursor.getCount() > 0) {

                while (cursor.moveToNext()) {

                    if (this.isCancelled()) {
                        newList.clear();
                        oldList.clear();
                        return null;
                    }

                    Music music = new Music();
                    music.setSongTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));

                    if (oldList.contains(music)) {
                        oldList.remove(music);
                    } else {
                        music = fillDataIntoSongMusic(contentUri, music, cursor);
                        newList.add(music);
                        Log.e("ADDING NEW SONG", music.getSongTitle());

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
                    mr.setDataSource(activity.getApplicationContext(), trackUri);
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

            deleteEntries();

        }

        private void insertNewEntries() {
            if (newList.size() > 0)
                new AsyncTaskUpdateMusicDb(newList, new InterfaceOperation() {
                    @Override
                    public void onOperationCompleted() {
                        audioList = AppDatabase.getAppDatabase(AppLevelConstraints.getAppContext()).daoMusic().getAll(DatabaseConstants.VALUE_FALSE);
                        setList();
                    }
                }, DatabaseConstants.VALUE_INSERT).execute();
            else {
                setProgressbar(false);
            }
        }

        private void deleteEntries() {
            if (oldList.size() > 0)
                new AsyncTaskUpdateMusicDb(oldList, new InterfaceOperation() {
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
