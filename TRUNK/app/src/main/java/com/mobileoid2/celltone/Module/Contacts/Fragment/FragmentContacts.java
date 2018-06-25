package com.mobileoid2.celltone.Module.Contacts.Fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mobileoid2.celltone.CustomWidget.Dialog.BeanDialogsOption;
import com.mobileoid2.celltone.CustomWidget.Dialog.DialogsCustom;
import com.mobileoid2.celltone.CustomWidget.EditTextView.EditTextEuro55Regular;
import com.mobileoid2.celltone.Module.Base.ActivityBase;
import com.mobileoid2.celltone.Module.Contacts.Adapter.AdapterContactsList;
import com.mobileoid2.celltone.Module.Contacts.Bean.BeanContacts;
import com.mobileoid2.celltone.Module.Contacts.Interface.InterfaceContacts;
import com.mobileoid2.celltone.Module.Music.Bean.Music;
import com.mobileoid2.celltone.Module.Music.Fragment.FragmentSongs;
import com.mobileoid2.celltone.Module.Music.Fragment.FragmentVideo;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.Util.AppLevelConstraints;
import com.mobileoid2.celltone.Util.AppUtils;
import com.mobileoid2.celltone.database.AppDatabase;
import com.mobileoid2.celltone.database.AsyncTaskUpdateContactsDb;
import com.mobileoid2.celltone.database.DatabaseConstants;
import com.mobileoid2.celltone.database.InterfaceOperation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentContacts extends Fragment implements InterfaceContacts {

    private View view;
    private Activity activity;
    private List<BeanContacts> contactlist = new CopyOnWriteArrayList<BeanContacts>();
    private AdapterContactsList adapterContacts;
    private RecyclerView listContacts;
    private Music selectedMusic;
    private String searchText = "";
    private TextView textViewNumbers;
    private SmoothProgressBar smoothProgressBar;
    private boolean isListEmpty = false;
    private boolean isIncoming;
    private AsyncTask<Void, Void, Void> contactUpdater;
    private readContactsFromDevice readContacts;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

    public static FragmentContacts newInstance(Music selectedMusic, boolean isIncoming) {
        FragmentContacts fragment = new FragmentContacts();
        fragment.selectedMusic = selectedMusic;
        fragment.isIncoming = isIncoming;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        try {
            view = inflater.inflate(R.layout.fragment_contacts, container, false);


            textViewNumbers = view.findViewById(R.id.textview_numbers);
            smoothProgressBar = (SmoothProgressBar) view.findViewById(R.id.custom_progressbar);
            listContacts = view.findViewById(R.id.list_contacts);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
            listContacts.setLayoutManager(layoutManager);
            listContacts.setItemAnimator(new DefaultItemAnimator());


            ((EditTextEuro55Regular) view.findViewById(R.id.edittext_search)).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().equals("")) return;
                    if (s.toString().length() < 3) {
                        searchText = "";
                    } else {
                        searchText = s.toString();
                    }
                    try {
                        setContactList(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            ((EditTextEuro55Regular) view.findViewById(R.id.edittext_search)).setSelected(false);
            ((EditTextEuro55Regular) view.findViewById(R.id.edittext_search)).clearFocus();

            view.findViewById(R.id.layout_checkbox).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    boolean isChecked = ((CheckBox) view.findViewById(R.id.checkbox_select_all)).isChecked();

                    if (adapterContacts != null) {
                        if (!isChecked) {
                            adapterContacts.setSelectAll(true);
                            setCheckBoxSelection(true);
                        } else {
                            adapterContacts.setSelectAll(false);
                            setCheckBoxSelection(false);
                        }
                    }

                }
            });


            view.findViewById(R.id.button_done).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (contactlist == null) {
                        activity.onBackPressed();
                        return;
                    }
                    if (adapterContacts == null) {
                        activity.onBackPressed();
                        return;
                    }
                    if (adapterContacts.getSelectedPositions().size() == 0) {
                        activity.onBackPressed();
                        return;
                    }

                    try {
                        onContactSelected();
                    } catch (Exception e) {
                        e.printStackTrace();
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

                    readContacts = new readContactsFromDevice();
                    readContacts.execute();
                }
            }.execute();


        } catch (Exception e) {
            Log.e("Contacts", AppUtils.instance.getExceptionString(e));
        }

        return view;
    }

    public void setCheckBoxSelection(boolean isChecked) {
        ((CheckBox) view.findViewById(R.id.checkbox_select_all)).setChecked(isChecked);
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

    private void onContactSelected() throws Exception {
        if (selectedMusic != null)
            ((ActivityBase) activity).setPhoneNo(adapterContacts.getSelectedPositions(), selectedMusic, isIncoming);
        else {
            showAudioVideoAlertBox(adapterContacts.getSelectedPositions());
        }
    }

    private void setContactList(boolean shallSort) throws Exception {
        if (view == null) return;
        List<BeanContacts> tempList = filterContacts(contactlist, searchText);

        if (shallSort)
            Collections.sort(tempList);
        if (adapterContacts == null) {
            adapterContacts = new AdapterContactsList(activity.getApplicationContext(), tempList, this);
            listContacts.setAdapter(adapterContacts);
        } else
            adapterContacts.updateList(tempList);
        if (!isListEmpty) setProgressbar(false);

        if (tempList.size() > 0)
            textViewNumbers.setText("(" + tempList.size() + ")");
        else
            textViewNumbers.setText("");
    }

    private List<BeanContacts> filterContacts(List<BeanContacts> models, @NonNull String query) {

        if (models == null) return new ArrayList<BeanContacts>();
        if (query.equals("")) return models;
        query = query.toLowerCase();


        List<BeanContacts> filteredModelList = new ArrayList<BeanContacts>();
        final String searchText = query;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            filteredModelList = models.stream().filter(p -> p.getName().toLowerCase().contains(searchText) || p.getNumber().toLowerCase().contains(searchText)).collect(Collectors.toList());
        } else {
            for (BeanContacts model : models) {
                String text = model.getName().toLowerCase() + " " + model.getNumber().toLowerCase();
                if (text.contains(query)) {
                    filteredModelList.add(model);
                }
            }
        }
        return filteredModelList;
    }

    public void showAudioVideoAlertBox(HashSet<BeanContacts> selectedPositions) {

        ArrayList<BeanDialogsOption> option = new ArrayList<BeanDialogsOption>();

        option.add(new BeanDialogsOption(getString(R.string.text_Set_as_audio), activity.getResources().getDrawable(R.mipmap.dialog_music), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogsCustom.instance.cancelDialog();
                ((ActivityBase) activity).launchDoctorFragmentByReplacing(FragmentSongs.newInstance(null, selectedPositions), FragmentSongs.class.toString());
            }
        }));


        option.add(new BeanDialogsOption(getString(R.string.text_set_as_video), activity.getResources().getDrawable(R.mipmap.dialog_video), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogsCustom.instance.cancelDialog();
                ((ActivityBase) activity).launchDoctorFragmentByReplacing(FragmentVideo.newInstance(null, selectedPositions), FragmentVideo.class.toString());
            }
        }));
        DialogsCustom.instance.showOptionsDialog(activity, option, getString(R.string.text_set_an_option));

    }


    @Override
    public void onContactSelectonChange(boolean isChecked) {
        setCheckBoxSelection(isChecked);
    }

    private class readContactsFromDevice extends AsyncTask<Void, Void, Void> {

        private List<BeanContacts> newEntries;

        @Override
        protected void onPreExecute() {


            DialogsCustom.instance.showMessageDialog(activity, getString(R.string.text_fetching_contacts), "");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (!isCancelled())
                newEntries = AppDatabase.getAppDatabase(AppLevelConstraints.getAppContext()).daoContacts().getAll();

            if (newEntries == null) newEntries = new ArrayList<BeanContacts>();

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            DialogsCustom.instance.cancelDialog();

            if (isCancelled()) return;

            if (newEntries.size() > 0) {
                contactlist = newEntries;
                try {
                    setContactList(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                isListEmpty = true;
            }

            contactUpdater = new updateContacts();
            contactUpdater.execute();
        }
    }

    private class updateContacts extends AsyncTask<Void, Void, Void> {


        private List<BeanContacts> newEntries = new ArrayList<BeanContacts>();
        ;
        private List<BeanContacts> deletedEntries = new ArrayList<BeanContacts>();

        @Override
        protected Void doInBackground(Void... voids) {
            deletedEntries.addAll(contactlist);

            long startMilliSeconds = System.currentTimeMillis();
            ContentResolver cr = AppLevelConstraints.getAppContext().getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            try {
                if (cur != null)
                    if (cur.getCount() > 0) {
                        int cursorSize = cur.getColumnCount();
                        String phoneNo = "";
                        while (cur.moveToNext()) {

                            if (isCancelled()) {
                                deletedEntries = new ArrayList<BeanContacts>();
                                break;
                            }

                            String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                            Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                            if (pCur != null) {
                                while (pCur.moveToNext()) {
                                    phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                }
                                pCur.close();
                            }

                            if (!phoneNo.equals("")) {
                                phoneNo = AppUtils.instance.fixPhoneNo(phoneNo);

                                BeanContacts contactDetail = new BeanContacts();
                                contactDetail.setId(id);
                                contactDetail.setNumber(phoneNo);
                                contactDetail.setName(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));


                                if (deletedEntries.contains(contactDetail)) {

                                    BeanContacts contactInDb = deletedEntries.get(deletedEntries.indexOf(contactDetail));
                                    deletedEntries.remove(contactDetail);
                                    if (!contactDetail.toString().equals(contactInDb.toString())) {
                                        contactDetail.setIsIncomingVideo(contactInDb.getIsIncomingVideo());
                                        contactDetail.setMusicIncomingPath(contactInDb.getMusicIncomingPath());
                                        contactDetail.setMusicIncomingThumbNail(contactInDb.getMusicIncomingThumbNail());
                                        contactDetail.setIsOutgoingVideo(contactInDb.getIsOutgoingVideo());
                                        contactDetail.setMusicOutgoingPath(contactInDb.getMusicOutgoingPath());
                                        contactDetail.setMusicOutgoingThumbNail(contactInDb.getMusicOutgoingThumbNail());
                                        newEntries.add(contactDetail);
                                    }
                                } else {
                                    contactDetail.setIsIncomingVideo("999");
                                    contactDetail.setMusicIncomingPath("");
                                    contactDetail.setMusicIncomingThumbNail("");
                                    contactDetail.setIsOutgoingVideo("999");
                                    contactDetail.setMusicOutgoingPath("");
                                    contactDetail.setMusicOutgoingThumbNail("");
                                    newEntries.add(contactDetail);

                                    if (isListEmpty) {
                                        if (newEntries.size() % 300 == 0 || newEntries.size() == cursorSize) {
                                            contactlist.clear();
                                            contactlist.addAll(newEntries);
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        setContactList(false);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                            Thread.sleep(300);
                                        }
                                    }
                                }
                            }

                        }
                    }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (cur != null)
                    cur.close();
            }

            Log.e("CONTACTS", "fetching time : " + (System.currentTimeMillis() - startMilliSeconds) + "size of list to be added " + newEntries.size() + " sie of list to be removed : " + deletedEntries.size());

            return null;
        }


        @Override
        protected void onPostExecute(Void o) {
            super.onPostExecute(o);
            try {
                deleteEntries();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private void deleteEntries() {
            if (deletedEntries != null)
                if (deletedEntries.size() > 0)
                    new AsyncTaskUpdateContactsDb(deletedEntries, new InterfaceOperation() {
                        @Override
                        public void onOperationCompleted() {

                            if (adapterContacts != null) {
                                for (BeanContacts deleteContact : deletedEntries) {

                                    adapterContacts.removeItem(deleteContact);
                                }
                            }
                            deletedEntries = null;
                            insertEntries();
                        }
                    }, DatabaseConstants.VALUE_DELETE).execute();
                else
                    insertEntries();
        }

        private void insertEntries() {
            if (newEntries.size() > 0)
                new AsyncTaskUpdateContactsDb(newEntries, new InterfaceOperation() {
                    @Override
                    public void onOperationCompleted() {
                        contactlist = AppDatabase.getAppDatabase(AppLevelConstraints.getAppContext()).daoContacts().getAll();
                        newEntries = null;
                        try {
                            setContactList(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, DatabaseConstants.VALUE_INSERT).execute();
        }

    }

    @Override
    public void onDetach() {
        if (contactUpdater != null)
            if (contactUpdater.getStatus() == AsyncTask.Status.RUNNING) {
                contactUpdater.cancel(true);
                contactUpdater = null;
            }
        if (readContacts != null)
            if (readContacts.getStatus() == AsyncTask.Status.RUNNING) {
                readContacts.cancel(true);
                readContacts = null;
            }

        if (adapterContacts != null)
            adapterContacts.cleanCacheWork();
        super.onDetach();
    }
}
