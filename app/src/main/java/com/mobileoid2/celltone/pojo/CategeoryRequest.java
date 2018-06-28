package com.mobileoid2.celltone.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CategeoryRequest {
    @SerializedName("limit")
    @Expose
    private int limit;
    @SerializedName("skip")
    @Expose
    private int skip;

    @SerializedName("requiredActive")
    @Expose
    private Boolean requiredActive;
    @SerializedName("orderby")
    @Expose
    private String orderby;
    @SerializedName("categoryId")
    @Expose
    private String categoryId;
    @SerializedName("mediaType")
    @Expose
    private String mediaType;
    @SerializedName("term")
    @Expose
    private String term;





    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }

    public Boolean getRequiredActive() {
        return requiredActive;
    }

    public void setRequiredActive(Boolean requiredActive) {
        this.requiredActive = requiredActive;
    }

    public String getOrderby() {
        return orderby;
    }

    public void setOrderby(String orderby) {
        this.orderby = orderby;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
