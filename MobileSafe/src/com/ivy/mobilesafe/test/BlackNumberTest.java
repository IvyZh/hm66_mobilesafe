package com.ivy.mobilesafe.test;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.ivy.mobilesafe.db.BlackNumberOpenHelper;

public class BlackNumberTest extends AndroidTestCase {

	public void create() {

	}

	// 使用openhelper来增加数据
	public void add() {
		BlackNumberOpenHelper openHelper = BlackNumberOpenHelper
				.getInstance(getContext());
		SQLiteDatabase db = openHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("type", 2);
		values.put("number", "13814239761");
		db.insert("black", null, values);
		db.close();
	}

	// 使用内容提供者
	public void add2() {
		ContentResolver resolver = getContext().getContentResolver();
		ContentValues values = new ContentValues();
		values.put("type", 1);
		values.put("number", "5556");
		resolver.insert(Uri.parse("content://com.ivy.mobilesafe.blacknumber"), values);
	}
	
	public void addFakeData(){
		for (int i = 0; i < 50; i++) {
			ContentResolver resolver = getContext().getContentResolver();
			ContentValues values = new ContentValues();
			values.put("type", i%3+1);
			values.put("number", "1000"+i);
			resolver.insert(Uri.parse("content://com.ivy.mobilesafe.blacknumber"), values);
		}
		
	}

}
