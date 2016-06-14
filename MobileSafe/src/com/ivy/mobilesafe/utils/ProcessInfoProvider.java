package com.ivy.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.ivy.mobilesafe.R;
import com.ivy.mobilesafe.domain.ProcessInfo;
import com.ivy.mobilesafe.log.L;

public class ProcessInfoProvider {

	public static int getRunningProcessNum(Context ctx) {
		ActivityManager am = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
		return list.size();
	}

	public static ArrayList<ProcessInfo> getRunningProcess(Context ctx) {
		ActivityManager am = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);

		List<RunningAppProcessInfo> processes = am.getRunningAppProcesses();
		PackageManager pm = ctx.getPackageManager();

		
		ArrayList<ProcessInfo> list = new ArrayList<ProcessInfo>();
		for (RunningAppProcessInfo info : processes) {
			ProcessInfo processInfo = new ProcessInfo();
			processInfo.packageName = info.processName;// 包名

			
			L.v("---packagename---"+info.processName);
			try {
				ApplicationInfo appInfo = pm.getApplicationInfo(
						info.processName, 0);
				processInfo.name = appInfo.loadLabel(pm).toString();
				processInfo.icon = appInfo.loadIcon(pm);
				int flags = appInfo.flags;

				//  判断是系统还是用户
				if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
					processInfo.isUser = false;
				} else {
					processInfo.isUser = true;
				}

			} catch (NameNotFoundException e) {

				processInfo.name = info.processName;
				processInfo.icon = ctx.getResources().getDrawable(
						R.drawable.system_default);
				processInfo.isUser = false;
				e.printStackTrace();
			}
			
			int pid = info.pid;
			// 根据pid返回内存信息
			android.os.Debug.MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(new int[]{pid});
			long memory = memoryInfos[0].getTotalPrivateDirty()*1024;// 获取当前进程占用内存大小,单位是kb
			
			processInfo.memory = memory;
			
			list.add(processInfo);
		}

		return list;
	}

	// 获取总内存
	public static long getTotalMemory(Context ctx) {
		// API level 16
		// ActivityManager am = (ActivityManager)
		// ctx.getSystemService(Context.ACTIVITY_SERVICE);
		// MemoryInfo outInfo = new MemoryInfo();
		// am.getMemoryInfo(outInfo);
		// return outInfo.totalMem;

		// 为了解决版本兼容问题, 可以读取/proc/meminfo文件中第一行, 获取总内存大小
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					"/proc/meminfo"));
			String readLine = reader.readLine();// 读取第一行内容

			char[] charArray = readLine.toCharArray();
			StringBuffer sb = new StringBuffer();
			for (char c : charArray) {
				if (c >= '0' && c <= '9') {// 判断是否是数字
					sb.append(c);
				}
			}

			String total = sb.toString();// 单位kb,需要转成字节
			return Long.parseLong(total) * 1024;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	// 获取可用内存
	public static long getAvailMemory(Context ctx) {
		// API level 16
		ActivityManager am = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.availMem;
	}
	
	public static void killAllProcess(Context ctx){
		ArrayList<ProcessInfo> list = getRunningProcess(ctx);
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		for (ProcessInfo info : list) {
			am.killBackgroundProcesses(info.packageName);
			L.v("kill---"+info.packageName);
		}
	}
}
