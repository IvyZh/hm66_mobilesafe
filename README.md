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