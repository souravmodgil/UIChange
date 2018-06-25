package com.mobileoid2.celltone.network.model.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserProfile {
    @SerializedName("currentPlan")
    @Expose
    private CurrentPlan currentPlan;
    @SerializedName("otp")
    @Expose
    private Object otp;
    @SerializedName("usedMedia")
    @Expose
    private List<String> usedMedia = null;
    @SerializedName("isVerified")
    @Expose
    private Boolean isVerified;
    @SerializedName("imei")
    @Expose
    private String imei;
    @SerializedName("fcmId")
    @Expose
    private Object fcmId;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("countryCode")
    @Expose
    private String countryCode;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("__v")
    @Expose
    private Integer v;
    @SerializedName("avatar")
    @Expose
    private String avatar;

    public CurrentPlan getCurrentPlan() {
        return currentPlan;
    }

    public void setCurrentPlan(CurrentPlan currentPlan) {
        this.currentPlan = currentPlan;
    }

    public Object getOtp() {
        return otp;
    }

    public void setOtp(Object otp) {
        this.otp = otp;
    }

    public List<String> getUsedMedia() {
        return usedMedia;
    }

    public void setUsedMedia(List<String> usedMedia) {
        this.usedMedia = usedMedia;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public Object getFcmId() {
        return fcmId;
    }

    public void setFcmId(Object fcmId) {
        this.fcmId = fcmId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

}
