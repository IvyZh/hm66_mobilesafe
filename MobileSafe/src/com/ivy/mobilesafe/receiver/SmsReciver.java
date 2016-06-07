package com.ivy.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import com.ivy.mobilesafe.R;
import com.ivy.mobilesafe.log.L;
import com.ivy.mobilesafe.service.LocationService;
import com.ivy.mobilesafe.utils.SharedPreUtils;

/**
 * 短信接收器
 * 
 */
public class SmsReciver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		L.v("MobileSafe SmsReciver", "短信来了");
		Object[] object = (Object[]) intent.getExtras().get("pdus");
		for (Object obj : object) {
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
			String address = sms.getOriginatingAddress();
			String body = sms.getMessageBody();

			String safe_phone = SharedPreUtils.getString(context, "safe_phone");

			L.v("address:" + address + ",---body:" + body + ",--" + safe_phone);
			
			if (address.equals(safe_phone)) {
				if ("#*alarm*#".equals(body)) {// 播放音乐
					MediaPlayer mp = MediaPlayer.create(context, R.raw.ylzs);
					mp.setLooping(true);
					mp.setVolume(1.0f, 1.0f);
					mp.start();
					abortBroadcast();
				} else if ("#*location*#".equals(body)) {// 06-03 20:16:53.292:
															// V/ivy(1843):
															// address:5556,---body:#*location*#

					L.v("准备开启定位服务---");
					// 显示意图开启
					Intent locationService = new Intent(context,
							LocationService.class);

					// com.ivy.mobilesafe.location
					// 隐式意图,应用内部推荐使用显示意图
					// Intent locationService = new Intent();
					// locationService.setAction("com.ivy.mobilesafe.location");

					context.startService(locationService);
					abortBroadcast();

				} else if ("#*wipedata*#".equals(body)) {
					L.v("准备清空数据---");
					activeManage(context,0);
					abortBroadcast();
				} else if ("#*lockscreen*#".equals(body)) {
					L.v("准备锁屏---");
					activeManage(context,1);
					abortBroadcast();
				}
			}

		}
	}

	/**
	 * 激活设备
	 * @param context
	 * @param mode
	 */
	private void activeManage(Context context,int mode) {
		DevicePolicyManager mDPM = (DevicePolicyManager) context
				.getSystemService(Context.DEVICE_POLICY_SERVICE);
		// AdminReceiver 继承自 DeviceAdminReceiver
		ComponentName componentName = new ComponentName(context,
				DeviceAdminSample.class);

		boolean active = mDPM.isAdminActive(componentName);

		L.v("active:" + active);

		if (active) {
			if(mode==0){
				mDPM.lockNow();
			}else{
				mDPM.wipeData(0);
			}
		} else {
			// AdminReceiver 继承自 DeviceAdminReceiver
			Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
					"hahhahahah啊哈哈");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.getApplicationContext().startActivity(intent);
		}

	}

}
