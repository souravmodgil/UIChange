package com.mobileoid2.celltone.network.model.setMediaByUser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SetMediaByUserBody {
    @SerializedName("media")
    @Expose
    private Media media;
    @SerializedName("count")
    @Expose
    private Integer count;

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
