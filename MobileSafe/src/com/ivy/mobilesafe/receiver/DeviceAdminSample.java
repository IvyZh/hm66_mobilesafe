package com.ivy.mobilesafe.receiver;

import com.ivy.mobilesafe.log.L;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

public class DeviceAdminSample extends DeviceAdminReceiver {
	
	
	@Override
	public void onEnabled(Context context, Intent intent) {
		super.onEnabled(context, intent);
		L.v("--DeviceAdminSample--onEnabled");
	}
	
	@Override
	public void onDisabled(Context context, Intent intent) {
		super.onDisabled(context, intent);
		L.v("--DeviceAdminSample--onDisabled");
	}

}
