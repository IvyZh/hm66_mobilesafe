package com.ivy.mobilesafe.service;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.gsm.SmsManager;

import com.ivy.mobilesafe.log.L;
import com.ivy.mobilesafe.utils.SharedPreUtils;

public class LocationService extends Service {

	private LocationManager lm;
	private MyListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}


	@Override
	public void onCreate() {
		super.onCreate();
		L.v("服务开启了--onCreate");
		// 在这里开启定位服务
		
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		List<String> allProviders = lm.getAllProviders();//passive gps
		for (String provider : allProviders) {
			L.v(provider);
		}
		
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setCostAllowed(true);
		String provider = lm.getBestProvider(criteria, true);
		
		L.v("best provider:"+provider);//null
		listener = new MyListener();
		
		lm.requestLocationUpdates("gps", 2000, 2, listener);
	}
	
	
	class MyListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			L.v("location change");
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			double altitude = location.getAltitude();
			L.v("--latitude--"+latitude);
			L.v("--longitude--"+longitude);
			L.v("--altitude--"+altitude);
			
			sendSms(latitude,longitude);
			
			stopSelf();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			L.v("onStatusChanged,"+provider+","+status);
		}

		@Override
		public void onProviderEnabled(String provider) {
			L.v("onProviderEnabled,"+provider);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		L.v("服务--onStartCommand");
		return super.onStartCommand(intent, flags, startId);
		
	}
	
	public void sendSms(double latitude, double longitude) {
		
		String safe_phone = SharedPreUtils.getString(getApplicationContext(), "safe_phone");
		SmsManager smsManager = SmsManager.getDefault();
		String text = "latitude:"+latitude+",longitude:"+longitude;
		smsManager.sendTextMessage(safe_phone, null, text, null, null);
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		L.v("服务销毁了--onDestroy");
		lm.removeUpdates(listener);
		listener=null;
	}
}
