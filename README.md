# hm66_mobilesafe
黑马66期手机安全卫士

* 代码可能有所不同


---

1. 把ADT工作空间的编码统一换成`UTF-8`
2. 抽象类及抽象方法要`public`修饰
3. 去掉应用Title

	    <style name="AppTheme" parent="AppBaseTheme">
       		 <item name="android:windowNoTitle">true</item>
  		</style>

4. 输入流InputStream转String

		public class StreamUtils {
		
			public static String InputStream2String(InputStream is) throws IOException{
				byte[] b = new byte[1024];
				int len = 0;
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				while((len = is.read(b)) != -1){
					//把读到的字节先写入字节数组输出流中存起来
					bos.write(b, 0, len);
				}
				//把字节数组输出流中的内容转换成字符串
				//默认使用utf-8
				String text = new String(bos.toByteArray());
				return text;
			}
		}

5. TextView实现跑马灯效果

	    android:ellipsize="marquee"
        android:focusable="true"
        android:focusableInTouchMode="true"


	或者通过继承TextView，将里面的`isFocused`返回`true`。

6. 去除低版本Dialog黑边：

		dialog.setView(view, 0, 0, 0, 0);// 去掉上下左右边距, 兼容2.x版本

7. 自定义控件（SettingItemView）
	1. 自定义View的属性，首先在res/values/  下建立一个attrs.xml ， 在里面定义我们的属性和声明我们的整个样式。

			<?xml version="1.0" encoding="utf-8"?>
			<resources>
			    <declare-styleable name="CustomTitleView">
			        <attr name="title" format="string" />
			        <attr name="desc_on" format="string" />
			        <attr name="desc_off" format="string" />
			    </declare-styleable>
			
			</resources>

	2. 布局中声明我们的自定义View

		    <com.ivy.mobilesafe.ui.SettingItemView
	        android:id="@+id/siv_bind"
	        android:layout_width="match_parent"
	        ivy:desc_off="sim卡未绑定"
	        ivy:desc_on="sim卡已绑定"
	        ivy:title="点击绑定sim卡"
	        android:layout_height="wrap_content" />

		一定要引入 xmlns:ivy="http://schemas.android.com/apk/res/com.ivy.mobilesafe" 我们的命名空间，后面的包路径指的是项目的package

	3. 在View的构造方法中，获得我们的自定义的样式

			desc_off = attrs.getAttributeValue(NAME_SPACE, "desc_off");

8. overridePendingTransition 属性可以给Activity跳转添加动画
9. 获取Sim卡信息要用到 `TelephonManager` 需要权限。
10. 调用系统联系人界面 TODO
11. 注册开机启动的监听
12. 注册短信监听
	1. 在api16的时候可以提示action
		1. <action android:name="android.provider.Telephony.SMS_RECEIVED"/>


13. 开启定位服务
	1. 可以用显示开启
	2. 也可以用隐式开启，应用内调用推荐显示。
	3. lm = (LocationManager) getSystemService(LOCATION_SERVICE);
	4. 得到所有可用List<String> allProviders = lm.getAllProviders();//passive gps

			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setCostAllowed(true);
			String provider = lm.getBestProvider(criteria, true);
	5. lm.requestLocationUpdates("gps", 2000, 2, listener);
	6. class MyListener implements LocationListener{


14. 锁屏
15. 清空数据

	以上两个功能，包括设置密码，都没有能够在安全卫士项目上得以实现，在新建的“一件锁屏”项目中可以删除。

16. 插补器
	1. R.anim.shake
	
			<?xml version="1.0" encoding="utf-8"?>
			<translate xmlns:android="http://schemas.android.com/apk/res/android"
			android:duration="1000"
			android:fromXDelta="0"
			android:interpolator="@anim/cycle_7"
			android:toXDelta="10" />
	2. R.anim.cycle
		
			<?xml version="1.0" encoding="utf-8"?>
			<cycleInterpolator xmlns:android="http://schemas.android.com/apk/res/android"
			    android:cycles="7" />

17. 震动

		private void vibrator() {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(2000);
		}

18. 判断服务是否正在运行

		public static boolean isRunning(Context ctx){
			
			ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningServiceInfo> services = am.getRunningServices(100);
			
			for (RunningServiceInfo service : services) {
				if(service.service.getPackageName().equals(ctx.getPackageName())){
					return true;
				}
			}
			
			return false;
		}


19. 监听来电

		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneStateListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);


		class MyPhoneStateListener extends PhoneStateListener {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
	
				switch (state) {
				case TelephonyManager.CALL_STATE_IDLE:
	
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
	
					break;
				case TelephonyManager.CALL_STATE_RINGING:
					String location = AddressDao.getLocationByNumber(getApplicationContext(), incomingNumber);
					L.v("电话来了--"+incomingNumber+","+location);
					
					showToast(location);
					break;
	
				}
			}
		}


		// 取消来电监听
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;


