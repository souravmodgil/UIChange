package com.mobileoid2.celltone.network.model.contacts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mobileoid2.celltone.pojo.getmedia.Outgoing;
import com.mobileoid2.celltone.pojo.getmedia.UserId;

public class ContactBody {




    @SerializedName("incommingself")
    @Expose
    private Object incommingself;
    @SerializedName("incommingother")
    @Expose
    private Outgoing incommingother;
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

    public Outgoing getIncommingother() {
        return incommingother;
    }

    public void setIncommingother(Outgoing incommingother) {
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

    @Override

    public boolean equals(Object obj) {

        return this.mobile.equals(((ContactBody) obj).mobile);


    }

}
