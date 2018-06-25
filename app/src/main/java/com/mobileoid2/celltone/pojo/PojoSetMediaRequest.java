package com.mobileoid2.celltone.pojo;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoSetMediaRequest {

    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("callType")
    @Expose
    private String callType;
    @SerializedName("actionType")
    @Expose
    private String actionType;
    @SerializedName("mediaId")
    @Expose
    private String mediaId;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

}
