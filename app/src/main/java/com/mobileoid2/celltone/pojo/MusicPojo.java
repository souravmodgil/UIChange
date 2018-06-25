package com.mobileoid2.celltone.pojo;

public class MusicPojo {

    public String id;
    public String title;
    public String bucketDisplayName;
    public String path;
    public int duration;
    public String mimeType;
    public String resolution;
    public String artist;
    public boolean isAudio;

    public MusicPojo(String id, String title, String bucketDisplayName, String path, int duration, String mimeType, String resolution, String artist, boolean isAudio) {
        this.id = id;
        this.title = title;
        this.bucketDisplayName = bucketDisplayName;
        this.path = path;
        this.duration = duration;
        this.mimeType = mimeType;
        this.resolution = resolution;
        this.artist = artist;
        this.isAudio = isAudio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBucketDisplayName() {
        return bucketDisplayName;
    }

    public void setBucketDisplayName(String bucketDisplayName) {
        this.bucketDisplayName = bucketDisplayName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public boolean isAudio() {
        return isAudio;
    }

    public void setAudio(boolean audio) {
        this.isAudio = audio;
    }


}
