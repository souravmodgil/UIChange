package com.mobileoid2.celltone.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoOTPVerifyRequest {


    /*{
	"mobile":"9560254331",
	"otp":174005,
	"imei":"123123123123"
}*/
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("imei")
    @Expose
    private String imei;
    @SerializedName("otp")
    @Expose
    private Integer otp;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public Integer getOtp() {
        return otp;
    }

    public void setOtp(Integer otp) {
        this.otp = otp;
    }
}