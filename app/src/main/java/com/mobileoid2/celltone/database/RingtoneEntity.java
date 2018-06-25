package com.mobileoid2.celltone.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "ringtone")
public class RingtoneEntity {


    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "phone_no")
    private String number;

    @ColumnInfo(name = "sample_file_url")
    private String sampleFileUrl;
    @ColumnInfo(name = "media_id")
    private String mediaId;
    @ColumnInfo(name = "content_yype")
    private String contentType ;
    @ColumnInfo(name = "action_type")
    private String actionType ;



    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSampleFileUrl() {
        return sampleFileUrl;
    }

    public void setSampleFileUrl(String sampleFileUrl) {
        this.sampleFileUrl = sampleFileUrl;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
}
