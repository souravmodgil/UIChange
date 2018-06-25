package com.mobileoid2.celltone.Util;

import android.os.Environment;

/**
 * Created by mobileoid2 on 13/11/17.
 */

public class Constant {
    public static boolean isIncoming = true;
    public static String PHONENUMBER = "";
    public static final String RECORDING_AUDIO_PATH = Environment.getExternalStorageDirectory() + "/Recordings/audio";
    public static final String RECORDING_VIDEO_PATH = Environment.getExternalStorageDirectory() + "/Recordings/video";
    public static final String RECORDING_BITMAP_PATH = Environment.getExternalStorageDirectory() + "/Recordings/bitmaps";

}
