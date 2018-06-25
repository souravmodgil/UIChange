package com.mobileoid2.celltone.utility;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.mobileoid2.celltone.celltoneDB.CellToneRoomDatabase;
import com.mobileoid2.celltone.database.AppDatabase;
import com.mobileoid2.celltone.pojo.audio.Body;
import com.mobileoid2.celltone.pojo.otpverifiy.User;

import java.util.List;

public class DatabaseInitializer {
    private static final String TAG = DatabaseInitializer.class.getName();

    public static void populateAsync(@NonNull final CellToneRoomDatabase db) {
        PopulateDbAsync task = new PopulateDbAsync(db);
        task.execute();
    }

    /*public static void populateSync(@NonNull final AppDatabase db) {
        populateWithTestData(db);
    }*/

    private static Long[] addALLMedia(final CellToneRoomDatabase db, List<Body> bodyList) {
        return db.get_pojoALLMediaDAO().insertList(bodyList);
    }

    /*private static void populateWithTestData(AppDatabase db) {
        User user = new User();
        user.setFirstName("Ajay");
        user.setLastName("Saini");
        user.setAge(25);
        addUser(db, user);

        List<User> userList = db.userDao().getAll();
        Log.d(DatabaseInitializer.TAG, "Rows Count: " + userList.size());
    }*/

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final CellToneRoomDatabase mDb;

        PopulateDbAsync(CellToneRoomDatabase db) {
            mDb = db;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            //populateWithTestData(mDb);
            return null;
        }

    }
}
