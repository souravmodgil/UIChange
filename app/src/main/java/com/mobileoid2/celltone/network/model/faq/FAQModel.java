package com.mobileoid2.celltone.network.model.faq;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FAQModel {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("body")
    @Expose
    private List<FAQBody> body = null;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<FAQBody> getBody() {
        return body;
    }

    public void setBody(List<FAQBody> body) {
        this.body = body;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
