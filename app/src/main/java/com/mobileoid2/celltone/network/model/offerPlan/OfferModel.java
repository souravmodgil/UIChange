package com.mobileoid2.celltone.network.model.offerPlan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OfferModel {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("body")
    @Expose
    private List<OfferBody> body = null;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<OfferBody> getBody() {
        return body;
    }

    public void setBody(List<OfferBody> body) {
        this.body = body;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
