package com.mobileoid2.celltone.network.model.contacts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContactBody {
    @SerializedName("incommingself")
    @Expose
    private Object incommingself;
    @SerializedName("incommingother")
    @Expose
    private Incommingother incommingother;
    @SerializedName("outgoingself")
    @Expose
    private Incommingother outgoingself;
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

    public Incommingother getIncommingother() {
        return incommingother;
    }

    public void setIncommingother(Incommingother incommingother) {
        this.incommingother = incommingother;
    }

    public Incommingother getOutgoingself() {
        return outgoingself;
    }

    public void setOutgoingself(Incommingother outgoingself) {
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
