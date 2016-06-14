package com.ivy.mobilesafe.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.ivy.mobilesafe.R;
import com.ivy.mobilesafe.base.BaseActivity;
import com.ivy.mobilesafe.log.L;
import com.ivy.mobilesafe.service.AppLockService;
import com.ivy.mobilesafe.service.BlackNumberService;
import com.ivy.mobilesafe.service.ShowCallService;
import com.ivy.mobilesafe.ui.SettingItemClickView;
import com.ivy.mobilesafe.ui.SettingItemView;
import com.ivy.mobilesafe.utils.ServiceStatusUtils;
import com.ivy.mobilesafe.utils.SharedPreUtils;

public class SettingActivity extends BaseActivity {

	private SettingItemView sivUpdate;
	private SettingItemView sivAddress;
	private SettingItemClickView sicStyle;
	private String[] mItems = new String[] { "半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿" };
	private SettingItemClickView sicLocation;
	private SettingItemView sivBlackNumber;
	private SettingItemView sivAppLock;

	@Override
	public void initView() {
		setContentView(R.layout.activity_setting);
		
		int a =0;
		System.out.println(1/a);

		sivUpdate = (SettingItemView) findViewById(R.id.siv_update);
		boolean autoupdate = SharedPreUtils.getBoolean(this, "autoupdate");
		sivUpdate.setChecked(autoupdate);

		sivAddress = (SettingItemView) findViewById(R.id.siv_address);
		sivBlackNumber = (SettingItemView) findViewById(R.id.siv_black_number);

		sicStyle = (SettingItemClickView) findViewById(R.id.sic_style);
		sicStyle.setTitle("归属地提示框风格");
		sicStyle.setDesc("半透明");

		sicLocation = (SettingItemClickView) findViewById(R.id.sic_location);
		sicLocation.setTitle("归属地提示框位置");
		sicLocation.setDesc("设置归属地提示框位置");
		
		
		sivAppLock = (SettingItemView) findViewById(R.id.siv_app_lock);

	}

	@Override
	protected void onResume() {
		super.onResume();
		boolean showcall = ServiceStatusUtils.isRunning(this,
				ShowCallService.class.getName());
		sivAddress.setChecked(showcall);

		boolean blackNumber = ServiceStatusUtils.isRunning(this,
				BlackNumberService.class.getName());
		sivBlackNumber.setChecked(blackNumber);

		int style = SharedPreUtils.getInt(this, "style");
		sicStyle.setDesc(mItems[style]);
		
		
		boolean appLock = ServiceStatusUtils.isRunning(this, AppLockService.class.getName());
		sivAppLock.setChecked(appLock);
	}

	@Override
	public void initLinstener() {
		// 自动更新
		sivUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean checked = sivUpdate.getChecked();
				sivUpdate.setChecked(!checked);
				SharedPreUtils.putBoolean(SettingActivity.this, "autoupdate",
						!checked);
			}
		});

		// 来电显示
		sivAddress.setOnClickListener(new OnClickListener() {

			private Intent showCallService;

			@Override
			public void onClick(View v) {
				boolean checked = sivAddress.getChecked();
				sivAddress.setChecked(!checked);
				if (sivAddress.getChecked()) {
					L.v("---开启来电显示--");
					showCallService = new Intent(SettingActivity.this,
							ShowCallService.class);
					startService(showCallService);

				} else {
					L.v("--close 来电显示--");
					if (showCallService != null) {
						stopService(showCallService);
					}
				}
			}
		});

		// 提示框风格

		sicStyle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showChooseDialog();
			}
		});

		// 归属地提示框位置
		sicLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 跳到位置修改的页面
				startActivity(new Intent(getApplicationContext(),
						DragViewActivity.class));
			}
		});

		// 黑名单拦截
		sivBlackNumber.setOnClickListener(new OnClickListener() {

			private Intent blackNumberService;

			@Override
			public void onClick(View v) {
				boolean checked = sivBlackNumber.getChecked();
				sivBlackNumber.setChecked(!checked);
				if (sivBlackNumber.getChecked()) {
					L.v("---开启黑名单拦截--");
					blackNumberService = new Intent(SettingActivity.this,
							BlackNumberService.class);
					startService(blackNumberService);

				} else {
					L.v("--close 黑名单拦截--");
					if (blackNumberService != null) {
						stopService(blackNumberService);
					}
				}
			}
		});
		
		// 程序锁
		sivAppLock.setOnClickListener(new OnClickListener() {

			private Intent appLockService;

			@Override
			public void onClick(View v) {
				boolean checked = sivAppLock.getChecked();
				sivAppLock.setChecked(!checked);
				if (sivAppLock.getChecked()) {
					appLockService = new Intent(SettingActivity.this,
							AppLockService.class);
					startService(appLockService);

				} else {
					if (appLockService != null) {
						stopService(appLockService);
					}
				}
			}
		});
	}

	/**
	 * 弹出选择风格单项对话框
	 */
	protected void showChooseDialog() {
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("选择提示框风格");
		builder.setSingleChoiceItems(mItems, 0,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharedPreUtils.putInt(SettingActivity.this, "style",
								which);
						dialog.dismiss();
					}
				});

		builder.show();
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void processClick(View v) {
		// TODO Auto-generated method stub

	}

}