20. 监听去电

		outCallBroadCast = new OutCallBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(outCallBroadCast, filter);

		// 取消去电监听
		unregisterReceiver(outCallBroadCast);
		outCallBroadCast = null;

		权限：android.permission.PROCESS_OUTGOING_CALLS

		class OutCallBroadCast extends BroadcastReceiver{
	
			@Override
			public void onReceive(Context context, Intent intent) {
				
				
				String data = getResultData();
				String location = AddressDao.getLocationByNumber(context, data);
				
				L.v("有电话拨出去了"+data+","+location);
				
				showToast(location);
			}
		}

21. 显示自定义Toast

		/**
		 * 显示来电
		 * 
		 * @param location
		 *  <uses-permission android:name="android.permission.PROCESS_OUTGOING_CALLS"/>
		 */
		public void showToast(String location) {
			mWM = (WindowManager) getApplication().getSystemService(
					Context.WINDOW_SERVICE);
	
			mScreenWidth = mWM.getDefaultDisplay().getWidth();// 屏幕宽度
			mScreenHeight = mWM.getDefaultDisplay().getHeight();// 屏幕高度
	
			// 初始化布局参数
			final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
			params.height = WindowManager.LayoutParams.WRAP_CONTENT;
			params.width = WindowManager.LayoutParams.WRAP_CONTENT;
			params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
			// | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
					| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
			params.format = PixelFormat.TRANSLUCENT;
			params.type = WindowManager.LayoutParams.TYPE_PHONE;// 修改窗口类型为TYPE_PHONE,保证可以触摸
			params.setTitle("Toast");
			params.gravity = Gravity.LEFT + Gravity.TOP;// 将重心设定到左上方的位置,这样的话,坐标体系就以左上方为准,
														// 方便设定布局位置, 默认是Center
			mView = View.inflate(this, R.layout.custom_toast, null);
			TextView tvAddress = (TextView) mView.findViewById(R.id.tv_address);
			tvAddress.setText(location);
			mWM.addView(mView, params);
			
			
		}


21. Activity 设置半透明
	* 给Activity设置半透明的主题
	* 在xml布局文件加一个背景就好了

22. DragView

		ivDrag.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:

					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					

					break;
				case MotionEvent.ACTION_MOVE:
					int endX = (int) event.getRawX();
					int endY = (int) event.getRawY();

					int dx = endX - startX;
					int dy = endY - startY;

					int top = ivDrag.getTop() + dy;
					int left = ivDrag.getLeft() + dx;
					int bottom = ivDrag.getBottom() + dy;
					int right = ivDrag.getRight() + dx;

					
					if(bottom>mHeight||top<0){
						return true;
					}
					
					if(right>mWidth||left<0){
						return true;
					}
					
					
					
					if (top < mHeight / 2) {
						tvBottom.setVisibility(View.VISIBLE);
						tvTop.setVisibility(View.INVISIBLE);
					} else {
						tvTop.setVisibility(View.VISIBLE);
						tvBottom.setVisibility(View.INVISIBLE);
					}

					ivDrag.layout(left, top, right, bottom);

					startX = (int) event.getRawX();
					startY = (int) event.getRawY();

					break;
				case MotionEvent.ACTION_UP:
					
					int pos_x = ivDrag.getLeft();
					int pos_y = ivDrag.getTop();
					
					SharedPreUtils.putInt(getApplicationContext(), "pos_x", pos_x);
					SharedPreUtils.putInt(getApplicationContext(), "pos_y", pos_y);
					break;

				}

				return true;
			}
		});

		//-----------------------------
		int pos_x = SharedPreUtils.getInt(this, "pos_x");
		int pos_y = SharedPreUtils.getInt(this, "pos_y");
		
		// 根据当前位置,显示文本框提示
		if (pos_y > mHeight / 2) {
			// 下方
			tvTop.setVisibility(View.VISIBLE);
			tvBottom.setVisibility(View.INVISIBLE);
		} else {
			// 上方
			tvTop.setVisibility(View.INVISIBLE);
			tvBottom.setVisibility(View.VISIBLE);
		}
		
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) ivDrag.getLayoutParams();
		params.topMargin  = pos_y;
		params.leftMargin = pos_x;
		
		// ivDrag.setLayoutParams(params);


