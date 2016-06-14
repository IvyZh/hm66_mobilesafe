package com.ivy.mobilesafe.service;

import com.ivy.mobilesafe.log.L;
import com.ivy.mobilesafe.utils.ProcessInfoProvider;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class AutoKillService extends Service {

	private InnerScreenOffReceiver mReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		// 动态注册监听
		L.v("AutoKillService onCreate ");
		mReceiver = new InnerScreenOffReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mReceiver, filter);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		L.v("AutoKillService onStartCommand ");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		L.v("AutoKillService onDestroy ");
		// 注销广播
		unregisterReceiver(mReceiver);
		mReceiver = null;
	}

	class InnerScreenOffReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			L.v("---屏幕关闭了---杀死所有进程");
			ProcessInfoProvider.killAllProcess(context);
		}

	}
}
