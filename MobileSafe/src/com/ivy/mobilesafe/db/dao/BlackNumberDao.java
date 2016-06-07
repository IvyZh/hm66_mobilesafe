package com.ivy.mobilesafe.db.dao;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.ivy.mobilesafe.constant.Constant;
import com.ivy.mobilesafe.db.BlackNumberOpenHelper;
import com.ivy.mobilesafe.domain.BlackNumber;
import com.ivy.mobilesafe.log.L;

public class BlackNumberDao {

	/**
	 * 添加数据
	 * 
	 * @param number
	 * @param type
	 * @param ctx
	 * @return
	 */
	public static Uri add(String number, int type, Context ctx) {
		ContentResolver resolver = ctx.getContentResolver();
		ContentValues values = new ContentValues();
		values.put("type", type);
		values.put("number", "5556");
		Uri uri = resolver.insert(Uri.parse(Constant.URI.BLACK_NUMBER), values);
		return uri;
	}

	/**
	 * 删除数据
	 * 
	 * @param number
	 * @param ctx
	 * @return
	 */
	public static int delete(String number, Context ctx) {
		L.v("delete--" + number);
		ContentResolver resolver = ctx.getContentResolver();
		int delete = resolver.delete(Uri.parse(Constant.URI.BLACK_NUMBER),
				"number = ?", new String[] { number });
		return delete;
	}

	/***
	 * 查询所有数据
	 * 
	 * @param ctx
	 * @return
	 */
	public static ArrayList<BlackNumber> findAll(Context ctx) {
		ContentResolver resolver = ctx.getContentResolver();
		Cursor cursor = resolver.query(Uri.parse(Constant.URI.BLACK_NUMBER),
				null, null, null, "_id desc");
		ArrayList<BlackNumber> blackList = BlackNumber.createByCursor(cursor);
		return blackList;
	}

	/**
	 * 查询部分数据
	 * 
	 * @param ctx
	 * @return
	 */

	public static ArrayList<BlackNumber> findPart(Context ctx, int index) {
		BlackNumberOpenHelper openHelper = BlackNumberOpenHelper
				.getInstance(ctx);
		SQLiteDatabase db = openHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from black limit " + index
				+ ",20;", null);
		ArrayList<BlackNumber> blackList = BlackNumber.createByCursor(cursor);
		return blackList;
	}

	/**
	 * 获取黑名单总个数
	 * 
	 * @return
	 */
	public static int getTotalCount(Context ctx) {

		BlackNumberOpenHelper openHelper = BlackNumberOpenHelper
				.getInstance(ctx);

		SQLiteDatabase database = openHelper.getWritableDatabase();
		Cursor cursor = database.rawQuery("select count(*) from black", null);

		int count = -1;
		if (cursor.moveToFirst()) {
			count = cursor.getInt(0);
		}

		cursor.close();
		database.close();
		return count;
	}
	
	
	public static int getMode(Context ctx,String number){
		ContentResolver resolver = ctx.getContentResolver();
		Cursor cursor = resolver.query(Uri.parse(Constant.URI.BLACK_NUMBER),
				null, "number = ?", new String[]{number}, "_id desc");
		
		if(cursor.moveToFirst()){
			int type = cursor.getInt(cursor.getColumnIndex("type"));
			return type;
		}else{
			return -1;
		}
	}

}
