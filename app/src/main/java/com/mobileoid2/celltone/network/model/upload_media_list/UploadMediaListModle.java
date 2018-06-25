package com.mobileoid2.celltone.network.model.upload_media_list;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UploadMediaListModle {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("body")
    @Expose
    private UploadMediaListBody body;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public UploadMediaListBody getBody() {
        return body;
    }

    public void setBody(UploadMediaListBody body) {
        this.body = body;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
