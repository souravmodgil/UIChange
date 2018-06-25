package com.mobileoid2.celltone.Util;

/**
 * Created by mobileoid2 on 13/10/17.
 */

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;

import com.mobileoid2.celltone.R;

import java.lang.reflect.Method;

public class AudioPlayer implements MediaController.MediaPlayerControl {

    private static MediaPlayer mMediaPlayer;
    private static MediaController mMediaController;
    private static AudioPlayer sInstance;

    final static int FOR_MEDIA = 1;
    final static int FORCE_NONE = 0;
    final static int FORCE_SPEAKER = 1;
    private static boolean showController = true;
    private static int duration = 0;


    public static synchronized AudioPlayer getInstance(Activity activity, View anchorView, String path, Handler handler) throws Exception {

        if (sInstance == null) {
            sInstance = new AudioPlayer();
        }

        try {
            if (mMediaController == null && activity != null) {
                mMediaController = new MediaController(activity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (path != null) {


            if (mMediaPlayer != null) mMediaPlayer.release();
            mMediaPlayer = MediaPlayer.create(activity.getApplicationContext(), Uri.parse(path));
            mMediaPlayer.setLooping(true); // Set looping

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (anchorView == null) return;
                    if (activity == null) return;
                    if (mMediaController == null)
                        mMediaController = new MediaController(activity);
                    mMediaController.setMediaPlayer(sInstance);
                    mMediaController.setAnchorView(anchorView);

                    handler.post(new Runnable() {
                        public void run() {
                            if (activity != null && !activity.isDestroyed() && !activity.isFinishing()) {
                                if (mMediaController == null) {
                                    mMediaController = new MediaController(activity);
                                }
                                try {
                                    styleMediaController(activity, mMediaController);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                mMediaController.setEnabled(true);
                                if (showController) {
                                    try {
                                        mMediaController.show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        }
                    });


                }
            });

        }
        return sInstance;
    }

    public static int getMeidaDuration() {
        if (sInstance != null)
            if (mMediaPlayer != null)
                return mMediaPlayer.getDuration();
            else
                return duration;
        return 0;
    }

    public static void showController() {
        if (mMediaController != null)
            mMediaController.show();
    }

    private static void styleMediaController(Activity activity, View view) throws Exception {
        if (view instanceof MediaController) {
            MediaController v = (MediaController) view;
            for (int i = 0; i < v.getChildCount(); i++) {
                styleMediaController(activity, v.getChildAt(i));
            }
        } else if (view instanceof LinearLayout) {
            LinearLayout ll = (LinearLayout) view;
            for (int i = 0; i < ll.getChildCount(); i++) {
                styleMediaController(activity, ll.getChildAt(i));
            }
        } else if (view instanceof SeekBar) {
            ((SeekBar) view).setProgressDrawable(activity.getResources().getDrawable(R.drawable.seekbar_background_fill));
            ((SeekBar) view).setThumb(activity.getResources().getDrawable(R.drawable.slider_line_handle));
        }
    }

    public static boolean isMusicLoaded() {
        if ((sInstance != null || mMediaPlayer != null)) {
            return true;
        }
        return false;
    }

    public static boolean isMusicPlaying() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying())
                return true;
            else
                return false;
        }
        return false;
    }

    public static void stop() throws Exception {
        if (mMediaController != null)
            mMediaController.hide();

        if (mMediaPlayer != null) {
            duration = mMediaPlayer.getDuration();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mMediaController = null;
        }
    }

    public static void setControllerVisibility(boolean visibility) {
        showController = visibility;
    }

    public static MediaController getmMediaController() {
        return mMediaController;
    }

    public static void play() throws Exception {
        //stop();
        Log.e("AudioPlayer.play", "" + mMediaPlayer);
        if (mMediaPlayer != null)
            mMediaPlayer.start();
    }

    @Override
    public void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    @Override
    public void start() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }


    public int getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    public int getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public void seekTo(int i) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(i);
        }
    }

    public boolean isPlaying() {

        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }

        return false;
    }

    public int getBufferPercentage() {
        return 0;
    }

    public boolean canPause() {
        return true;
    }

    public boolean canSeekBackward() {
        return true;
    }

    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }


}