package com.ivy.mobilesafe.service;

import java.util.ArrayList;
import java.util.List;

import com.ivy.mobilesafe.activity.EnterPwdActivity;
import com.ivy.mobilesafe.db.dao.AppLockDao;
import com.ivy.mobilesafe.log.L;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.SlidingDrawer;

public class AppLockService extends Service {

	private ArrayList<String> lockList;
	private boolean isOpen = false;
	private ActivityManager am;
	private String skipPackage;
	private MyReceiver receiver;
	private MyObserver observer;
	private AppLockDao mDao;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		L.v("applock service onCreate");

		mDao = AppLockDao.getInstance(getApplicationContext());
		lockList = mDao.findAll();
		am = (ActivityManager) getApplicationContext().getSystemService(
				ACTIVITY_SERVICE);

		receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(getPackageName() + ".SKIP");
		registerReceiver(receiver, filter);

		isOpen = true;
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (isOpen) {
					List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);
					String packageName = runningTasks.get(0).topActivity
							.getPackageName();

					L.v("--看门狗 检测到开启了栈顶Activity所在的包名---" + packageName);

					if (lockList.contains(packageName)
							&& !packageName.equals(skipPackage)) {
						L.v("---跳转输入密码页面--");

						// 跳转输入密码页面
						Intent intent = new Intent(getApplicationContext(),
								EnterPwdActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("package", packageName);// 将包名传递给输入密码页面
						startActivity(intent);
					}
					SystemClock.sleep(200);
				}
			}
		}).start();
		
		observer = new MyObserver(new Handler());
		getContentResolver().registerContentObserver(Uri.parse("content://"+getApplicationContext().getPackageName()+"/APPLOCK.CHANGE"), true, observer);
	}
	
	class MyObserver extends ContentObserver{

		public MyObserver(Handler handler) {
			super(handler);
		}
		
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			L.v("MyObserver onChange");
			lockList = mDao.findAll();
		}
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		L.v("applock service onDestroy");
		isOpen = false;
		
		if(receiver!=null){
			unregisterReceiver(receiver);
		}
		
		
		getContentResolver().unregisterContentObserver(observer);
	}

	class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			L.v("收到过滤已知包名的广播了---watchDog");
			skipPackage = intent.getStringExtra("package");
		}

	}

}
