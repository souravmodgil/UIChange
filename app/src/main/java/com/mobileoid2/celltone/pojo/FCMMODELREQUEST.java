package com.mobileoid2.celltone.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FCMMODELREQUEST {
    @SerializedName("fcmId")
    @Expose
    private String fcpId;

    public String getFcpId() {
        return fcpId;
    }

    public void setFcpId(String fcpId) {
        this.fcpId = fcpId;
    }
}
