package com.mobileoid2.celltone.utility;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

import com.mobileoid2.celltone.pojo.MusicPojo;

import java.util.ArrayList;

public class AudioFetcher {

    private final Context context;

    public AudioFetcher(Context c) {
        this.context = c;
    }

    public ArrayList<MusicPojo> fetchAll() {
        String[] projectionFields = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };
        ArrayList<MusicPojo> musicPojoArrayList = new ArrayList<>();
        CursorLoader cursorLoader = new CursorLoader(context,
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projectionFields, // the columns to retrieve
                null, // the selection criteria (none)
                null, // the selection args (none)
                null // the sort order (default)
        );

        Cursor c = cursorLoader.loadInBackground();
        if (c.moveToFirst()) {
            do {
                /*new MusicPojo(
                        ""
                        ,""
                        ,""
                        ,""
                        ,0
                        ,""
                        ,""
                        ,""
                        ,false
                );*/
                MusicPojo musicPojo=new MusicPojo(
                        c.getString(c.getColumnIndex(MediaStore.Audio.Media._ID)),
                        c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                        c.getString(c.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)),
                        c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA)),
                        Integer.parseInt(c.getString(c.getColumnIndex(MediaStore.Audio.Media.DURATION))),
                       "",
                        "",
                        c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                        true
                        );
                //contactsMap.put(contactId, musicPojo);
                musicPojoArrayList.add(musicPojo);
            } while (c.moveToNext());
        }

        c.close();

       // matchContactEmails(contactsMap);


        return musicPojoArrayList;
    }

}
