package com.mobileoid2.celltone.network.model.category;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mobileoid2.celltone.network.model.treadingMedia.Song;

import java.util.List;

public class CategoryBody {
    @SerializedName("data")
    @Expose
    private List<Song> data = null;


    @SerializedName("count")
    @Expose
    private Integer count;

    public List<Song> getData() {
        return data;
    }

    public void setData(List<Song> data) {
        this.data = data;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
