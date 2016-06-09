package com.ivy.mobilesafe.domain;

import android.database.Cursor;

public class Sms {
	int type;
	long date;
	String body;
	String address;
	int _id;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public static Sms createFromCursor(Cursor cursor) {
		int _id = cursor.getInt(cursor.getColumnIndex("_id"));
		int type = cursor.getInt(cursor.getColumnIndex("type"));
		String address = cursor.getString(cursor.getColumnIndex("address"));
		String body = cursor.getString(cursor.getColumnIndex("body"));
		long date = cursor.getLong(cursor.getColumnIndex("date"));

		Sms sms = new Sms(type, date, body, address, _id);

		return sms;
	}

	public Sms(int type, long date, String body, String address, int _id) {
		super();
		this.type = type;
		this.date = date;
		this.body = body;
		this.address = address;
		this._id = _id;
	}

	@Override
	public String toString() {
		return "Sms [type=" + type + ", date=" + date + ", body=" + body
				+ ", address=" + address + ", _id=" + _id + "]";
	}
	
	

}
