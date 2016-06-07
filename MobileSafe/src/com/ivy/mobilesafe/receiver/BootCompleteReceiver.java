package com.ivy.mobilesafe.receiver;

import com.ivy.mobilesafe.log.L;
import com.ivy.mobilesafe.utils.SharedPreUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;

public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		L.v("MobileSafe BootCompleteReceiver","开机了...");
		
		boolean protect = SharedPreUtils.getBoolean(context, "protect");
		if(protect){
			TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
			String serialNumber = tm.getSimSerialNumber();
			String old = SharedPreUtils.getString(context, "serialNumber")+"x";
			if(!old.equals(serialNumber)){
				L.v("sim卡不一样，发送短信");
				String safe_phone = SharedPreUtils.getString(context, "safe_phone");
				
				SmsManager sms = SmsManager.getDefault();
				
				String text = "手机sim卡发生变动，新号码为："+tm.getLine1Number();
				sms.sendTextMessage(safe_phone, null, text , null, null);
				
			}
		}
	}

}
