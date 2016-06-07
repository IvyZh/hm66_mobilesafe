package com.ivy.mobilesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreUtils {
	private static final String SHAREDPRE_NAME = "config";
	
	// int
	public static void putInt(Context ctx ,String key,int value){
		SharedPreferences sp = ctx.getSharedPreferences(SHAREDPRE_NAME, ctx.MODE_PRIVATE);
		sp.edit().putInt(key, value).commit();
	}

	public static int getInt(Context ctx ,String key){
		SharedPreferences sp = ctx.getSharedPreferences(SHAREDPRE_NAME, ctx.MODE_PRIVATE);
		return sp.getInt(key, 0);
	}
	
	//String
	public static void putString(Context ctx ,String key,String value){
		SharedPreferences sp = ctx.getSharedPreferences(SHAREDPRE_NAME, ctx.MODE_PRIVATE);
		sp.edit().putString(key, value).commit();
	}

	public static String getString(Context ctx ,String key){
		SharedPreferences sp = ctx.getSharedPreferences(SHAREDPRE_NAME, ctx.MODE_PRIVATE);
		return sp.getString(key, "");
	}
	
	
	//boolean
	public static void putBoolean(Context ctx ,String key,boolean value){
		SharedPreferences sp = ctx.getSharedPreferences(SHAREDPRE_NAME, ctx.MODE_PRIVATE);
		sp.edit().putBoolean(key, value).commit();
	}

	public static boolean getBoolean(Context ctx ,String key){
		SharedPreferences sp = ctx.getSharedPreferences(SHAREDPRE_NAME, ctx.MODE_PRIVATE);
		return sp.getBoolean(key, false);
	}
	
	
	// remove
	public static void remove(Context ctx ,String key){
		SharedPreferences sp = ctx.getSharedPreferences(SHAREDPRE_NAME, ctx.MODE_PRIVATE);
		sp.edit().remove(key).commit();
	}
}
