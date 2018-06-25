package com.mobileoid2.celltone.network.model.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileBody {
    @SerializedName("user")
    @Expose
    private UserProfile user;
    @SerializedName("ownMediaCount")
    @Expose
    private Integer ownMediaCount;
    @SerializedName("planDetail")
    @Expose
    private ProfilePlanDetail planDetail;

    public UserProfile getUser() {
        return user;
    }

    public void setUser(UserProfile user) {
        this.user = user;
    }

    public Integer getOwnMediaCount() {
        return ownMediaCount;
    }

    public void setOwnMediaCount(Integer ownMediaCount) {
        this.ownMediaCount = ownMediaCount;
    }

    public ProfilePlanDetail getPlanDetail() {
        return planDetail;
    }

    public void setPlanDetail(ProfilePlanDetail planDetail) {
        this.planDetail = planDetail;
    }

}