23. 更新Toast位置

		mView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:

					startX = (int) event.getRawX();
					startY = (int) event.getRawY();

					break;
				case MotionEvent.ACTION_MOVE:
					int endX = (int) event.getRawX();
					int endY = (int) event.getRawY();

					int dx = endX - startX;
					int dy = endY - startY;

					params.x = params.x + dx;
					params.y = params.y + dy;

					if (params.x > mScreenWidth - mView.getWidth()) {
						params.x = mScreenWidth - mView.getWidth();
					}

					if (params.y > mScreenHeight - mView.getHeight()) {
						params.y = mScreenHeight - mView.getHeight();
					}

					if (params.x < 0) {
						params.x = 0;
					}
					if (params.y < 0) {
						params.y = 0;
					}

					// 更新窗口布局
					mWM.updateViewLayout(mView, params);

					startX = (int) event.getRawX();
					startY = (int) event.getRawY();

					break;
				case MotionEvent.ACTION_UP:
					SharedPreUtils.putInt(getApplicationContext(), "pos_x",
							params.x);
					SharedPreUtils.putInt(getApplicationContext(), "pos_y",
							params.y);
					break;

				}

				return true;
			}
		});

24. 双击


		// 数组拷贝:参1:原数组;参2:原数组拷贝起始位置;参3:目标数组;参4:目标数组起始拷贝位置;参5:拷贝数组长度
		System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
		mHits[mHits.length - 1] = SystemClock.uptimeMillis();// 手机开机时间
		if (SystemClock.uptimeMillis() - mHits[0] <= 500) {
 
		}


25. 加入双击之后，和触摸的冲突了，是因为居中方法写错了

		protected void center() {
			ivDrag.layout(mWidth / 2 - ivDrag.getWidth() / 2, ivDrag.getTop(),
					mWidth / 2 + ivDrag.getWidth() / 2, ivDrag.getBottom());
		}


26. 小火箭
27. 黑名单（使用内容观察者实现
	1. 注册短信拦截

			// 注册黑名单短信拦截服务

			smsBlockBroadCast = new SmsBlockBroadCast();
	
			IntentFilter filter = new IntentFilter();
			filter.addAction("android.provider.Telephony.SMS_RECEIVED");
			filter.setPriority(Integer.MAX_VALUE);
			registerReceiver(smsBlockBroadCast, filter);
	2. 解除注册

			L.v("--黑名单拦截服务--onDestory");
			unregisterReceiver(smsBlockBroadCast);
			smsBlockBroadCast = null;
	3. 短信的广播接受者

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
	
28. 判断服务是否在运行

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


30. 电话拦截
31. 删除来电记录
32. 短信备份

		try {
			XmlSerializer serializer = Xml.newSerializer();
			File file = new File(getFilesDir(), "sms_backup.xml");
			serializer.setOutput(new FileOutputStream(file ), "utf-8");
			serializer.startDocument("utf-8", false);
			serializer.startTag(null, "smss");
			for (int i=0;i<smsList.size();i++) {
				Sms sms = smsList.get(i);
				pb.setProgress(i);
				Thread.sleep(50);
				serializer.startTag(null, "sms");
				
				serializer.startTag(null, "_id");
				serializer.text(sms.get_id()+"");
				serializer.endTag(null, "_id");
				
				serializer.startTag(null, "type");
				serializer.text(sms.getType()+"");
				serializer.endTag(null, "type");
				
				serializer.startTag(null, "address");
				serializer.text(sms.getAddress());
				serializer.endTag(null, "address");
				
				serializer.startTag(null, "body");
				serializer.text(sms.getBody());
				serializer.endTag(null, "body");
				
				serializer.startTag(null, "date");
				serializer.text(sms.getDate()+"");
				serializer.endTag(null, "date");
				
				serializer.endTag(null, "sms");
				
				System.out.println("finish---");
			}
			
			serializer.endTag(null, "smss");
			serializer.endDocument();
			
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					pb.setVisibility(View.GONE);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

33. 创建快捷方式（一键呼叫

		Intent shortcutinntent = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		// 不允许重复创建
		shortcutintent.putExtra("duplicate", false);
		// 需要现实的名称
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机卫士");
		// 快捷图片
		Parcelable icon = Intent.ShortcutIconResource.fromContext(
				getApplicationContext(), R.drawable.ic_launcher);
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
		// 点击快捷图片，运行的程序主入口
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(
				getApplicationContext(), SplashActivity.class));
		// 发送广播。OK
		sendBroadcast(shortcutitent);

	权限：

 		<uses-permission android:name="com.android.launcher.permission.INSTALL_SHORTCUT" />


