package com.mobileoid2.celltone.network.model.category;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mobileoid2.celltone.network.model.treadingMedia.Song;

import java.util.List;

public class CategoryModel {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("body")
    @Expose
    private CategoryBody body;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public CategoryBody getBody() {
        return body;
    }

    public void setBody(CategoryBody body) {
        this.body = body;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
