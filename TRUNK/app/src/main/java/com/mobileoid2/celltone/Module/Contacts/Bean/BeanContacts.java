package com.mobileoid2.celltone.Module.Contacts.Bean;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.mobileoid2.celltone.Module.Music.Bean.Music;
import com.mobileoid2.celltone.Util.AppUtils;

import java.io.Serializable;

/**
 * Created by mobileoid2 on 9/11/17.
 */
@Entity(tableName = "phone_contacts")
public class BeanContacts implements Serializable, Comparable<BeanContacts> {

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "_id")
    private String id = "";

    @ColumnInfo(name = "phone_no")
    private String number;

    @ColumnInfo(name = "phoneName")
    private String name;


    @ColumnInfo(name = "music_path_incoming")
    private String musicIncomingPath;

    @ColumnInfo(name = "music_thumb_incoming")
    private String musicIncomingThumbNail;

    @ColumnInfo(name = "is_video_incoming")
    private String isIncomingVideo = "999";


    @ColumnInfo(name = "music_path_outgoing")
    private String musicOutgoingPath;

    @ColumnInfo(name = "music_thumb_outgoing")
    private String musicOutgoingThumbNail;

    @ColumnInfo(name = "is_video_outgoing")
    private String isOutgoingVideo = "999";


    @Ignore
    public BeanContacts() {
    }

    public BeanContacts(@NonNull String id, String number, String name, String musicIncomingPath, String musicIncomingThumbNail, String isIncomingVideo, String musicOutgoingPath, String musicOutgoingThumbNail, String isOutgoingVideo) {
        this.id = id;
        this.number = number;
        this.name = name;
        this.musicIncomingPath = musicIncomingPath;
        this.musicIncomingThumbNail = musicIncomingThumbNail;
        this.isIncomingVideo = isIncomingVideo;
        this.musicOutgoingPath = musicOutgoingPath;
        this.musicOutgoingThumbNail = musicOutgoingThumbNail;
        this.isOutgoingVideo = isOutgoingVideo;
    }

    public String getId() {
        return AppUtils.instance.stringValidator(id);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return AppUtils.instance.stringValidator(number);
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return AppUtils.instance.stringValidator(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMusicIncomingPath() {

        return AppUtils.instance.stringValidator(musicIncomingPath);
    }

    public void setMusicIncomingPath(String musicPath) {
        this.musicIncomingPath = musicPath;
    }

    public String getMusicIncomingThumbNail() {
        return AppUtils.instance.stringValidator(musicIncomingThumbNail);
    }

    public void setMusicIncomingThumbNail(String musicThumbNail) {
        this.musicIncomingThumbNail = musicThumbNail;
    }

    public String getIsIncomingVideo() {
        return AppUtils.instance.stringValidator(isIncomingVideo);
    }

    public void setIsIncomingVideo(String isVideo) {
        this.isIncomingVideo = isVideo;
    }


    public String getMusicOutgoingPath() {
        return musicOutgoingPath;
    }

    public void setMusicOutgoingPath(String musicOutgoingPath) {
        this.musicOutgoingPath = musicOutgoingPath;
    }

    public String getMusicOutgoingThumbNail() {
        return musicOutgoingThumbNail;
    }

    public void setMusicOutgoingThumbNail(String musicOutgoingThumbNail) {
        this.musicOutgoingThumbNail = musicOutgoingThumbNail;
    }

    public String getIsOutgoingVideo() {
        return isOutgoingVideo;
    }

    public void setIsOutgoingVideo(String isOutgoingVideo) {
        this.isOutgoingVideo = isOutgoingVideo;
    }

    @Override
    public int compareTo(BeanContacts o) {
        return this.getName().compareToIgnoreCase(o.getName());
    }


    @Override
    public String toString() {
        return this.getName().toLowerCase() + ":" + this.getNumber().toLowerCase();
    }

    @Override
    public boolean equals(Object obj) {
        return this.getId().equalsIgnoreCase(((BeanContacts) obj).getId());
    }
}