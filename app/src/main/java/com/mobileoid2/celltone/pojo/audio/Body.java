
package com.mobileoid2.celltone.pojo.audio;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mobileoid2.celltone.pojo.getmedia.GETMEDIATypeConverter;

@Entity(tableName = "allMedia")
public class Body {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    /////////////

    @TypeConverters(ALL_MEDIATypeConverter.class)
    @ColumnInfo(name = "genre")
    @SerializedName("genre")
    @Expose
    private List<String> genre = null;

    @TypeConverters(ALL_MEDIATypeConverter.class)
    @ColumnInfo(name = "category")
    @SerializedName("category")
    @Expose
    private List<String> category = null;

    @ColumnInfo(name = "active")
    @SerializedName("active")
    @Expose
    private Boolean active;

    @ColumnInfo(name = "deleted")
    @SerializedName("deleted")
    @Expose
    private Boolean deleted;

    @ColumnInfo(name = "_id")
    @SerializedName("_id")
    @Expose
    private String id;

    @ColumnInfo(name = "title")
    @SerializedName("title")
    @Expose
    private String title;

    @ColumnInfo(name = "contentType")
    @SerializedName("contentType")
    @Expose
    private String contentType;

    @ColumnInfo(name = "artistName")
    @SerializedName("artistName")
    @Expose
    private String artistName;

    @ColumnInfo(name = "createdAt")
    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    @ColumnInfo(name = "updatedAt")
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;


    @ColumnInfo(name = "__v")
    @SerializedName("__v")
    @Expose
    private Integer v;

    @ColumnInfo(name = "clipArtUrl")
    @SerializedName("clipArtUrl")
    @Expose
    private String clipArtUrl;

    @ColumnInfo(name = "originalFileUrl")
    @SerializedName("originalFileUrl")
    @Expose
    private String originalFileUrl;

    @ColumnInfo(name = "sampleFileUrl")
    @SerializedName("sampleFileUrl")
    @Expose
    private String sampleFileUrl;

    public List<String> getGenre() {
        return genre;
    }

    public void setGenre(List<String> genre) {
        this.genre = genre;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    public String getClipArtUrl() {
        return clipArtUrl;
    }

    public void setClipArtUrl(String clipArtUrl) {
        this.clipArtUrl = clipArtUrl;
    }

    public String getOriginalFileUrl() {
        return originalFileUrl;
    }

    public void setOriginalFileUrl(String originalFileUrl) {
        this.originalFileUrl = originalFileUrl;
    }

    public String getSampleFileUrl() {
        return sampleFileUrl;
    }

    public void setSampleFileUrl(String sampleFileUrl) {
        this.sampleFileUrl = sampleFileUrl;
    }

}
