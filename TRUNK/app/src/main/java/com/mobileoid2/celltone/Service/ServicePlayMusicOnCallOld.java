package com.mobileoid2.celltone.Service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.Toast;
import android.widget.VideoView;

import com.mobileoid2.celltone.Module.Base.ActivityBase;
import com.mobileoid2.celltone.Module.Contacts.Bean.BeanContacts;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.Util.AppSharedPref;
import com.mobileoid2.celltone.Util.AppUtils;
import com.mobileoid2.celltone.Util.Constant;
import com.mobileoid2.celltone.database.AppDatabase;
import com.mobileoid2.celltone.database.DatabaseConstants;

import java.io.File;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

import static android.content.ContentValues.TAG;

/**
 * Created by mobileoid2 on 13/10/17.
 */

public class ServicePlayMusicOnCallOld extends Service {
    private WindowManager windowManager;
    private VideoView videoView;
    private View videoLayout;
    private Notification notice;
    private int myID = 40693;
    private String selectedtrack = "";
    private SwitchButtonListener switchButtonListener;
    private AudioPlayer audioPlayer;
    private int ringerMode = -1;

    public ServicePlayMusicOnCallOld(Context applicationContext) {
        super();
    }

    public ServicePlayMusicOnCallOld() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        System.out.println("ServiceOugoingCallMusicPlay.onStartCommand");

        if (!isAccessibilitySettingsOn(getApplicationContext())) {
            Toast.makeText(this, "Switch on Accessibility for this application", Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(this, "reading for contact :" + Constant.PHONENUMBER, Toast.LENGTH_SHORT).show();

        if (Constant.PHONENUMBER == null)
            return START_STICKY;

        if (Constant.PHONENUMBER.equals(""))
            return START_STICKY;


        if (Constant.PHONENUMBER.contains("+"))
            Constant.PHONENUMBER = getRealPhoneNo(Constant.PHONENUMBER);
        else
            Constant.PHONENUMBER = AppUtils.instance.fixPhoneNo(Constant.PHONENUMBER);


        BeanContacts selectedContact = AppDatabase.getAppDatabase(this).daoContacts().findByName("%" + Constant.PHONENUMBER);

        if (selectedContact == null) {
            Toast.makeText(this, "Contact is null", Toast.LENGTH_SHORT).show();
            startForegroundMethods();
            stopSelf();
            return START_STICKY;
        }


        String pathOfFileToBePlayed = "";
        String isVideo = "";
        if (Constant.isIncoming) {
            pathOfFileToBePlayed = selectedContact.getMusicIncomingPath();
            isVideo = selectedContact.getIsIncomingVideo();
        } else {
            pathOfFileToBePlayed = selectedContact.getMusicOutgoingPath();
            isVideo = selectedContact.getIsOutgoingVideo();
        }


        if (pathOfFileToBePlayed.equalsIgnoreCase("")) {
            startForegroundMethods();
            stopSelf();
            return START_STICKY;
        }

        if (!new File(pathOfFileToBePlayed).exists()) {
            startForegroundMethods();
            stopSelf();
            return START_STICKY;
        }


        File f = new File(pathOfFileToBePlayed);
        if (f.exists() && !f.isDirectory()) {
            // file is present


            try {
                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                if (audioManager != null) {
                    ringerMode = audioManager.getRingerMode();

                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    audioManager.setParameters("incall_music_enabled=true");
                    if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0)
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_SHOW_UI);

                    if (Constant.isIncoming) {
                        // if (audioManager.isWiredHeadsetOn()) {
                        //    audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM), 0);
                        // } else {

                        audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), AudioManager.FLAG_SHOW_UI);
                        //}

                    } else {

                        
                        audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                        if (!audioManager.isWiredHeadsetOn())
                            audioManager.setSpeakerphoneOn(true);
                    }
                }
            } catch (Exception e) {
                Log.e("OutgoingService", AppUtils.instance.getExceptionString(e));
            }


            if (switchButtonListener == null) switchButtonListener = new SwitchButtonListener();
            registerReceiver(switchButtonListener, new IntentFilter("CATCH_SWITCH_CLICK"));


            String[] results = pathOfFileToBePlayed.split("/");

            selectedtrack = (results[results.length - 1]).split("\\.")[0];

            if (isVideo.equals(DatabaseConstants.VALUE_FALSE)) {
                audioPlayer = new AudioPlayer(this, pathOfFileToBePlayed);
                audioPlayer.play();
            }

            if (isVideo.equals(DatabaseConstants.VALUE_TRUE)) {
                startVideoViewNew(pathOfFileToBePlayed);
            }

            startForegroundMethods();
        } else {
            startForegroundMethods();
            stopSelf();
        }
        return START_STICKY;
    }

    private void startVideoViewNew(String pathOfFileToBePlayed) {


        LayoutInflater layoutInflator = LayoutInflater.from(getApplicationContext());

        videoLayout = layoutInflator.inflate(R.layout.layout_video_player_on_call, null);
        videoView = videoLayout.findViewById(R.id.video_view);
        videoView.setVisibility(View.VISIBLE);
        Uri uri = Uri.parse(pathOfFileToBePlayed);
        videoView.setVideoURI(uri);

        videoView.start();

        ImageButton imageButtonPlay = videoLayout.findViewById(R.id.image_button_play);
        imageButtonPlay.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));

        imageButtonPlay.setTag(true);
        imageButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                if ((boolean) view1.getTag()) {
                    videoView.pause();
                    imageButtonPlay.setTag(false);
                    imageButtonPlay.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
                } else {
                    videoView.start();
                    imageButtonPlay.setTag(true);
                    imageButtonPlay.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
                }

            }
        });

        RelativeLayout.LayoutParams paramsRelative = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AppSharedPref.instance.getScreenHeight() / 3);
        videoView.setLayoutParams(paramsRelative);


        int flagTypeScreen = WindowManager.LayoutParams.TYPE_PHONE;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            flagTypeScreen = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                AppSharedPref.instance.getScreenHeight() / 3 + 10,
                flagTypeScreen,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE//so that it is read by accessibility service
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER;
        // params.x = 10;
        // params.y = 300;

        windowManager.addView(videoLayout, params);

        ImageButton imageButtonClose = videoLayout.findViewById(R.id.image_button_close);
        imageButtonClose.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel));
        imageButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeVideoView(videoLayout);
            }
        });

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
                Log.e("VIDEO VIEW", "" + videoView.getHeight());

