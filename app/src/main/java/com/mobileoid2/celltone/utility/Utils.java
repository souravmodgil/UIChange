package com.mobileoid2.celltone.utility;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobileoid2.celltone.Service.ServiceCallScreenChanged;
import com.mobileoid2.celltone.Util.Constant;
import com.mobileoid2.celltone.database.AppDatabase;
import com.mobileoid2.celltone.database.ContactEntity;
import com.mobileoid2.celltone.database.RingtoneEntity;
import com.mobileoid2.celltone.network.APIClient;
import com.mobileoid2.celltone.network.ApiConstant;
import com.mobileoid2.celltone.network.ApiInterface;
import com.mobileoid2.celltone.network.NetworkCallBack;
import com.mobileoid2.celltone.network.SendRequest;
import com.mobileoid2.celltone.network.model.MainNetworkModel;
import com.mobileoid2.celltone.network.model.allmediaforuser.AllMediaForUser;
import com.mobileoid2.celltone.network.model.contacts.ContactBody;
import com.mobileoid2.celltone.network.model.contacts.ContactsMedia;
import com.mobileoid2.celltone.network.model.contacts.SaveContactsResponse;
import com.mobileoid2.celltone.network.model.setOwnMedia.SetOwnMediaModel;
import com.mobileoid2.celltone.network.model.treadingMedia.Song;
import com.mobileoid2.celltone.pojo.SelectContact;
import com.mobileoid2.celltone.pojo.getmedia.Body;
import com.mobileoid2.celltone.pojo.getmedia.Outgoing;

import com.mobileoid2.celltone.view.activity.UploadActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static android.content.ContentValues.TAG;

public class Utils {


    public static boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + ServiceCallScreenChanged.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.e(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.e(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.e(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.e(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.e(TAG, "***ACCESSIBILITY IS DISABLED***");
        }

        return false;
    }

    /**/
    public static boolean isMyServiceRunning(Context ctx, Class<?> serviceClass) {
        try {
            ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    Log.i("isMyServiceRunning?", true + "");
                    return true;
                }
            }
            Log.i("isMyServiceRunning?", false + "");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else
                return true;

        } else
            return true;

    }

    public static String parseDate(String date) {
        SimpleDateFormat dateFormatParse = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        // 2018-06-15T06:16:16.563Z
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd , yyyy");
        java.util.Date dDate = null;
        try {
            dDate = dateFormatParse.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dDate != null)
            return dateFormat.format(dDate);
        else
            return date;

    }

    public static void getMediForMe(ApiInterface apiInterface, NetworkCallBack networkCallBack) {
        SendRequest.sendRequest(ApiConstant.MEDIA_SET_API, apiInterface.getMediForMe(SharedPrefrenceHandler.getInstance().getUSER_TOKEN()), networkCallBack);
    }


    public static String getFilePath(Context context) {
        return context.getFilesDir() + File.separator + "MEDIA";
    }

