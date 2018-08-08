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

    @ColumnInfo(name = "outgoing_sample_file_url")
    private String outgoingSampleFileUrl;
    @ColumnInfo(name = "incoming_sample_file_url")
    private String incomingSampleFileUrl;
    @ColumnInfo(name = "outgoing_media_id")
    private String outgoingMediaId;
    @ColumnInfo(name = "incoming_media_id")
    private String incomingMediaId;
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

    public String getOutgoingSampleFileUrl() {
        return outgoingSampleFileUrl;
    }

    public void setOutgoingSampleFileUrl(String outgoingSampleFileUrl) {
        this.outgoingSampleFileUrl = outgoingSampleFileUrl;
    }

    public String getIncomingSampleFileUrl() {
        return incomingSampleFileUrl;
    }

    public void setIncomingSampleFileUrl(String incomingSampleFileUrl) {
        this.incomingSampleFileUrl = incomingSampleFileUrl;
    }

    public String getOutgoingMediaId() {
        return outgoingMediaId;
    }

    public void setOutgoingMediaId(String mediaId) {
        this.outgoingMediaId = mediaId;
    }
    public String getIncomingMediaId() {
        return incomingMediaId;
    }

    public void setIncomingMediaId(String mediaId) {
        this.incomingMediaId = mediaId;
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
