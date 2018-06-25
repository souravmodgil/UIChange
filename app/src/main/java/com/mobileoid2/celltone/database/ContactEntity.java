package com.mobileoid2.celltone.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity(tableName = "user_contact")
public class ContactEntity  implements Serializable {

    @Ignore
    private  int isSelcted = 0;
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "_id")
    private long id;
    @ColumnInfo(name = "phone_no")
    private String number;
    @ColumnInfo(name = "phoneName")
    private String name;
    @ColumnInfo(name = "is_outgoing")
    private int isOutgoing;
    @ColumnInfo(name = "is_incoming")
    private int isIncoming;
    @ColumnInfo(name = "incoming_is_Video")
    private int isincomingVideo = 0;
    @ColumnInfo(name = "incoming_song_name" )
    private String incomingSongName;
    @ColumnInfo(name = "incoming_artist_name" )
    private String inComingArtistName;
    @ColumnInfo(name = "outgoing_is_Video")
    private int outgoingIsVideo = 0;
    @ColumnInfo(name = "outgoing_song_name" )
    private String outgoingSongName;
    @ColumnInfo(name = "outgoing_artist_name" )
    private String outgoingArtistName;

    public ContactEntity() {

    }

    public ContactEntity(int isSelcted ,String number, String name, int isOutgoing,
                         int isIncoming, int isincomingVideo, String incomingSongName,
                         String inComingArtistName, int outgoingIsVideo, String outgoingSongName, String outgoingArtistName) {

        this.isSelcted = isSelcted;
        this.number = number;
        this.name = name;
        this.isOutgoing = isOutgoing;
        this.isIncoming = isIncoming;
        this.isincomingVideo = isincomingVideo;
        this.incomingSongName = incomingSongName;
        this.inComingArtistName = inComingArtistName;
        this.outgoingIsVideo = outgoingIsVideo;
        this.outgoingSongName = outgoingSongName;
        this.outgoingArtistName = outgoingArtistName;
    }

    public int getIsSelcted() {
        return isSelcted;
    }

    public void setIsSelcted(int isSelcted) {
        this.isSelcted = isSelcted;
    }

    @NonNull
    public long getId() {
        return id;
    }

    public void setId(@NonNull long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIsOutgoing() {
        return isOutgoing;
    }

    public void setIsOutgoing(int isOutgoing) {
        this.isOutgoing = isOutgoing;
    }

    public int getIsIncoming() {
        return isIncoming;
    }

    public void setIsIncoming(int isIncoming) {
        this.isIncoming = isIncoming;
    }

    public int getIsincomingVideo() {
        return isincomingVideo;
    }

    public void setIsincomingVideo(int isincomingVideo) {
        this.isincomingVideo = isincomingVideo;
    }

    public String getIncomingSongName() {
        return incomingSongName;
    }

    public void setIncomingSongName(String incomingSongName) {
        this.incomingSongName = incomingSongName;
    }

    public String getInComingArtistName() {
        return inComingArtistName;
    }

    public void setInComingArtistName(String inComingArtistName) {
        this.inComingArtistName = inComingArtistName;
    }

    public int getOutgoingIsVideo() {
        return outgoingIsVideo;
    }

    public void setOutgoingIsVideo(int outgoingIsVideo) {
        this.outgoingIsVideo = outgoingIsVideo;
    }

    public String getOutgoingSongName() {
        return outgoingSongName;
    }

    public void setOutgoingSongName(String outgoingSongName) {
        this.outgoingSongName = outgoingSongName;
    }

    public String getOutgoingArtistName() {
        return outgoingArtistName;
    }

    public void setOutgoingArtistName(String outgoingArtistName) {
        this.outgoingArtistName = outgoingArtistName;
    }





}
