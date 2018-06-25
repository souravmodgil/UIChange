package com.mobileoid2.celltone.network.model.setOwnMedia;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SetOwnMediaBody {
    @SerializedName("original")
    @Expose
    private String original;
    @SerializedName("sample")
    @Expose
    private String sample;

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }
}
