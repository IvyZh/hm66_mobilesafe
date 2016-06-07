package com.ivy.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BlackNumberOpenHelper extends SQLiteOpenHelper {

	private static BlackNumberOpenHelper openHelper;
	public BlackNumberOpenHelper(Context context) {
		super(context, "blacknumber.db", null, 1);
	}
	
	public static BlackNumberOpenHelper getInstance(Context context){
		if(openHelper == null){
			openHelper = new BlackNumberOpenHelper(context);
		}
		return openHelper;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("create table black (_id integer primary key autoincrement ,type char,number);");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
