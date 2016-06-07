package com.ivy.mobilesafe.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
	public static void show(Context ctx,String text){
		Toast.makeText(ctx, text, 0).show();
	}

}