34. 获取SdCard和rom大小

		private String getStorage(String path) {
			StatFs statFs = new StatFs(path);
			
			int blockCount = statFs.getBlockCount();
			int availableBlocks = statFs.getAvailableBlocks();
			int blockSize = statFs.getBlockSize();
			String allSize = Formatter.formatFileSize(this, blockSize*blockCount);
			String avaSize = Formatter.formatFileSize(this, blockSize*availableBlocks);
			
			return avaSize;
		}


35. 获得安装的APP列表

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
				appInfoList.add(appInfo);
			}
			return appInfoList;
		}


36. 判断应用是系统应用还是用户应用，安装在外部还是内部存储空间

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

37. BaseAdapter的两个方法

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0 || position == userAppList.size() + 1) {
				return 0;
			} else {
				return 1;
			}
		}


38. 监听ListView滑动事件

		if(firstVisibleItem <= userAppList.size()){
			tvHeader.setText("用户应用(" + userAppList.size() + ")");
		}else{
			tvHeader.setText("系统应用(" + systemAppList.size()
					+ ")");
		}

39. popupwindow

		protected void showPopupWindow(View view) {
			if(popupWindow!=null && popupWindow.isShowing()){
				popupWindow.dismiss();
			}else{
				View contentView = View.inflate(this, R.layout.popup_item_appinfo, null);
				PopupWindow popupWindow = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
				popupWindow.setBackgroundDrawable(new ColorDrawable(android.R.color.holo_blue_light));
				
				// 加入监听
				
				// 加入动画
				
				ScaleAnimation sa = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
				AlphaAnimation aa = new AlphaAnimation(0, 1);
				AnimationSet as = new AnimationSet(false);
				as.setDuration(500);
				as.addAnimation(sa);
				as.addAnimation(aa);
				
				popupWindow.showAsDropDown(view,  50, -view.getHeight());
				contentView.startAnimation(as);
			}
			
		}

40. 卸载应用

		private void uninstall() {
			if(mCurrentApp!=null&&mCurrentApp.isUser){
				Intent intent = new Intent();
				//intent.setAction(Intent.ACTION_UNINSTALL_PACKAGE);//api 14
				intent.setAction(Intent.ACTION_DELETE);
				intent.setData(Uri.parse("package:"+mCurrentApp.packageName));
				startActivityForResult(intent, 0);
			}else{
				ToastUtils.show(this, "系统应用不能删除");
			}
		}

41. 启动应用
	
		private void launch() {
			if (mCurrentApp != null) {
				PackageManager pm = getPackageManager();
				Intent intent = pm.getLaunchIntentForPackage(mCurrentApp.packageName);
				if(intent!=null){
					startActivity(intent);
				}else{
					ToastUtils.show(this, "找不到启动界面");
				}
			}
		}

42. 分享应用

		private void share() {
			Intent intent = new Intent(Intent.ACTION_SEND);
			iintent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_TEXT, "分享一个APP，下载地址: https://play.google.com/store/apps/details?id="+mCurrentApp.packageName);
			startActivity(intent);
		}

43. ExpandableListView的使用

	 	<ExpandableListView
	        android:id="@+id/elv_list"
	        android:layout_width="match_parent"
	        android:layout_height="wrap_content" >
	    </ExpandableListView>

	
		class MyAdapter extends BaseExpandableListAdapter{
			// ....
		}

44. 获取运行中进程数量

		public static int getRunningProcessNum(Context ctx){
			ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
			return list.size();
		}

45. 格式化显示文本

		String.format("运行中的进程:%d个", mRunningProcessNum)

