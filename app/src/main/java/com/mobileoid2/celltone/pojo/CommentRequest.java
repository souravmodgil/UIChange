package com.mobileoid2.celltone.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommentRequest {


    @SerializedName("reply")
    @Expose
    private String reply;
    @SerializedName("id")
    @Expose
    private String id;

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
