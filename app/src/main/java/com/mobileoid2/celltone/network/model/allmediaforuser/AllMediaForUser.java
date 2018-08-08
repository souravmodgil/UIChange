package com.mobileoid2.celltone.network.model.allmediaforuser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllMediaForUser {
    @SerializedName("forMe")
    @Expose
    private List<ForMe> forMe = null;
    @SerializedName("byMe")
    @Expose
    private List<ByMe> byMe = null;

    public List<ForMe> getForMe() {
        return forMe;
    }

    public void setForMe(List<ForMe> forMe) {
        this.forMe = forMe;
    }

    public List<ByMe> getByMe() {
        return byMe;
    }

    public void setByMe(List<ByMe> byMe) {
        this.byMe = byMe;
    }
}