46. 获取总内存

			// 获取总内存
			public static long getTotalMemory(Context ctx){
				//API level 16
		//		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		//		MemoryInfo outInfo = new MemoryInfo();
		//		am.getMemoryInfo(outInfo);
		//		return outInfo.totalMem;
				
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
	

47. 获取可用内存

		// 获取可用内存
		public static long getAvailMemory(Context ctx){
			//API level 16
			ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
			MemoryInfo outInfo = new MemoryInfo();
			am.getMemoryInfo(outInfo);
			return outInfo.availMem;
		}


48. 获取进程信息（icon name packagename memory）


		public static ArrayList<ProcessInfo> getRunningProcess(Context ctx) {
			ActivityManager am = (ActivityManager) ctx
					.getSystemService(Context.ACTIVITY_SERVICE);
	
			List<RunningAppProcessInfo> processes = am.getRunningAppProcesses();
			PackageManager pm = ctx.getPackageManager();
	
			ProcessInfo processInfo = new ProcessInfo();
			ArrayList<ProcessInfo> list = new ArrayList<ProcessInfo>();
			for (RunningAppProcessInfo info : processes) {
				processInfo.packageName = info.processName;// 包名
	
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


49. 一键清理


		// 一键清理
		// 需要权限: <uses-permission
		// android:name="android.permission.KILL_BACKGROUND_PROCESSES" />
		public void killAll(View view) {
			ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	
			// java.util.ConcurrentModificationException,并发修改异常,遍历集合过程中,修改集合元素个数
			// foreach会出现此问题
			ArrayList<ProcessInfo> killedList = new ArrayList<ProcessInfo>();// 被清理的进程集合
			for (ProcessInfo info : mUserList) {
				if (info.isChecked) {
					am.killBackgroundProcesses(info.packageName);
					// mUserList.remove(info);
					killedList.add(info);
				}
			}
	
			boolean showSystem = PrefUtils.getBoolean("show_system", true, this);
			if (showSystem) {// 如果不展示系统应用,就不用清除系统进程
				for (ProcessInfo info : mSystemList) {
					if (info.isChecked) {
						am.killBackgroundProcesses(info.packageName);
						// mSystemList.remove(info);
						killedList.add(info);
					}
				}
			}
	
			long savedMemory = 0;
			for (ProcessInfo processInfo : killedList) {
				if (processInfo.isUser) {
					mUserList.remove(processInfo);
				} else {
					mSystemList.remove(processInfo);
				}
	
				savedMemory += processInfo.memory;
			}
	
			mAdapter.notifyDataSetChanged();
	
			ToastUtils
					.showToast(this, String.format("帮您杀死了%d个进程,共节省%s空间!",
							killedList.size(),
							Formatter.formatFileSize(this, savedMemory)));
	
			// 更新文本信息
			mRunningProcessNum -= killedList.size();
			mAvailMemory += savedMemory;
			tvRunningNum.setText(String.format("运行中的进程:%d个", mRunningProcessNum));
			tvMemoInfo.setText(String.format("剩余/总内存:%s/%s",
					Formatter.formatFileSize(this, mAvailMemory),
					Formatter.formatFileSize(this, mTotalMemory)));
	
		}


50. 注册屏幕关闭杀死所有进程的Service


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


		记得要在清单文件里面注册

51. widget
52. 程序锁
	1. 构造AppLock的数据库和dao
	
			public class AppLockDao {
	
				private static AppLockDao sinstance = null;
				private static AppLockOpenHelper openHelper = null;
			
				private AppLockDao(Context ctx) {
					openHelper = new AppLockOpenHelper(ctx);
				}
			
				public static AppLockDao getInstance(Context context) {
					if (sinstance == null) {
						synchronized (AppLockDao.class) {
							if (sinstance == null) {
								sinstance = new AppLockDao(context);
							}
						}
					}
					return sinstance;
				}
				// ...crud
			}	
53. AppLockService
	1. 线程睡眠
		1. SystemClock.sleep(200);
	2. 开启线程，查询当前任务栈的Activity所在包名
			
			// 获取当前屏幕展示的页面
			// 需要权限:android.permission.GET_TASKS
			List<RunningTaskInfo> runningTasks = mAM.getRunningTasks(1);// 获取当前运行的任务栈,返回一条最新的任务栈
			String packageName = runningTasks.get(0).topActivity.getPackageName();// 获取栈顶activity所在的包名
	3. 输入密码界面
		1. 输入正确密码之后，需要发个广播通知看门狗，要过滤掉该条应用
			
				// 通知看门狗,跳过当前包名的验证
				Intent intent = new Intent();
				intent.setAction(getPackageName()+".SKIP");
				intent.putExtra("package", packageName);// 传递包名
				sendBroadcast(intent);
		2. 同时还需要注册广播接受者

				MyReceiver receiver = new MyReceiver();
				IntentFilter filter = new IntentFilter();
				filter.addAction(getPackageName() + ".SKIP");
				registerReceiver(receiver, filter);
		3. 注意事项（1）——点返回键要回到桌面
		
				@Override
				public void onBackPressed() {
					// 跳到桌面
					Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_HOME);
					startActivity(intent);
					finish();
				}
		4. 注意事项（2）设置启动模式:

			 
		   		<activity
		            android:name="com.itheima.mobilesafe66.activity.EnterPwdActivity"
		            android:launchMode="singleInstance" >
			            
			    任务栈只允许有一个activity, 解决手机卫士退到后台,被呼起的问题
			     
			   当此页面呼起时,不展示在最近运行的应用中
			   
			      <activity
			            android:name="com.itheima.mobilesafe66.activity.EnterPwdActivity"
			            android:launchMode="singleInstance" 
			            android:excludeFromRecents="true"




54. 程序锁优化（并发修改数据库 + 发通知说明数据库改变了）

		public void delete(String packageName) {
			SQLiteDatabase database = mHelper.getWritableDatabase();
			database.delete("applock", "package=?", new String[] { packageName });
			database.close();
	
			// 通知数据库发生变化
			mContext.getContentResolver().notifyChange(
					Uri.parse("content://com.itheima.mobilesafe66/change"), null);
		}

55. 注册上面的内容观察者

		observer = new MyObserver(new Handler());
			getContentResolver().registerContentObserver(Uri.parse("content://"+getApplicationContext().getPackageName()+"/APPLOCK.CHANGE"), true, observer);
		
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


56. 以上

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


57. 流量统计

	* 真机可以看到流量
		* proc/uid_stat
			* tcp_rcv
			* tcp_snd
		* 重启就会清零
	* 系统给了API读取流量的
		* TrafficStatsa
			* 可以获取某个应用的，3g+wifi
			* 可以获取3g
			* 可以获取总共3g+wifi
	
	- 流量拦截
	
		应该有root权限, linux有开源软件iptable
	
		droidwall, Android下专用的流量拦截软件, 开源
	
		https://code.google.com/

			String totalRxBytes = "总下载流量:" + TrafficStats.getTotalRxBytes();// wifi+3g
			String totalTxBytes = "总上传流量:" + TrafficStats.getTotalTxBytes();// wifi+3g
	
			String mobileRxBytes = "移动下载流量" + TrafficStats.getMobileRxBytes();// 3g下载流量
			String mobileTxBytes = "移动上传流量" + TrafficStats.getMobileTxBytes();// 3g下载流量
	
			// 具体微信uid以真机为准
			String uidRxBytes = "微信下载流量:" + TrafficStats.getUidRxBytes(10088);// wifi+3g
			String uidTxBytes = "微信上传流量:" + TrafficStats.getUidTxBytes(10088);// wifi+3g



58. 抽屉效果实现 SlidingDrawer

	    <LinearLayout
	        android:layout_width="match_parent"
	        android:layout_height="match_parent"
	        android:orientation="horizontal" >
	
	        <View
	            android:layout_width="100dp"
	            android:layout_height="match_parent" />
	
	        <SlidingDrawer
	            android:layout_width="match_parent"
	            android:layout_height="match_parent"
	            android:content="@+id/content"
	            android:handle="@+id/handle"
	            android:orientation="horizontal" >
	
	            <LinearLayout
	                android:id="@id/handle"
	                android:layout_width="wrap_content"
	                android:layout_height="match_parent"
	                android:orientation="vertical" >
	
	                <ImageView
	                    android:layout_width="wrap_content"
	                    android:layout_height="wrap_content"
	                    android:src="@drawable/lock" />
	            </LinearLayout>
	
	            <LinearLayout
	                android:id="@id/content"
	                android:layout_width="match_parent"
	                android:layout_height="match_parent"
	                android:background="#9e9e9e"
	                android:gravity="center" >
	
	                <TextView
	                    android:id="@+id/tv_tcp"
	                    android:layout_width="wrap_content"
	                    android:layout_height="wrap_content"
	                    android:text="我是小抽屉" />
	            </LinearLayout>
	        </SlidingDrawer>
	    </LinearLayout>


59. 杀毒


修改progressbar的样式：

	<ProgressBar
    android:id="@+id/pb_progress"
    style="?android:attr/progressBarStyleHorizontal"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginTop="5dp"
    android:progressDrawable="@drawable/custom_progress" />


custom_progress.xml：


	<?xml version="1.0" encoding="utf-8"?>
	<layer-list xmlns:android="http://schemas.android.com/apk/res/android" >
	
	    <item
	        android:id="@android:id/background"
	        android:drawable="@drawable/security_progress_bg"/>
	    <item
	        android:id="@android:id/secondaryProgress"
	        android:drawable="@drawable/security_progress"/>
	    <item
	        android:id="@android:id/progress"
	        android:drawable="@drawable/security_progress"/>
	
	</layer-list>


计算文件的MD5：

	/**
	 * 计算文件md5
	 * 
	 * @param filePath
	 * @return
	 */
	public static String encodeFile(String filePath) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");

			FileInputStream in = new FileInputStream(filePath);
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = in.read(buffer)) != -1) {
				digest.update(buffer, 0, len);
			}

			byte[] bytes = digest.digest();

			StringBuffer sb = new StringBuffer();
			for (byte b : bytes) {
				int i = b & 0xff;
				String hexString = Integer.toHexString(i);
				// System.out.println(hexString);
				if (hexString.length() == 1) {
					hexString = "0" + hexString;
				}

				sb.append(hexString);
			}

			String md5 = sb.toString();

			return md5;
		} catch (Exception e) {
			// 没有此算法异常
			e.printStackTrace();
		}

		return null;
	}


