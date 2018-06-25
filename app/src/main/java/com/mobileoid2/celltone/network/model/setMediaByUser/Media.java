package com.mobileoid2.celltone.network.model.setMediaByUser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Media {
    @SerializedName("originalFileUrl")
    @Expose
    private String originalFileUrl;
    @SerializedName("sampleFileUrl")
    @Expose
    private String sampleFileUrl;
    @SerializedName("clipArtUrl")
    @Expose
    private String clipArtUrl;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("contentType")
    @Expose
    private String contentType;

    public String getOriginalFileUrl() {
        return originalFileUrl;
    }

    public void setOriginalFileUrl(String originalFileUrl) {
        this.originalFileUrl = originalFileUrl;
    }

    public String getSampleFileUrl() {
        return sampleFileUrl;
    }

    public void setSampleFileUrl(String sampleFileUrl) {
        this.sampleFileUrl = sampleFileUrl;
    }

    public String getClipArtUrl() {
        return clipArtUrl;
    }

    public void setClipArtUrl(String clipArtUrl) {
        this.clipArtUrl = clipArtUrl;
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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
