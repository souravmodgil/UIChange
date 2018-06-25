package com.mobileoid2.celltone.Module.Music.Interface;

import com.mobileoid2.celltone.Module.Music.Bean.Music;

/**
 * Created by root on 5/12/17.
 */

public interface InterfaceSongSelected {
    void onSongSelected(Music music);
    void onSongSelected(int position, boolean isAssign);
}
