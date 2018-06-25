package com.mobileoid2.celltone.network.model.myPlan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllPlan {

    private int isSelected =0;
    @SerializedName("validity")
    @Expose
    private Object validity;
    @SerializedName("expiry")
    @Expose
    private Object expiry;
    @SerializedName("published")
    @Expose
    private Boolean published;
    @SerializedName("isDefaultPlan")
    @Expose
    private Boolean isDefaultPlan;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("mediaCount")
    @Expose
    private Integer mediaCount;
    @SerializedName("userCount")
    @Expose
    private Integer userCount;
    @SerializedName("ownMediaCount")
    @Expose
    private Integer ownMediaCount;
    @SerializedName("price")
    @Expose
    private Integer price;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("__v")
    @Expose
    private Integer v;

    public Object getValidity() {
        return validity;
    }

    public void setValidity(Object validity) {
        this.validity = validity;
    }

    public Object getExpiry() {
        return expiry;
    }

    public void setExpiry(Object expiry) {
        this.expiry = expiry;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Boolean getIsDefaultPlan() {
        return isDefaultPlan;
    }

    public void setIsDefaultPlan(Boolean isDefaultPlan) {
        this.isDefaultPlan = isDefaultPlan;
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

    public Integer getMediaCount() {
        return mediaCount;
    }

    public void setMediaCount(Integer mediaCount) {
        this.mediaCount = mediaCount;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    public Integer getOwnMediaCount() {
        return ownMediaCount;
    }

    public void setOwnMediaCount(Integer ownMediaCount) {
        this.ownMediaCount = ownMediaCount;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
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

    public int getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(int isSelected) {
        this.isSelected = isSelected;
    }

    public Boolean getDefaultPlan() {
        return isDefaultPlan;
    }

    public void setDefaultPlan(Boolean defaultPlan) {
        isDefaultPlan = defaultPlan;
    }
}
