package com.mobileoid2.celltone.network.model.upload_media_list;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UploadMediaListBody {
    @SerializedName("list")
    @Expose
    private List<UploadMediaList> list = null;
    @SerializedName("count")
    @Expose
    private Integer count;

    public List<UploadMediaList> getList() {
        return list;
    }

    public void setList(List<UploadMediaList> list) {
        this.list = list;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
