package com.mobileoid2.celltone.network.model.myPlan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MyPlanModel {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("body")
    @Expose
    private PlanBody body;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public PlanBody getBody() {
        return body;
    }

    public void setBody(PlanBody body) {
        this.body = body;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
