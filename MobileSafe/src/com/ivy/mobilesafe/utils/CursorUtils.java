package com.ivy.mobilesafe.utils;

import com.ivy.mobilesafe.log.L;

import android.R.color;
import android.database.Cursor;

public class CursorUtils {
	
	public static void print(Cursor cursor){
		if(cursor!=null){
			int count = cursor.getCount();
			L.v("CursorUtils","共有 "+count+" 条数据");
			int columnCount = cursor.getColumnCount();
			while (cursor.moveToNext()) {
				for (int i = 0; i < columnCount; i++) {
					String string = cursor.getString(i);
					String columnName = cursor.getColumnName(i);
					L.v(columnName+"---"+string);
				}
				L.v("==========================================");
			}
//			cursor.moveToFirst();
		}
	}

}
