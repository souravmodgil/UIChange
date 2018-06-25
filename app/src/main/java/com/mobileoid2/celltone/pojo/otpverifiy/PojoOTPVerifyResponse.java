package com.mobileoid2.celltone.pojo.otpverifiy;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoOTPVerifyResponse {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("body")
    @Expose
    private Body body;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    @Override
    public String toString() {
        return "ClassPojo [message = " + message + ", body = " + body + ", status = " + status + "]";
    }
}