package com.ivy.mobilesafe.db.dao;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AddressDao {

	public static String getLocationByNumber(Context ctx, String number) {
		
		if(number.length()==3){
			return "报警电话";
		}else if(number.length()==4){
			return "模拟器";
		}else if(number.length()==5){
			return "客服电话";
		}else if(number.length()==6){
			return "未知电话";
		}else if(number.length()==7||number.length()==8){
			return "本地电话";
		}else if(number.length()<11){
			return "未知电话";
		}
		
		if(number.length()==11){
			// 需要用到正则表达式 TODO
			number = number.substring(0, 7);
			File file = new File(ctx.getFilesDir(), "address.db");
			if (file.exists()) {
				SQLiteDatabase db = SQLiteDatabase.openDatabase(
						file.getAbsolutePath(), null, 0);
				String sql = "select location from data2 where id = (select outkey from data1 where id='"
						+ number + "');";
				Cursor cursor = db.rawQuery(sql, null);
				if (cursor.moveToFirst()) {
					String location = cursor.getString(0);
					cursor.close();
					db.close();
					return location;
				}
	
				cursor.close();
				db.close();
				return "未知号码";
			}
		}
		return "未知号码";
	}

}
