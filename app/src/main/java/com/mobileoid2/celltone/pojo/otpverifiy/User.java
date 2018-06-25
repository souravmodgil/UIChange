package com.mobileoid2.celltone.pojo.otpverifiy;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mobileoid2.celltone.network.model.myPlan.CurrentPlan;

public class User
{
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("countryCode")
    @Expose
    private String countryCode;
    @SerializedName("currentPlan")
    @Expose
    private CurrentPlan currentPlan;
    @SerializedName("type")
    @Expose
    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public CurrentPlan getCurrentPlan() {
        return currentPlan;
    }

    public void setCurrentPlan(CurrentPlan currentPlan) {
        this.currentPlan = currentPlan;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

