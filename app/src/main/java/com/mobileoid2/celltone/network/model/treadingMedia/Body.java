package com.mobileoid2.celltone.network.model.treadingMedia;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Body {

    @SerializedName("trending")
    @Expose
    private List<Trending> trending = null;
    @SerializedName("category")
    @Expose
    private List<Category> category = null;
    @SerializedName("ownMedia")
    @Expose
    private List<OwnMedium> ownMedia = null;

    public List<Trending> getTrending() {
        return trending;
    }

    public void setTrending(List<Trending> trending) {
        this.trending = trending;
    }

    public List<Category> getCategory() {
        return category;
    }

    public void setCategory(List<Category> category) {
        this.category = category;
    }

    public List<OwnMedium> getOwnMedia() {
        return ownMedia;
    }

    public void setOwnMedia(List<OwnMedium> ownMedia) {
        this.ownMedia = ownMedia;
    }


}
