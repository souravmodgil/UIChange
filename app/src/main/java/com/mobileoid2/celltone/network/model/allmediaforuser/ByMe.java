package com.mobileoid2.celltone.network.model.allmediaforuser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ByMe {
    @SerializedName("incommingself")
    @Expose
    private Object incommingself;
    @SerializedName("incommingother")
    @Expose
    private Object incommingother;
    @SerializedName("outgoingself")
    @Expose
    private Outgoing outgoingself;
    @SerializedName("outgoingother")
    @Expose
    private Object outgoingother;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;

    public Object getIncommingself() {
        return incommingself;
    }

    public void setIncommingself(Object incommingself) {
        this.incommingself = incommingself;
    }

    public Object getIncommingother() {
        return incommingother;
    }

    public void setIncommingother(Object incommingother) {
        this.incommingother = incommingother;
    }

    public Outgoing getOutgoingself() {
        return outgoingself;
    }

    public void setOutgoingself(Outgoing outgoingself) {
        this.outgoingself = outgoingself;
    }

    public Object getOutgoingother() {
        return outgoingother;
    }

    public void setOutgoingother(Object outgoingother) {
        this.outgoingother = outgoingother;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}
