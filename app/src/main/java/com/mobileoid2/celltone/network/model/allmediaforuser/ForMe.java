package com.mobileoid2.celltone.network.model.allmediaforuser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ForMe {
    @SerializedName("userId")
    @Expose
    private UserId userId;
    @SerializedName("outgoing")
    @Expose
    private Outgoing outgoing;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public Outgoing getOutgoing() {
        return outgoing;
    }

    public void setOutgoing(Outgoing outgoing) {
        this.outgoing = outgoing;
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
