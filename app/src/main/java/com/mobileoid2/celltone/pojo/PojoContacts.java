package com.mobileoid2.celltone.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoContacts{

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("mobileNumer")
    @Expose
    private String mobileNumer;
    @SerializedName("isSongSet")
    @Expose
    private Boolean isSongSet;
    @SerializedName("isAudio")
    @Expose
    private Boolean isAudio;
    @SerializedName("path")
    @Expose
    private String path;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumer() {
        return mobileNumer;
    }

    public void setMobileNumer(String mobileNumer) {
        this.mobileNumer = mobileNumer;
    }

    public Boolean getIsSongSet() {
        return isSongSet;
    }

    public void setIsSongSet(Boolean isSongSet) {
        this.isSongSet = isSongSet;
    }

    public Boolean getIsAudio() {
        return isAudio;
    }

    public void setIsAudio(Boolean isAudio) {
        this.isAudio = isAudio;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}