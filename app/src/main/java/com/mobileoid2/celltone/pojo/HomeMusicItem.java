package com.mobileoid2.celltone.pojo;

import com.mobileoid2.celltone.network.model.treadingMedia.Song;

import java.util.List;

public class HomeMusicItem {

    String title;
    List<Song> musicPojoList;
/*
    public HomeMusicItem(String title, List<MusicPojo> musicPojoList) {
        this.title = title;
        this.musicPojoList = musicPojoList;
    }*/

    public HomeMusicItem(String title, List<Song> musicPojoList) {
        this.title = title;
        this.musicPojoList = musicPojoList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Song> getMusicPojoList() {
        return musicPojoList;
    }

    public void setMusicPojoList(List<Song> musicPojoList) {
        this.musicPojoList = musicPojoList;
    }
}
