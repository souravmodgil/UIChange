package com.mobileoid2.celltone.pojo;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by mobileoid2 on 9/11/17.
 */
@Entity(tableName = "music_database")
public class Music implements Comparable<Music>, Serializable {

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "_id")
    private String id = "";

    @ColumnInfo(name = "thumb_url")
    private String thumbUrl = "";

    @ColumnInfo(name = "song_title")
    private String songTitle = "";

    @ColumnInfo(name = "song_album")
    private String songAlbum = "";

    @ColumnInfo(name = "songs_path")
    private String songsPath = "";

    @ColumnInfo(name = "is_video")
    private String isVideo = "999";

    @ColumnInfo(name = "gerne")
    private String gerne = "other";


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongAlbum() {
        return songAlbum;
    }

    public void setSongAlbum(String songAlbum) {
        this.songAlbum = songAlbum;
    }

    public String getSongsPath() {
        return songsPath;
    }

    public void setSongsPath(String songsPath) {
        this.songsPath = songsPath;
    }

    @Override
    public String toString() {
        return songTitle;
    }


    public String getIsVideo() {
        return isVideo;
    }

    public void setIsVideo(String isVideo) {
        this.isVideo = isVideo;
    }

    public String getGerne() {
        return gerne;
    }

    public void setGerne(String gerne) {
        this.gerne = gerne;
    }

    @Override
    public int compareTo(Music object) {
        return this.songTitle.compareTo(object.songTitle);
    }


    @Override
    public boolean equals(Object obj) {
        return this.songTitle.equalsIgnoreCase(((Music) obj).getSongTitle());
    }
}
