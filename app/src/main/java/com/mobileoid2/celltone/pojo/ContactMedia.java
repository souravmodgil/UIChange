package com.mobileoid2.celltone.pojo;

import com.mobileoid2.celltone.utility.ContactEmail;
import com.mobileoid2.celltone.utility.ContactPhone;

import java.util.ArrayList;

public class ContactMedia {
    private String mobileNo;
    private String name;
    private int isOutgoingAudio;
    private String outgoingsongName;
    private String outgoingartistName;
    private int isIncomingAudio;
    private String incomingsongName;
    private String incomingartistName;
    private int isOutgoing =0;
    private int isIncomng =0;
    private int isSelected = 0;

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getName() {
        return name;
    }

    public int getIsOutgoingAudio() {
        return isOutgoingAudio;
    }

    public void setIsOutgoingAudio(int isOutgoingAudio) {
        this.isOutgoingAudio = isOutgoingAudio;
    }

    public String getOutgoingsongName() {
        return outgoingsongName;
    }

    public void setOutgoingsongName(String outgoingsongName) {
        this.outgoingsongName = outgoingsongName;
    }

    public String getOutgoingartistName() {
        return outgoingartistName;
    }

    public void setOutgoingartistName(String outgoingartistName) {
        this.outgoingartistName = outgoingartistName;
    }

    public int getIsIncomingAudio() {
        return isIncomingAudio;
    }

    public void setIsIncomingAudio(int isIncomingAudio) {
        this.isIncomingAudio = isIncomingAudio;
    }

    public String getIncomingsongName() {
        return incomingsongName;
    }

    public void setIncomingsongName(String incomingsongName) {
        this.incomingsongName = incomingsongName;
    }

    public String getIncomingartistName() {
        return incomingartistName;
    }

    public void setIncomingartistName(String incomingartistName) {
        this.incomingartistName = incomingartistName;
    }

    public int getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(int isSelected) {
        this.isSelected = isSelected;
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

    public int getIsIncomng() {
        return isIncomng;
    }

    public void setIsIncomng(int isIncomng) {
        this.isIncomng = isIncomng;
    }
}
