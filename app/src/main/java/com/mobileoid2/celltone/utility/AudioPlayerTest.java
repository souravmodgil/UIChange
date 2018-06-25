package com.mobileoid2.celltone.utility;

/**
 * Created by mobileoid2 on 13/10/17.
 */

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.mobileoid2.celltone.R;

public class AudioPlayerTest {

    private static MediaPlayer mMediaPlayer;
    private static AudioPlayerTest sInstance;

    public static synchronized AudioPlayerTest getInstance(Context context) {


        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        if (sInstance == null || mMediaPlayer==null) {
            sInstance = new AudioPlayerTest();
            mMediaPlayer = MediaPlayer.create(context, R.raw.test);
            mMediaPlayer.setLooping(true); // Set looping
            mMediaPlayer.setVolume(1000,1000);
            final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setParameters("incall_music_enabled=true");
            audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM,
                    AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);

        }
        return sInstance;
    }


    public static void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public  static void play() {
        //stop();
        System.out.println("AudioPlayer.play"+mMediaPlayer);
        if (mMediaPlayer!=null)
        mMediaPlayer.start();




    }


}