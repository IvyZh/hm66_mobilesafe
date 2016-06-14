package com.ivy.mobilesafe.db.dao;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.ivy.mobilesafe.db.AppLockOpenHelper;
import com.ivy.mobilesafe.domain.AppInfo;

public class AppLockDao {

	private static AppLockDao sinstance = null;
	private static AppLockOpenHelper openHelper = null;
	private final Context ctx;

	private AppLockDao(Context ctx) {
		this.ctx = ctx;
		openHelper = new AppLockOpenHelper(ctx);
	}

	public static AppLockDao getInstance(Context context) {
		if (sinstance == null) {
			synchronized (AppLockDao.class) {
				if (sinstance == null) {
					sinstance = new AppLockDao(context);
				}
			}
		}
		return sinstance;
	}

	public void add(String packageName) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("package", packageName);
		db.insert("applock", null, values);
		db.close();
		
		ContentResolver resolver = ctx.getContentResolver();
		resolver.notifyChange(Uri.parse("content://com.ivy.mobilesafe"+"/APPLOCK.CHANGE"), null);
	}
	
	
	public void delete(String packageName){
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.delete("applock", "package=?", new String[]{packageName});
		db.close();
		ContentResolver resolver = ctx.getContentResolver();
		resolver.notifyChange(Uri.parse("content://com.ivy.mobilesafe"+"/APPLOCK.CHANGE"), null);
	}
	
	public boolean findByPackage(String packageName){
		SQLiteDatabase db = openHelper.getWritableDatabase();
		Cursor cursor = db.query("applock", null, "package=?", new String[]{packageName}, null, null, null);
		boolean exist = false;
		if(cursor.moveToFirst()){
			exist = true;
		}
		cursor.close();
		db.close();
		return exist;
	}

	public ArrayList<String> findAll() {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		Cursor cursor = db.query("applock", null,null, null, null, null, null);
		
		ArrayList<String> lockList = new ArrayList<String>();
		while(cursor.moveToNext()){
			String packageName = cursor.getString(cursor.getColumnIndex("package"));
			lockList.add(packageName);
		}
		cursor.close();
		db.close();
		return lockList;
	}

}
