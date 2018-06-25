package com.mobileoid2.celltone.Module.Music.Fragment;


import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;

import com.mobileoid2.celltone.Module.Base.ActivityBase;
import com.mobileoid2.celltone.Module.Music.Adapter.AdapterSongsGerne;
import com.mobileoid2.celltone.Module.Music.Bean.Music;
import com.mobileoid2.celltone.Module.Music.Interface.InterfaceSongSelected;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.Util.AppLevelConstraints;
import com.mobileoid2.celltone.Util.AppUtils;
import com.mobileoid2.celltone.database.AppDatabase;
import com.mobileoid2.celltone.database.AsyncTaskUpdateMusicDb;
import com.mobileoid2.celltone.database.DatabaseConstants;
import com.mobileoid2.celltone.database.InterfaceOperation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSongsGerne extends Fragment implements InterfaceSongSelected {

    private View view;
    private Activity activity;
    private String type = "";
    private RecyclerView listGernes;
    private AdapterSongsGerne adapter;
    private List<Music> audioList = new ArrayList<Music>();
    private SmoothProgressBar smoothProgressBar;
    private List<Music> oldList;
    private boolean listIsEmpty = false;

    public static FragmentSongsGerne newInstance() {
        FragmentSongsGerne fragment = new FragmentSongsGerne();
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
            view = inflater.inflate(R.layout.fragment_gerne, container, false);
            listGernes = view.findViewById(R.id.list_gerne);
            smoothProgressBar = (SmoothProgressBar) view.findViewById(R.id.custom_progressbar);

            LinearLayoutManager layoutManager = new LinearLayoutManager(activity.getApplicationContext(), LinearLayoutManager.VERTICAL, false);
            listGernes.setLayoutManager(layoutManager);
            listGernes.setItemAnimator(new DefaultItemAnimator());


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

                    new songsfetcher().execute();

                }
            }.execute();

        } catch (Exception e) {
            Log.e("Dashboard", AppUtils.instance.getExceptionString(e));
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


    private void setAdapter() {
        if (view == null) return;
        Collections.sort(audioList);
        if (adapter == null) {
            adapter = new AdapterSongsGerne(activity.getApplicationContext(), audioList, this);
            listGernes.setAdapter(adapter);
        } else {
            adapter.refreshList(audioList);
        }
        listGernes.invalidate();
        ((ActivityBase) activity).setCurrentMusicList(audioList);
        ((ActivityBase) activity).setSongList(audioList);
        if (!listIsEmpty)
            setProgressbar(false);
    }

    @Override
    public void onSongSelected(Music music) {
        ((ActivityBase) activity).launchDoctorFragmentByAdding(FragmentSongs.newInstance(music, null), FragmentSongs.class.toString());
    }

    @Override
    public void onSongSelected(int position, boolean isAssign) {

    }

    class songsfetcher extends AsyncTask<String, Void, Void> {

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

            oldList = AppDatabase.getAppDatabase(AppLevelConstraints.getAppContext()).daoMusic().getAll(DatabaseConstants.VALUE_FALSE);

            // database list is blank so initialize it in case of null
            if (oldList == null) oldList = new ArrayList<Music>();

            //if oldList size is greater than 0, than set the list in adapter
            if (oldList.size() > 0) {


                for (int i = 0; i < oldList.size(); i++) {
                    audioList.add(oldList.get(i));
                    if (audioList.size() % 300 == 0 || audioList.size() == oldList.size()) {
                        Log.e("SONGS", "Setting list , size :: " + audioList.size());
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setAdapter();
                            }
                        });
                        Thread.sleep(100);
                    }
                }
            } else {
                listIsEmpty = true;
            }


            // start the update process
            List<Music> newList = new ArrayList<Music>();

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
                fillNewSongListData(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, newList, cursor);
                if (cursor != null)
                    cursor.close();

                cursor = AppLevelConstraints.getAppContext().getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, cols, selection, null, null);
                fillNewSongListData(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, newList, cursor);
                if (cursor != null)
                    cursor.close();


                // if the newList has data, means new music is added, if audiolist had data, means it has been removed from device. hence commence operation update
                if (newList.size() > 0 || oldList.size() > 0)
                    new AsyncTaskUpdateMusicDb(newList, new InterfaceOperation() {
                        @Override
                        public void onOperationCompleted() {

                            if (oldList.size() > 0)
                                new AsyncTaskUpdateMusicDb(oldList, new InterfaceOperation() {
                                    @Override
                                    public void onOperationCompleted() {
                                        audioList = AppDatabase.getAppDatabase(AppLevelConstraints.getAppContext()).daoMusic().getAll(DatabaseConstants.VALUE_FALSE);
                                        listIsEmpty = false;
                                        oldList = null;

                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                setAdapter();
                                            }
                                        });

                                    }
                                }, DatabaseConstants.VALUE_DELETE).execute();
                            else {
                                audioList = AppDatabase.getAppDatabase(AppLevelConstraints.getAppContext()).daoMusic().getAll(DatabaseConstants.VALUE_FALSE);
                                listIsEmpty = false;
                                oldList = null;
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAdapter();
                                    }
                                });
                            }
                        }
                    }, DatabaseConstants.VALUE_INSERT).execute();
                else {
                    oldList = null;
                    setProgressbar(false);
                }

            } catch (Exception e) {
                Log.e("Update", AppUtils.instance.getExceptionString(e));
            }
        }

        private void fillNewSongListData(Uri contentUri, List<Music> newList, Cursor cursor) throws Exception {
            if (cursor != null && cursor.getCount() > 0) {

                while (cursor.moveToNext()) {
                    Music music = new Music();
                    music.setSongTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));

                    if (!oldList.contains(music)) {
                        fillDataIntoSongMusic(contentUri, music, cursor);
                        newList.add(music);
                        Log.e("ADDING NEW SONG", music.getSongTitle());
                        if (listIsEmpty) {
                            if (newList.size() % 100 == 0) {
                                audioList = newList;
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAdapter();
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
        }

        private void fillDataIntoSongMusic(Uri contentUri, Music music, Cursor cursor) throws Exception {

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
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        audioList = null;
    }
}
