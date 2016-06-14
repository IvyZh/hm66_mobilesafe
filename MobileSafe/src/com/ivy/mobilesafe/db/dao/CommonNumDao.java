package com.ivy.mobilesafe.db.dao;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CommonNumDao {

	// 获取所有常用电话的分类
	public static ArrayList<GroupInfo> getCategory(Context ctx) {
		String PATH = new File(ctx.getFilesDir(), "commonnum.db")
				.getAbsolutePath();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.query("classlist", new String[] { "name", "idx" },
				null, null, null, null, null);
		
		ArrayList<GroupInfo> list = new ArrayList<CommonNumDao.GroupInfo>();
		while (cursor.moveToNext()) {
			GroupInfo info = new GroupInfo();
			String name = cursor.getString(0);
			String idx = cursor.getString(1);

			info.name = name;
			info.idx = idx;
			info.children = getCommonNumberChildren(idx, db);

			list.add(info);
		}

		cursor.close();
		db.close();

		return list;
	}

	/**
	 * 获取某个组孩子的信息
	 * 
	 * @param idx
	 * @param database
	 * @return
	 */
	private static ArrayList<ChildInfo> getCommonNumberChildren(String idx,
			SQLiteDatabase database) {
		Cursor cursor = database.query("table" + idx, new String[] { "number",
				"name" }, null, null, null, null, null);

		ArrayList<ChildInfo> list = new ArrayList<CommonNumDao.ChildInfo>();
		while (cursor.moveToNext()) {
			ChildInfo info = new ChildInfo();
			String number = cursor.getString(0);
			String name = cursor.getString(1);
			info.number = number;
			info.name = name;

			list.add(info);
		}

		cursor.close();
		return list;
	}
	
	
	public static class GroupInfo {
		public String name;
		public String idx;
		public ArrayList<ChildInfo> children;
	}
	
	public static class ChildInfo {
		public String name;
		public String number;
	}
}
