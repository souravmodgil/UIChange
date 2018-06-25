package com.mobileoid2.celltone.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.mobileoid2.celltone.Module.Contacts.Bean.BeanContacts;
import com.mobileoid2.celltone.Module.Music.Bean.Music;

/**
 * Created by mobileoid2 on 13/11/17.
 */
@Database(entities = {BeanContacts.class, Music.class}, version = 6, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    private static final Object lock = new Object();

    public abstract DaoContacts daoContacts();

    public abstract DaoMusic daoMusic();

    public static AppDatabase getAppDatabase(Context context) {
        AppDatabase temp = INSTANCE;
        if (temp == null) {
            synchronized (lock) {    // While we were waiting for the lock, another
                temp = INSTANCE;        // thread may have instantiated the object.
                if (temp == null) {
                    temp = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "ring_tone-database").allowMainThreadQueries().build();
                    INSTANCE = temp;
                }
            }
        }
        return temp;
    }

    public static synchronized void closeInstance() {
        if (INSTANCE != null) {
            INSTANCE.close();
            INSTANCE = null;
        }
    }

}







