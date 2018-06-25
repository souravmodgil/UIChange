package com.mobileoid2.celltone.network.model.feedback;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FeedBackBody {

    @SerializedName("list")
    @Expose
    private java.util.List<FeedBackList> list = null;
    @SerializedName("count")
    @Expose
    private Integer count;

    public java.util.List<FeedBackList> getList() {
        return list;
    }

    public void setList(java.util.List<FeedBackList> list) {
        this.list = list;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}