    public void parseRequest(String response, Context context, AppDatabase appDatabase) {
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(getMediaForMe(response)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getPlanObserver(context, response, appDatabase)));

    }

    private Observable<MainNetworkModel<AllMediaForUser>> getMediaForMe(String response) {
        Gson gsonObj = new Gson();
        //  final MainNetworkModel<AllMediaForUser> planBody = gsonObj.fromJson(response, MainNetworkModel.class);
        MainNetworkModel<AllMediaForUser> planBody = gsonObj.fromJson(response, new TypeToken<MainNetworkModel<AllMediaForUser>>() {
        }.getType());

        return Observable.create(new ObservableOnSubscribe<MainNetworkModel<AllMediaForUser>>() {
            @Override
            public void subscribe(ObservableEmitter<MainNetworkModel<AllMediaForUser>> emitter) throws Exception {
                if (!emitter.isDisposed()) {
                    emitter.onNext(planBody);
                    emitter.onComplete();
                }


            }
        });
    }

    private DisposableObserver<MainNetworkModel<AllMediaForUser>> getPlanObserver(Context context, String response, AppDatabase appDatabase) {
        return new DisposableObserver<MainNetworkModel<AllMediaForUser>>() {

            @Override
            public void onNext(MainNetworkModel<AllMediaForUser> pojoContactsUploadResonse) {
                //   MainNetworkModel<AllMediaForUser> pojoContactsUploadResonse = Arrays.asList(new Gson().fromJson(response.toString(), MainNetworkModel<AllMediaForUser>.class)).get(0);

                if (pojoContactsUploadResonse.getStatus() == 1000) {
                    SharedPrefrenceHandler.getInstance().setGET_MEDIA_RESPONSE(response.toString());


                    new DownloadTask(context, appDatabase).execute(pojoContactsUploadResonse.getBody());
                }


            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    public void download(Context context, String filepath) {
        new DownloadSingleTask(context).execute(filepath);

    }

    public void downloadOutgoingFiles(Context context, List<ContactBody> contactBodyList) {
        new DownloadOutgoingSelfTask(context).execute(contactBodyList);

    }


    public void parseSaveContactResponse(Activity context, String response, int isIncoming, int isAudio, String mediaId,
                                         String mobileNo, String sampleUrl, AppDatabase appDatabase,
                                         ContactEntity contactEntity, List<Song> songList, int currentSongPostion, ProgressBar progressBar) {

        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                int status = 0;
                Gson gsonObj = new Gson();
                SaveContactsResponse saveContactsResponse = gsonObj.fromJson(response, SaveContactsResponse.class);
                status = saveContactsResponse.getStatus();
                if (status == 1000) {


                    if (isIncoming == 0) {

                        RingtoneEntity ringtoneEntity = new RingtoneEntity();
                        if (isAudio == 1)
                            ringtoneEntity.setContentType("audio");
                        else
                            ringtoneEntity.setContentType("video");
                        ringtoneEntity.setOutgoingMediaId(mediaId);
                        ringtoneEntity.setActionType("self");
                        ringtoneEntity.setNumber(mobileNo);
                        ringtoneEntity.setOutgoingSampleFileUrl(sampleUrl);
                        long id = appDatabase.daoRingtone().insert(ringtoneEntity);
                        if (id == -1) {
                            appDatabase.daoRingtone().update(ringtoneEntity);
                        }


                        if (isAudio == 0)
                            contactEntity.setOutgoingIsVideo(1);
                        else
                            contactEntity.setOutgoingIsVideo(0);
                        contactEntity.setOutgoingSongName(songList.get(currentSongPostion).getTitle());
                        contactEntity.setIsOutgoing(1);
                        contactEntity.setOutgoingArtistName(songList.get(currentSongPostion).getArtistName());
                        contactEntity.setOutgoingSampleFileUrl(sampleUrl);
                        contactEntity.setOutgoingMediaId(mediaId);


                    } else {
                        if (isAudio == 0)
                            contactEntity.setOutgoingIsVideo(1);
                        else
                            contactEntity.setOutgoingIsVideo(0);
                        contactEntity.setIncomingSongName(songList.get(currentSongPostion).getTitle());
                        contactEntity.setIsIncoming(1);
                        contactEntity.setInComingArtistName(songList.get(currentSongPostion).getArtistName());
                        contactEntity.setIncomingSampleFileUrl(sampleUrl);
                        contactEntity.setIncomingMediaId(mediaId);

                    }
                }


                appDatabase.daoContacts().update(contactEntity);


                return status;
            }

            @Override
            protected void onPostExecute(Integer status) {
                super.onPostExecute(status);
                if (status == 1000) {
                    Utils utils = new Utils();
                    download(context, sampleUrl);
                    Toast.makeText(context, "Song set  successfully", Toast.LENGTH_LONG).show();
                    context.onBackPressed();

                }
                progressBar.setVisibility(View.GONE);


            }
        }.execute();
    }

    private Observable<String> getIncomingSongUrl(Context context, String mediaId, AppDatabase appDatabase, String mobielNo
    ) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                if (!emitter.isDisposed()) {
                    String path ="";

                    RingtoneEntity entity = getSongUrl(appDatabase, mobielNo);
                    if(entity!=null)
                    {
                        path =  entity.getIncomingSampleFileUrl();
                        entity.setIncomingSampleFileUrl("");
                        entity.setIncomingMediaId("");
                        appDatabase.daoRingtone().update(entity);
                        if(!path.isEmpty())
                        {
                            deleteMedia(context, mediaId, appDatabase, path);
                        }
                    }




                    emitter.onNext(path);
                    emitter.onComplete();
                }


            }
        });
    }

    private DisposableObserver<String> getFilePath(ApiInterface apiInterface, NetworkCallBack networkCallBack) {
        return new DisposableObserver<String>() {

            @Override
            public void onNext(String path) {
                if (!path.isEmpty())
                    Utils.getMediForMe(apiInterface, networkCallBack);

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }


    public void unsetIncomingothers(Context context, String mediaid, AppDatabase appDatabase, String mobileNumber,
                                    ApiInterface apiInterface, NetworkCallBack callBack)

    {
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(getIncomingSongUrl(context, mediaid, appDatabase, mobileNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getFilePath(apiInterface, callBack)));
    }

    public void deleteMedia(Context context, String mediaid, AppDatabase appDatabase, String filepath) {
        int count = appDatabase.daoRingtone().getMediaId(mediaid);
        if (count == 0) {
            File directoryToZip = new File(Utils.getFilePath(context));
            File file = new File(directoryToZip.getPath() + File.separator + "" + filepath.split("/")[0]);
            File deletefile = new File(file, filepath.split("/")[1]);

            if (deletefile.exists()) {
                deletefile.delete();

            }
        }


    }

    private boolean downloadFiles(File directoryToZip, String filePath) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(ApiConstant.MEDIA_URL + filePath);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return true;
            }
            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();
            // download the file
            input = connection.getInputStream();

            File file = new File(directoryToZip.getPath() + File.separator + "" + filePath.split("/")[0]);
            file.mkdirs();
            File outputFile = new File(file, filePath.split("/")[1]);
            output = new FileOutputStream(outputFile);
            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
               /* if (isCancelled) {
                    input.close();
                    break;
                }*/
                total += count;
                // publishing the progress....

                output.write(data, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        } finally {
            try {
                if (output != null) output.close();
                if (input != null) input.close();
            } catch (IOException ignored) {
            }

            if (connection != null) connection.disconnect();
        }
        return false;
    }

    public void parseSaveContactResponse(Activity context, String response, List<SelectContact> selectedContacts, List<ContactEntity> contactList,
                                         int isOutgoing, int isAudio, Song songs, AppDatabase appDatabase, ProgressBar progressBar) {


        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                int status = 0;
                Gson gsonObj = new Gson();
                SaveContactsResponse saveContactsResponse = gsonObj.fromJson(response, SaveContactsResponse.class);
                status = saveContactsResponse.getStatus();
                if (status == 1000) {
                    ArrayList<ContactEntity> contactEntities = new ArrayList<>();
                    for (int i = 0; i < selectedContacts.size(); i++) {
                        ContactEntity contactEntity = contactList.get(selectedContacts.get(i).getId());
                        if (isOutgoing == 1) {

                            RingtoneEntity ringtoneEntity = new RingtoneEntity();
                            if (isAudio == 1)
                                ringtoneEntity.setContentType("audio");
                            else
                                ringtoneEntity.setContentType("video");
                            ringtoneEntity.setOutgoingMediaId(songs.getId());
                            ringtoneEntity.setActionType("self");
                            ringtoneEntity.setNumber(selectedContacts.get(i).getPhoneNumber());
                            ringtoneEntity.setOutgoingSampleFileUrl(songs.getSampleFileUrl());
                            long id = appDatabase.daoRingtone().insert(ringtoneEntity);
                            if (id == -1) {
                                appDatabase.daoRingtone().update(ringtoneEntity);
                            }


                            if (isAudio == 0)
                                contactEntity.setOutgoingIsVideo(1);
                            else
                                contactEntity.setOutgoingIsVideo(0);
                            contactEntity.setOutgoingSongName(songs.getTitle());
                            contactEntity.setIsOutgoing(1);
                            contactEntity.setOutgoingArtistName(songs.getArtistName());
                            contactEntity.setOutgoingMediaId(songs.getId());
                            contactEntity.setOutgoingSampleFileUrl(songs.getSampleFileUrl());


                        } else {
                            if (isAudio == 0)
                                contactEntity.setOutgoingIsVideo(1);
                            else
                                contactEntity.setOutgoingIsVideo(0);
                            contactEntity.setIncomingSongName(songs.getTitle());
                            contactEntity.setIsIncoming(1);
                            contactEntity.setInComingArtistName(songs.getArtistName());
                            contactEntity.setIncomingMediaId(songs.getId());
                            contactEntity.setIncomingSampleFileUrl(songs.getSampleFileUrl());
                        }
                        appDatabase.daoContacts().update(contactEntity);
                    }


                    // appDatabase.daoContacts().update(contactEntities);


                }
                return status;
            }

            @Override
            protected void onPostExecute(Integer status) {
                super.onPostExecute(status);
                if (status == 1000) {
                    download(context, songs.getSampleFileUrl());
                    Toast.makeText(context, "Song set  successfully", Toast.LENGTH_LONG).show();
                    context.onBackPressed();

                }
                //  new DownloadTask(getActivity().getApplicationContext()).execute(songs.getSampleFileUrl());
                progressBar.setVisibility(View.GONE);


            }
        }.execute();
    }


    public List<ContactEntity> getContactList(ContactsMedia contactsMedia, Map<String, String> contactMap, AppDatabase appDatabase)

    {
        List<ContactEntity> contactList = new ArrayList<>();
        List<RingtoneEntity> ringtoneEntities = new ArrayList<>();

        if (contactsMedia != null) {
            int length = contactsMedia.getBody().size();
            ContactEntity contactEntity;
            for (int i = 0; i < length; i++) {


                contactEntity = new ContactEntity();
                String mobileno = contactsMedia.getBody().get(i).getMobile();

                // check media set on outgoing for self

                Outgoing outgoingSelf = contactsMedia.getBody().get(i).getOutgoingself();
                if (outgoingSelf != null) {
                    // contactEntity.setIsIncoming(0);
                    contactEntity.setIsOutgoing(1);
                    if (contactsMedia.getBody().get(i).getOutgoingself().getContentType().equals("audio"))
                        contactEntity.setOutgoingIsVideo(0);
                    else
                        contactEntity.setOutgoingIsVideo(1);
                    contactEntity.setOutgoingSongName(contactsMedia.getBody().get(i).getOutgoingself().getTitle());
                    contactEntity.setOutgoingMediaId(contactsMedia.getBody().get(i).getOutgoingself().getId());
                    contactEntity.setOutgoingSampleFileUrl(contactsMedia.getBody().get(i).getOutgoingself().getSampleFileUrl());


                } else
                    contactEntity.setIsOutgoing(0);

                //   "[^a-zA-Z]+", " "
                String name = contactMap.get(mobileno);
                contactEntity.setNumber(mobileno);
                if (name != null)
                    contactEntity.setName(name);
                else
                    contactEntity.setName(mobileno);

                // check media set to other user


                if (contactsMedia.getBody().get(i).getIncommingother() instanceof Outgoing &&
                        contactsMedia.getBody().get(i).getIncommingother() != null)

                {
                    contactEntity.setIsIncoming(1);

                    if (contactsMedia.getBody().get(i).getIncommingother().getContentType().equals("audio"))
                        contactEntity.setIsincomingVideo(0);
                    else
                        contactEntity.setIsincomingVideo(1);
                    contactEntity.setIncomingSongName(contactsMedia.getBody().get(i).getIncommingother().getTitle());

                    contactEntity.setIncomingMediaId(contactsMedia.getBody().get(i).getIncommingother().getId());
                    contactEntity.setIncomingSampleFileUrl(contactsMedia.getBody().get(i).getIncommingother().getSampleFileUrl());


                } else
                    contactEntity.setIsIncoming(0);

                contactList.add(contactEntity);


            }
            appDatabase.daoContacts().insertAll(contactList);
            appDatabase.daoRingtone().insertAll(ringtoneEntities);


        }

        return contactList;
    }


    private class DownloadSingleTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadSingleTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {

            File directoryToZip = new File(Utils.getFilePath(context));
            downloadFiles(directoryToZip, sUrl[0]);
            //  publishProgress((int) (1 * 100 / 100));

            return null;
        }


    }

    private class DownloadOutgoingSelfTask extends AsyncTask<List<ContactBody>, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadOutgoingSelfTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(List<ContactBody>... sUrl) {


            File directoryToZip = new File(Utils.getFilePath(context));
            List<ContactBody> bodyList = sUrl[0];


            for (int i = 0; i < bodyList.size(); i++) {

                File file = null;
                Outgoing outgoing = bodyList.get(i).getOutgoingself();
                //  appDatabase.daoRingtone().update(ringtoneEntity);

                if (outgoing != null) {

                    file = new File(directoryToZip.getPath() + "/" + outgoing.getSampleFileUrl());
                    System.out.println("DownloadTask.doInBackground-----" + file.exists() + "\t" + file.getPath());
                    if (!file.exists())
                        downloadFiles(directoryToZip, outgoing.getSampleFileUrl());
                }


            }

            return null;
        }


    }

    public RingtoneEntity getSongUrl(AppDatabase appDatabase, String phoneNumber)

    {

        String fileUrl = "";
        RingtoneEntity ringtoneEntity = appDatabase.daoRingtone().getContatcByNumber(phoneNumber);
//        if (ringtoneEntity != null) {
//            fileUrl = ringtoneEntity.getIncomingSampleFileUrl();
//        }


        return ringtoneEntity;
    }


    private class DownloadTask extends AsyncTask<AllMediaForUser, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        private AppDatabase appDatabase;

        public DownloadTask(Context context, AppDatabase appDatabase) {
            this.context = context;
            this.appDatabase = appDatabase;
        }

        @Override
        protected String doInBackground(AllMediaForUser... sUrl) {


            File directoryToZip = new File(Utils.getFilePath(context));
            AllMediaForUser allMediaForUser = sUrl[0];

            try {
                appDatabase.daoRingtone().delete();

            } catch (Exception ex) {

            }
            List<RingtoneEntity> ringtoneEntities = new ArrayList<>();

            if (allMediaForUser != null) {
                if (allMediaForUser.getByMe() != null && allMediaForUser.getByMe().size() > 0) {

                    /// set by me  as outgoing for self
                    for (int i = 0; i < allMediaForUser.getByMe().size(); i++) {

                        File file = null;
                        com.mobileoid2.celltone.network.model.allmediaforuser.Outgoing outgoing = allMediaForUser.getByMe().get(i).getOutgoingself();

                        if (outgoing != null) {

                            RingtoneEntity ringtoneEntity = new RingtoneEntity();
                            if (outgoing.getContentType().equals("audio"))
                                ringtoneEntity.setContentType("audio");
                            else
                                ringtoneEntity.setContentType("video");
                            ringtoneEntity.setOutgoingMediaId(outgoing.getId());
                            ringtoneEntity.setActionType("self");
                            ringtoneEntity.setNumber(allMediaForUser.getByMe().get(i).getMobile());
                            ringtoneEntity.setOutgoingSampleFileUrl(outgoing.getSampleFileUrl());
                            ringtoneEntities.add(ringtoneEntity);


                            file = new File(directoryToZip.getPath() + "/" + outgoing.getSampleFileUrl());
                            System.out.println("DownloadTask.doInBackground-----" + file.exists() + "\t" + file.getPath());
                            if (!file.exists())
                                downloadFiles(directoryToZip, outgoing.getSampleFileUrl());

                        }


                    }
                }
                if (allMediaForUser.getForMe() != null && allMediaForUser.getForMe().size() > 0) {

                    /// set by others for me
                    for (int i = 0; i < allMediaForUser.getForMe().size(); i++) {

                        File file = null;
                        com.mobileoid2.celltone.network.model.allmediaforuser.Outgoing outgoing = allMediaForUser.getForMe().get(i).getOutgoing();
                        com.mobileoid2.celltone.network.model.allmediaforuser.UserId userId = allMediaForUser.getForMe().get(i).getUserId();

                        if (outgoing != null) {

                            RingtoneEntity ringtoneEntity = new RingtoneEntity();
                            if (outgoing.getContentType().equals("audio"))
                                ringtoneEntity.setContentType("audio");
                            else
                                ringtoneEntity.setContentType("video");
                            ringtoneEntity.setIncomingMediaId(outgoing.getId());
                            ringtoneEntity.setActionType("self");
                            ringtoneEntity.setNumber(userId.getMobile());
                            ringtoneEntity.setIncomingSampleFileUrl(outgoing.getSampleFileUrl());
                            ringtoneEntities.add(ringtoneEntity);


                            file = new File(directoryToZip.getPath() + "/" + outgoing.getSampleFileUrl());
                            System.out.println("DownloadTask.doInBackground-----" + file.exists() + "\t" + file.getPath());
                            if (!file.exists())
                                downloadFiles(directoryToZip, outgoing.getSampleFileUrl());

                        }


                    }

                }
                appDatabase.daoRingtone().insertAll(ringtoneEntities);

            }

            return null;
        }


    }


}
