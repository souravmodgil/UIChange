package com.mobileoid2.celltone.network.model.myPlan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlanBody {
    @SerializedName("currentPlan")
    @Expose
    private CurrentPlan currentPlan;
    @SerializedName("currentPlanDetail")
    @Expose
    private CurrentPlanDetail currentPlanDetail;
    @SerializedName("allPlans")
    @Expose
    private List<AllPlan> allPlans = null;
    @SerializedName("at")
    @Expose
    private String at;

    public CurrentPlan getCurrentPlan() {
        return currentPlan;
    }

    public void setCurrentPlan(CurrentPlan currentPlan) {
        this.currentPlan = currentPlan;
    }

    public CurrentPlanDetail getCurrentPlanDetail() {
        return currentPlanDetail;
    }

    public void setCurrentPlanDetail(CurrentPlanDetail currentPlanDetail) {
        this.currentPlanDetail = currentPlanDetail;
    }

    public List<AllPlan> getAllPlans() {
        return allPlans;
    }

    public void setAllPlans(List<AllPlan> allPlans) {
        this.allPlans = allPlans;
    }

    public String getAt() {
        return at;
    }

    public void setAt(String at) {
        this.at = at;
    }

}