Cursor查询注意条件的大小写

	Cursor cursor = db.rawQuery("select * from datable where md5=?",new String[] { md5 });


60. 缓存清理


TabHost 使用,需要继承TabActivity

		setContentView(R.layout.activity_cache_tab);


		TabHost tabHost = getTabHost();

		// 初始化一个标签
		TabSpec tab1 = tabHost.newTabSpec("Clean_Cache");
		tab1.setIndicator("缓存清理");// 系统默认样式
		tab1.setContent(new Intent(this, CleanCacheActivity.class));// 页签点击后跳转缓存清理页面

		// 初始化一个标签
		TabSpec tab2 = tabHost.newTabSpec("Sdcard_Cache");
		tab2.setIndicator("sdcard清理");// 系统默认样式
		tab2.setContent(new Intent(this, SdcardCacheActivity.class));// 页签点击后跳转sdcard清理页面

		tabHost.addTab(tab1);
		tabHost.addTab(tab2);


activity_cache_tab.xml

	<?xml version="1.0" encoding="utf-8"?>
	<TabHost xmlns:android="http://schemas.android.com/apk/res/android"
	    android:id="@android:id/tabhost"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent" >
	
	    <LinearLayout
	        android:layout_width="match_parent"
	        android:layout_height="match_parent"
	        android:orientation="vertical" >
	
	        <FrameLayout
	            android:id="@android:id/tabcontent"
	            android:layout_width="match_parent"
	            android:layout_height="0dp"
	            android:layout_weight="1" >
	        </FrameLayout>
	
	        <TabWidget
	            android:id="@android:id/tabs"
	            android:layout_width="match_parent"
	            android:layout_height="wrap_content" >
	        </TabWidget>
	    </LinearLayout>
	
	</TabHost>





