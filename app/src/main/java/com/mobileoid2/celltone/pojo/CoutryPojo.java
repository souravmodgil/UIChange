package com.mobileoid2.celltone.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CoutryPojo {
    @SerializedName("coutryCode")
    @Expose
    private List<CoutryCode> coutryCode = null;

    public List<CoutryCode> getCoutryCode() {
        return coutryCode;
    }

    public void setCoutryCode(List<CoutryCode> coutryCode) {
        this.coutryCode = coutryCode;
    }
}
