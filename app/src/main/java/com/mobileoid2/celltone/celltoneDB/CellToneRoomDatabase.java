package com.mobileoid2.celltone.celltoneDB;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

//import com.mobileoid2.celltone.celltoneDB.dao.GET_MEDIA_DAO;
import com.mobileoid2.celltone.celltoneDB.dao.POJO_ALL_MEDIA_DAO;
import com.mobileoid2.celltone.pojo.audio.Body;
import com.mobileoid2.celltone.pojo.getmedia.PojoGETMediaResponse;

@Database(entities =
        {/*PojoGETMediaResponse.class,*/ Body.class},
        version = 1)
public abstract class CellToneRoomDatabase extends RoomDatabase {
    private static CellToneRoomDatabase INSTANCE;

   public static CellToneRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CellToneRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            CellToneRoomDatabase.class, "CellToneRoomDatabase")
                            .build();

                }
            }
        }
        return INSTANCE;
    }

    /*---------DAO---------------*/
   // public abstract GET_MEDIA_DAO get_media_dao();
    public abstract POJO_ALL_MEDIA_DAO get_pojoALLMediaDAO();

    public static void destroyInstance() {
        INSTANCE = null;
    }

}
