package com.mobileoid2.celltone.database;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.mobileoid2.celltone.Module.Music.Bean.Music;
import com.mobileoid2.celltone.Util.AppLevelConstraints;

import java.util.ArrayList;
import java.util.List;

public class AsyncTaskUpdateMusicDb extends AsyncTask<Void, Void, Void> {

    private final int operationValue;
    private List<Music> musicList;
    private InterfaceOperation interfaceOperation;
    private String isVideo;

    public AsyncTaskUpdateMusicDb( @NonNull List<Music> musicList, InterfaceOperation onOperationCompleted, int operationValue) {

        this.operationValue = operationValue;
        this.musicList = musicList;
        this.interfaceOperation = onOperationCompleted;
    }

    public AsyncTaskUpdateMusicDb(String isVideo, InterfaceOperation onOperationCompleted, int operationValue) {
        this.operationValue = operationValue;
        this.isVideo = isVideo;
        this.interfaceOperation = onOperationCompleted;
        musicList = null;
    }


    @Override
    protected Void doInBackground(final Void... params) {

        if (musicList != null) {
            if (operationValue == DatabaseConstants.VALUE_INSERT)
                AppDatabase.getAppDatabase(AppLevelConstraints.getAppContext()).daoMusic().insertAll(musicList);
            if (operationValue == DatabaseConstants.VALUE_UPDATE) {
                int value = AppDatabase.getAppDatabase(AppLevelConstraints.getAppContext()).daoMusic().update(musicList.get(0));
                Log.e("Database udpate", musicList.get(0).getSongsPath() + ", value is = " + value);
            }
        }
        if (operationValue == DatabaseConstants.VALUE_DELETE) {
            if (musicList == null) {
                AppDatabase.getAppDatabase(AppLevelConstraints.getAppContext()).daoMusic().delete("-1", isVideo);
                return null;
            }
            if (musicList.size() == 0) {
                AppDatabase.getAppDatabase(AppLevelConstraints.getAppContext()).daoMusic().delete();
                return null;
            }
            if (musicList.size() > 0)
                for (int i = 0; i < musicList.size(); i++)
                    AppDatabase.getAppDatabase(AppLevelConstraints.getAppContext()).daoMusic().delete(musicList.get(i).getId());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        AppDatabase.closeInstance();
        if (interfaceOperation != null) interfaceOperation.onOperationCompleted();
    }
}