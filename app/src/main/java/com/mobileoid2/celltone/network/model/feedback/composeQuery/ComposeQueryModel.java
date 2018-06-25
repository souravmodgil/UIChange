package com.mobileoid2.celltone.network.model.feedback.composeQuery;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ComposeQueryModel {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("body")
    @Expose
    private ComposeQueryBody body;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public ComposeQueryBody getBody() {
        return body;
    }

    public void setBody(ComposeQueryBody body) {
        this.body = body;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
