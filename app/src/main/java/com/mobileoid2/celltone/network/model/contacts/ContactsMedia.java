package com.mobileoid2.celltone.network.model.contacts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ContactsMedia  {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("body")
    @Expose
    private List<ContactBody> body = null;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<ContactBody> getBody() {
        return body;
    }

    public void setBody(List<ContactBody> body) {
        this.body = body;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