扫描缓存和清除缓存需要两个权限：

    <uses-permission android:name="android.permission.GET_PACKAGE_SIZE" />
    <uses-permission android:name="android.permission.CLEAR_APP_CACHE" />


需要借助三个远程服务：

都在 android.content.pm 下面

	IPackageDataObserver.aidl
	IPackageStatsObserver.aidl
	PackageStats.aidl


然后在利用反射


		mPM = getPackageManager();
		new Thread() {
			public void run() {
				List<PackageInfo> installedPackages = mPM
						.getInstalledPackages(0);
				pbProgress.setMax(installedPackages.size());
				int progress = 0;
				for (PackageInfo packageInfo : installedPackages) {
					String packageName = packageInfo.packageName;
					// 权限:android.permission.GET_PACKAGE_SIZE
					try {
						Method method = mPM.getClass().getMethod(
								"getPackageSizeInfo", String.class,
								IPackageStatsObserver.class);
						method.invoke(mPM, packageName, new MyObserver());
					} catch (Exception e) {
						e.printStackTrace();
					}

					String name = packageInfo.applicationInfo.loadLabel(mPM)
							.toString();

					progress++;
					pbProgress.setProgress(progress);
					Message msg = Message.obtain();
					msg.what = STATE_UPDATE_STATUS;
					msg.obj = name;
					mHandler.sendMessage(msg);

					SystemClock.sleep(100);
				}

				mHandler.sendEmptyMessage(STATE_SCAN_FINISH);
			};
		}.start();


	class MyObserver extends IPackageStatsObserver.Stub {

		// 此方法在子线程运行
		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			long cacheSize = pStats.cacheSize;

			if (cacheSize > 0) {// 有些手机会默认给每个app预留至少12KB,无法清掉, cacheSize >
								// 12*1024
				// 有缓存
				CacheInfo info = new CacheInfo();
				info.packageName = pStats.packageName;
				info.cacheSize = cacheSize;

				// 根据包名获取应用信息
				ApplicationInfo applicationInfo;
				try {
					applicationInfo = mPM.getApplicationInfo(info.packageName,
							0);
					info.name = applicationInfo.loadLabel(mPM).toString();
					info.icon = applicationInfo.loadIcon(mPM);

					Message msg = Message.obtain();
					msg.obj = info;
					msg.what = STATE_FIND_CACHE;
					mHandler.sendMessage(msg);

				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}

			}
		}
	}