/*

                int videoViewHeight = videoView.getHeight();

                LinearLayout layoutUpper = videoLayout.findViewById(R.id.layout_upper);
                LinearLayout layoutLower = videoLayout.findViewById(R.id.layout_lower);
                RelativeLayout layoutVideoView = videoLayout.findViewById(R.id.layout_video_view);
                int requiredHeight = AppSharedPref.instance.getScreenHeight() / 2;
                requiredHeight = requiredHeight - videoViewHeight;

                layoutUpper.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, requiredHeight));
                layoutLower.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, requiredHeight));
                layoutVideoView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, videoViewHeight + 4));
*/


                if (layoutInflator == null)
                    Log.e("Layout Inflator", "Layout Inflator is null");
            }
        }.execute();
    }

    private void closeVideoView(View view) {

        if (videoView != null) {
            videoView.stopPlayback();
            videoView.setVisibility(View.GONE);
        }

        if (view != null && windowManager != null) {
            windowManager.removeView(view);
            view = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startForegroundMethods() {

        try {
            Intent intent = new Intent(this, ActivityBase.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendIntent = PendingIntent.getActivity(this, 0, intent, 0);
            // This constructor is deprecated. Use Notification.Builder instead
            notice = new Notification(R.mipmap.ic_launcher_round, getString(R.string.app_name), System.currentTimeMillis());
            // This method is deprecated. Use Notification.Builder instead.

            notice.flags |= Notification.FLAG_NO_CLEAR;
            Intent notificationIntent = null;
            notificationIntent = new Intent(getApplicationContext(), ActivityBase.class);
            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.remote_notification_view_custom);
            contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher_round);
            contentView.setTextViewText(R.id.title, "Now playing");
            contentView.setTextViewText(R.id.text, selectedtrack);
            contentView.setImageViewResource(R.id.image_button_close, android.R.drawable.ic_menu_close_clear_cancel);

            //this is the intent that is supposed to be called when the
            //button is clicked
            Intent switchIntent = new Intent("CATCH_SWITCH_CLICK");
            PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 0, switchIntent, 0);
            contentView.setOnClickPendingIntent(R.id.image_button_close, pendingSwitchIntent);
            notice.contentView = contentView;
            notice.contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            startForeground(myID, notice);

        } catch (Exception e) {
            Log.e("Main Service", AppUtils.instance.getExceptionString(e));
        }

    }

    public class SwitchButtonListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopService(new Intent(context, ServicePlayMusicOnCallOld.class));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (switchButtonListener != null) unregisterReceiver(switchButtonListener);
            if (videoView != null && videoLayout != null && windowManager != null)
                closeVideoView(videoLayout);
            if (audioPlayer != null) audioPlayer.stop();

            Constant.PHONENUMBER = "";

            try {
                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                if (audioManager != null) {
                    if (audioManager.isSpeakerphoneOn())
                        audioManager.setSpeakerphoneOn(false);
                    if (ringerMode != -1) {
                        audioManager.setRingerMode(ringerMode);
                        ringerMode = -1;
                    }
                    //  audioManager.adjustStreamVolume(AudioManager.USE_DEFAULT_STREAM_TYPE, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);


                    audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), AudioManager.FLAG_SHOW_UI);
                    //      audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_SHOW_UI);
                    //       audioManager.setStreamVolume(AudioManager.USE_DEFAULT_STREAM_TYPE, audioManager.getStreamMaxVolume(AudioManager.USE_DEFAULT_STREAM_TYPE), AudioManager.FLAG_SHOW_UI);

                }
            } catch (Exception e) {
                Log.e("OutgoingService", AppUtils.instance.getExceptionString(e));
            }


            stopForeground(true);
        } catch (Exception e) {
            Log.e("Main Service", AppUtils.instance.getExceptionString(e));
        }
    }

    private boolean isAccessibilitySettingsOn(Context mContext) {
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
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
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

    private String getRealPhoneNo(String phoneNumberE164Format) {
        if (phoneNumberE164Format.equals("")) return "";
        String realPhoneNo = "";
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.createInstance(getApplicationContext());
        try {
            Phonenumber.PhoneNumber phoneNumberProto = phoneUtil.parse(phoneNumberE164Format, null);
            boolean isValid = phoneUtil.isValidNumber(phoneNumberProto); // returns true if valid
            if (isValid) {
                // Actions to perform if the number is valid
                realPhoneNo = String.valueOf(phoneNumberProto.getNationalNumber());
            } else {
                // Do necessary actions if its not valid
            }
        } catch (NumberParseException e) {
            Log.e("NumberParseException", AppUtils.instance.getExceptionString(e));
            realPhoneNo = "";
        }
        return realPhoneNo;
    }

    public class AudioPlayer {

        private MediaPlayer mMediaPlayer;
        private Handler mHandler;

        public AudioPlayer(Context context, String path) {
            if (path != null) {
                if (mMediaPlayer != null) mMediaPlayer.release();
                mMediaPlayer = MediaPlayer.create(context, Uri.parse(path));
                try {
                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    if (audioManager.isWiredHeadsetOn())
                        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                    else
                        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                } catch (Exception e) {
                    Log.e("AudioManagerException", AppUtils.instance.getExceptionString(e));
                }
                if (mHandler == null)
                    mHandler = new Handler();
            }
        }

        private Runnable mUpdateTime = new Runnable() {
            public void run() {
                if (mMediaPlayer != null)
                    if (mMediaPlayer.isPlaying()) {
                        int currentDuration = mMediaPlayer.getCurrentPosition();
                        if (currentDuration / 1000 > 30 || Constant.PHONENUMBER.equals("")) {
                            stop();
                        } else {
                            mHandler.postDelayed(mUpdateTime, 1000);
                        }

                    }
            }
        };

        public void stop() {
            if (mMediaPlayer != null) {
                if (mMediaPlayer.isPlaying())
                    mMediaPlayer.stop();
                mMediaPlayer.release();
                mHandler.removeCallbacks(mUpdateTime);
                mMediaPlayer = null;
            }
        }


        public void play() {
            if (mMediaPlayer != null) {
                mMediaPlayer.start();
                mHandler.post(mUpdateTime);
            }
        }
    }

}