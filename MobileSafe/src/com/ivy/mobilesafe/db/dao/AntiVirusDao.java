package com.ivy.mobilesafe.db.dao;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AntiVirusDao {

	public static boolean isVirus(String md5, Context ctx) {

		File file = new File(ctx.getFilesDir(), "antivirus.db");

		SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(),
				null, SQLiteDatabase.OPEN_READONLY);
//		Cursor cursor = db.query("datable", null, "md5 = ?",
//				new String[] { md5 }, null, null, null);
		md5 = md5.toUpperCase();
		Cursor cursor = db.rawQuery("select * from datable where md5=?",
				new String[] { md5 });
		boolean isVirus = false;
		if (cursor.moveToFirst()) {
			isVirus = true;
		}
		cursor.close();
		db.close();
		return isVirus;
	}
	
	
	private static final String PATH = "/data/data/com.ivy.mobilesafe/files/antivirus.db";

	/**
	 * 根据apk的md5,判断是否是病毒
	 * 
	 * @param md5
	 * @return
	 */
	public static boolean isVirus(String md5) {
		SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null,
				SQLiteDatabase.OPEN_READONLY);// 打开数据库, 只支持从data/data目录打开,
												// 不能从assets打开

		md5 = md5.toUpperCase();
		Cursor cursor = database.rawQuery("select * from datable where md5=?",
				new String[] { md5 });

		boolean isVirus = false;
		if (cursor.moveToFirst()) {
			isVirus = true;
		}

		cursor.close();
		database.close();

		return isVirus;
	}

}
