package com.mobileoid2.celltone.database;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.mobileoid2.celltone.Module.Contacts.Bean.BeanContacts;
import com.mobileoid2.celltone.Util.AppLevelConstraints;

import java.util.List;

public class AsyncTaskUpdateContactsDb extends AsyncTask<Void, Void, Void> {

    private final int operationValue;
    private final List<BeanContacts> phonecontenteList;
    InterfaceOperation interfaceOperation;

    public AsyncTaskUpdateContactsDb(@NonNull List<BeanContacts> phonecontenteList, InterfaceOperation interfaceOperation, int operationValue) {
        this.operationValue = operationValue;
        this.phonecontenteList = phonecontenteList;
        this.interfaceOperation = interfaceOperation;
    }

    @Override
    protected Void doInBackground(final Void... params) {

        if (phonecontenteList != null) {
            if (operationValue == DatabaseConstants.VALUE_INSERT)
                AppDatabase.getAppDatabase(AppLevelConstraints.getAppContext()).daoContacts().insertAll(phonecontenteList);
            if (operationValue == DatabaseConstants.VALUE_UPDATE) {
//                    int value = mDb.daoContacts().update(phonecontenteList.get(0).getMusicIncomingPath(), phonecontenteList.get(0).getMusicIncomingThumbNail(), phonecontenteList.get(0).getIsIncomingVideo(), phonecontenteList.get(0).getNumber());
                for (int i = 0; i < phonecontenteList.size(); i++) {
                    int value = AppDatabase.getAppDatabase(AppLevelConstraints.getAppContext()).daoContacts().update(phonecontenteList.get(i));
                    Log.e("Database udpate", phonecontenteList.get(i).getNumber() + ", value is = " + value);
                }

            }
            if (operationValue == DatabaseConstants.VALUE_DELETE) {
                if (phonecontenteList.size() == 0)
                    AppDatabase.getAppDatabase(AppLevelConstraints.getAppContext()).daoContacts().delete();
                else {
                    for (int i = 0; i < phonecontenteList.size(); i++) {
                        AppDatabase.getAppDatabase(AppLevelConstraints.getAppContext()).daoMusic().delete(phonecontenteList.get(i).getId());
                    }
                }
            }


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