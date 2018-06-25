package com.mobileoid2.celltone.Util;

import android.app.Activity;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.VideoView;

import com.mobileoid2.celltone.R;


/**
 * Created by mobileoid2 on 8/11/17.
 */

public class VideoPlayer {

    static VideoPlayer sInstance;
    VideoView videoView;
    MediaController mediaController;

    public static synchronized VideoPlayer getInstance(final Activity activity, VideoView videoView) {
        if (sInstance == null) sInstance = new VideoPlayer();
        sInstance.mediaController = new MediaController(activity);
        sInstance.videoView = videoView;
        sInstance.mediaController.setMediaPlayer(videoView);
        sInstance.mediaController.setAnchorView(videoView);
        videoView.setMediaController(sInstance.mediaController);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                styleMediaController(activity, sInstance.mediaController);
            }
        });
        return sInstance;
    }

    private static void styleMediaController(Activity activity, View view) {
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
            ((SeekBar) view).setProgressDrawable(activity.getResources().getDrawable(R.drawable.seekbar_progress));
            ((SeekBar) view).setThumb(activity.getResources().getDrawable(R.drawable.seekbar_thumb));
        }
    }


    public static void stop() {
        if (sInstance != null)
            if (sInstance.videoView != null) {
                sInstance.videoView.stopPlayback();
            }
    }

    public static MediaController getMediaController() {
        return sInstance.mediaController;
    }

}
