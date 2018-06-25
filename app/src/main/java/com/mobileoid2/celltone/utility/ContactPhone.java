package com.mobileoid2.celltone.utility;

public class ContactPhone {
	public String number;
	public String type;

	public ContactPhone(String number, String type) {
		this.number = number;
		this.type = type;
	}

	@Override
	public String toString() {
		return number+"-"+type;
	}
}
