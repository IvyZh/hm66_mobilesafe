package com.ivy.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AppLockOpenHelper extends SQLiteOpenHelper {

	private static AppLockOpenHelper openHelper;
	public AppLockOpenHelper(Context context) {
		super(context, "applock.db", null, 1);
	}
	
	public static AppLockOpenHelper getInstance(Context context){
		if(openHelper == null){
			openHelper = new AppLockOpenHelper(context);
		}
		return openHelper;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("create table applock (_id integer primary key autoincrement ,package varchar(50));");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
