package com.ivy.mobilesafe.utils;

import java.util.List;

import com.ivy.mobilesafe.log.L;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceStatusUtils {

	public static boolean isRunning(Context ctx,String serviceName){
		
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> services = am.getRunningServices(100);
		
		for (RunningServiceInfo service : services) {
			
			String className = service.service.getClassName();
			L.v("--service runnig--"+className);//06-07 15:11:12.157: V/ivy(10838): --service runnig--
			if(className.equals(serviceName)){
				return true;
			}
		}
		
		return false;
	}
}
