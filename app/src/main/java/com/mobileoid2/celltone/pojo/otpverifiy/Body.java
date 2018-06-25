package com.mobileoid2.celltone.pojo.otpverifiy;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Body
{
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("token")
    @Expose
    private String token;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    @Override
    public String toString()
    {
        return "ClassPojo [token = "+token+", user = "+user+"]";
    }
}