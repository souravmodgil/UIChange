package com.mobileoid2.celltone.pojo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoContactsUpload {

    @SerializedName("contacts")
    @Expose
    private List<String> contacts = null;

    public List<String> getContacts() {
        return contacts;
    }

    public void setContacts(List<String> contacts) {
        this.contacts = contacts;
    }

}