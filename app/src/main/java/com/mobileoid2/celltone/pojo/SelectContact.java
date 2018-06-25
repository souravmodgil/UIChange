package com.mobileoid2.celltone.pojo;

public class SelectContact {
    private int id;
    private String phoneNumber;

    public SelectContact(int id, String phoneNumber) {
        this.id = id;
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof SelectContact)) {
            return false;
        }

        SelectContact user = (SelectContact) o;

        return user.phoneNumber.equals(phoneNumber) &&
                user.id == id ;
    }
}
