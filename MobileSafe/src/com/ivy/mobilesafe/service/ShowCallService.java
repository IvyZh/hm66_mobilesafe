package com.ivy.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ivy.mobilesafe.R;
import com.ivy.mobilesafe.db.dao.AddressDao;
import com.ivy.mobilesafe.log.L;
import com.ivy.mobilesafe.utils.SharedPreUtils;

/**
 * 
 * 来电显示的服务
 */
public class ShowCallService extends Service {

	private TelephonyManager tm;
	private MyPhoneStateListener listener;
	private OutCallBroadCast outCallBroadCast;
	private int mScreenHeight;
	private int mScreenWidth;
	private WindowManager mWM;
	private View mView;
	protected int startX;
	protected int startY;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		L.v("ShowCallService onCreate");
		super.onCreate();

		// 监听来电
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneStateListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

		// 监听去电

		outCallBroadCast = new OutCallBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(outCallBroadCast, filter);

	}

	class OutCallBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			String data = getResultData();
			String location = AddressDao.getLocationByNumber(context, data);

			L.v("有电话拨出去了" + data + "," + location);

			showToast(location);
		}
	}

	class MyPhoneStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				// 空闲

				removeView();

				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				// 摘机
				removeView();
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				String location = AddressDao.getLocationByNumber(
						getApplicationContext(), incomingNumber);
				L.v("电话来了--" + incomingNumber + "," + location);

				showToast(location);
				break;

			}
		}
	}

	@Override
	public void onDestroy() {
		L.v("ShowCallService onDestroy");
		super.onDestroy();

		// 取消来电监听
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;

		// 取消去电监听
		unregisterReceiver(outCallBroadCast);
		outCallBroadCast = null;

	}

	public void removeView() {
		if (mView != null) {
			mWM.removeView(mView);
			mView = null;
		}
	}

	/**
	 * 显示来电
	 * 
	 * @param location
	 *            <uses-permission
	 *            android:name="android.permission.PROCESS_OUTGOING_CALLS"/>
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
		final TextView tvAddress = (TextView) mView
				.findViewById(R.id.tv_address);
		tvAddress.setText(location);
		// private String[] mItems = new String[] { "半透明", "活力橙", "卫士蓝", "金属灰",
		// "苹果绿" };
		int style = SharedPreUtils.getInt(getApplicationContext(), "style");

		int pos_x = SharedPreUtils.getInt(getApplicationContext(), "pos_x");
		int pos_y = SharedPreUtils.getInt(getApplicationContext(), "pos_y");

		// LinearLayout.LayoutParams layoutParams =
		// (android.widget.LinearLayout.LayoutParams)
		// tvAddress.getLayoutParams();
		params.x = pos_x;
		params.y = pos_y;

		switch (style) {
		case 0:
			tvAddress.setBackgroundResource(R.drawable.call_locate_white);
			break;
		case 1:
			tvAddress.setBackgroundResource(R.drawable.call_locate_orange);
			break;
		case 2:
			tvAddress.setBackgroundResource(R.drawable.call_locate_blue);
			break;
		case 3:
			tvAddress.setBackgroundResource(R.drawable.call_locate_gray);
			break;
		case 4:
			tvAddress.setBackgroundResource(R.drawable.call_locate_green);
			break;

		}

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
		mWM.addView(mView, params);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		L.v("ShowCallService onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

}