清除单个缓存：

	// deleteApplicationCacheFiles, 只有系统应用才有此权限
	// 需要跳到系统页面清理缓存
	// Starting: Intent {
	// act=android.settings.APPLICATION_DETAILS_SETTINGS
	// dat=package:com.android.browser flg=0x10000000
	// cmp=com.android.settings/.applications.InstalledAppDetails
	// } from pid 200
	Intent intent = new Intent(
			"android.settings.APPLICATION_DETAILS_SETTINGS");
	intent.setData(Uri.parse("package:" + info.packageName));
	startActivity(intent);





清除所有缓存：


	/**
	 * 清理全部缓存 权限:android.permission.CLEAR_APP_CACHE
	 * 
	 * @param view
	 */
	public void clearCache(View view) {
		// 立即清理
		// freeStorageAndNotify 向系统索要足够大的空间, 那么系统就会删除所有缓存文件来凑空间,从而间接达到清理缓存的目的
		// LRU least recentlly used 最近最少使用
		try {
			Method method = mPM.getClass().getMethod("freeStorageAndNotify",
					long.class, IPackageDataObserver.class);
			method.invoke(mPM, Long.MAX_VALUE, new IPackageDataObserver.Stub() {

				@Override
				public void onRemoveCompleted(String packageName,
						boolean succeeded) throws RemoteException {
					System.out.println("succeeded:" + succeeded);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}



清除SDCard下的缓存：（就是依据数据库路径

	// 1. 读取sdcard缓存数据库,获取缓存目录
	// 2. 判断该目录是否在本地存在
	// 3. 如果存在,就删除该文件夹

	// File file = new File("");
	// file.exists();

	// 递归删除文件夹中所有文件
	// File[] listFiles = file.listFiles();
	// file.delete()











61. 自定义Application
62. 全局捕获异常

	### 03.自定义Application
	* Application是应用启动时会运行。
	* 要在清单文件里面说明
	* 作用：
		* 全局初始化
		* 全局变量或方法
	### 04.应用退出原理
	* Application作用
		* 退出应用
			* 方法1：system.exit(0);  暴力
			* 方法2：维护一个Activity列表
	### 05.全局捕获异常
	* Thread.setDefaultUncatchException(Handler...);
	* 停止当前进程
		* andoid.os.Process.KillProcess(andriod.os.Process.getMyPid());
	### 06.崩溃日志收集
	* err.printStactErr(new PrintWriter(""));
	* err.close();

代码：

	public class MyApplication extends Application {
		@Override
		public void onCreate() {
			super.onCreate();
			L.v("---应用一旦启动就会执行这里---");
			// 设置未捕获异常的处理器
			Thread.setDefaultUncaughtExceptionHandler(new MyHandler());
		}
	
		class MyHandler implements UncaughtExceptionHandler {
	
			// 一旦有未捕获的异常,就会回调此方法
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				System.out.println("发现一个未处理的异常, 但是被哥捕获了...");
				ex.printStackTrace();
				// 收集崩溃日志, 可以在后台上传给服务器,供开发人员分析
				try {
					PrintWriter err = new PrintWriter(
							Environment.getExternalStorageDirectory()
									+ "/err66.log");
					ex.printStackTrace(err);
					err.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
	
				// 停止当前进程
				android.os.Process.killProcess(android.os.Process.myPid());
			}
	
		}
	}


63. 广告
64. 混淆















