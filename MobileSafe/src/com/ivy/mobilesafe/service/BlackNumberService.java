package com.ivy.mobilesafe.service;

import com.ivy.mobilesafe.db.dao.BlackNumberDao;
import com.ivy.mobilesafe.log.L;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.gsm.SmsMessage;

public class BlackNumberService extends Service {

	private SmsBlockBroadCast smsBlockBroadCast;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		L.v("--黑名单拦截服务--onCreate");
		// 注册黑名单短信拦截服务

		smsBlockBroadCast = new SmsBlockBroadCast();

		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(Integer.MAX_VALUE);
		registerReceiver(smsBlockBroadCast, filter);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		L.v("--黑名单拦截服务--onStart");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 取消拦截短信广播
		L.v("--黑名单拦截服务--onDestory");
		unregisterReceiver(smsBlockBroadCast);
		smsBlockBroadCast = null;
	}

	/**
	 * 短信广播接受者
	 */
	class SmsBlockBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			L.v("---smsBlockBroadCast---");
			Object[] object = (Object[]) intent.getExtras().get("pdus");

			for (Object obj : object) {
				SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
				String address = sms.getOriginatingAddress();
				int mode = BlackNumberDao.getMode(getApplicationContext(),
						address);
				L.v("--黑名单--短信来了--" + address + ",mode:" + mode);

				if (mode == 1) {// 电话拦截

				} else if (mode == 2) {// 短信拦截
					abortBroadcast();
				} else if (mode == 3) {// 全部拦截
					abortBroadcast();
				}
			}

		}

	}

}
