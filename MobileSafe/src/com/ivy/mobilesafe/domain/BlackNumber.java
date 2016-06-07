package com.ivy.mobilesafe.domain;

import java.util.ArrayList;

import com.ivy.mobilesafe.log.L;

import android.database.Cursor;

public class BlackNumber {

	public int type;
	public String number;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public BlackNumber(int type, String number) {
		super();
		this.type = type;
		this.number = number;
	}

	public static ArrayList<BlackNumber> createByCursor(Cursor cursor) {
		ArrayList<BlackNumber> list = new ArrayList<BlackNumber>();
		int count = cursor.getCount();
		L.v("一共有 "+ count+" 条数据");
		if (cursor != null) {
			while (cursor.moveToNext()) {
				String number = cursor.getString(cursor
						.getColumnIndex("number"));
				int type = cursor.getInt(cursor.getColumnIndex("type"));
				BlackNumber blackNumber = new BlackNumber(type, number);
				list.add(blackNumber);
			}
		}

		return list;
	}

}
