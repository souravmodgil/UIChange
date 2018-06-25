package com.mobileoid2.celltone.network.model.offerPlan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApplicablePlan {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;

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

}
