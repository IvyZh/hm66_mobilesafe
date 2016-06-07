package com.ivy.mobilesafe.provider;

import com.ivy.mobilesafe.db.BlackNumberOpenHelper;
import com.ivy.mobilesafe.log.L;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class BlackNumberContentPrvider extends ContentProvider {

	private static final String TABLE = "black";
	private SQLiteDatabase db;

	@Override
	public boolean onCreate() {
		BlackNumberOpenHelper openHelper = BlackNumberOpenHelper.getInstance(getContext());
		db = openHelper.getWritableDatabase();
		
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		reLive();
		Cursor cursor = db.query(TABLE, projection, selection, selectionArgs, null, null, sortOrder);
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		reLive();
		long insert = db.insert(TABLE, null, values);
		return Uri.withAppendedPath(uri, insert+"");
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		L.v("---selection--"+selection);
		reLive();
		return db.delete(TABLE, selection, selectionArgs);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}

	private void reLive(){
		if(!db.isOpen()){
			BlackNumberOpenHelper openHelper = BlackNumberOpenHelper.getInstance(getContext());
			db = openHelper.getWritableDatabase();
		}
	}
}
