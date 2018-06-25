package com.mobileoid2.celltone.network.model.setOwnMedia;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SetOwnMediaModel {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("body")
    @Expose
    private SetOwnMediaBody body;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public SetOwnMediaBody getBody() {
        return body;
    }

    public void setBody(SetOwnMediaBody body) {
        this.body = body;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
