package com.ivy.mobilesafe.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.ivy.mobilesafe.domain.AppInfo;

public class AppInfoUtils {
	public static ArrayList<AppInfo> getInstallApp(Context ctx){
		PackageManager pm = ctx.getPackageManager();
		List<PackageInfo> list = pm.getInstalledPackages(0);
		ArrayList<AppInfo> appInfoList = new ArrayList<AppInfo>();
		for (PackageInfo packageInfo : list) {
			AppInfo appInfo = new AppInfo();
			appInfo.packageName = packageInfo.packageName;
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			appInfo.logo = applicationInfo.loadIcon(pm);
			appInfo.name = applicationInfo.loadLabel(pm).toString();
			
			
			int flags = applicationInfo.flags;
			if((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE)==ApplicationInfo.FLAG_EXTERNAL_STORAGE){
				appInfo.isRom = false;
			}else{
				appInfo.isRom = true;
			}
			
			if((flags& ApplicationInfo.FLAG_SYSTEM)==ApplicationInfo.FLAG_SYSTEM){
				appInfo.isUser = false;
			}else{
				appInfo.isUser = true;
			}
			
			appInfoList.add(appInfo);
		}
		return appInfoList;
	}
}
