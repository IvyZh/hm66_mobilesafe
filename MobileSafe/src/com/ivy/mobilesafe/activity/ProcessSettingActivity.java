package com.ivy.mobilesafe.activity;

import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.ivy.mobilesafe.R;
import com.ivy.mobilesafe.base.BaseActivity;
import com.ivy.mobilesafe.service.AutoKillService;
import com.ivy.mobilesafe.utils.ServiceStatusUtils;

public class ProcessSettingActivity extends BaseActivity {
	private CheckBox cbShowSystem;
	private CheckBox cbAutoKill;

	@Override
	public void initView() {
		setContentView(R.layout.activity_process_setting);
		cbShowSystem = (CheckBox) findViewById(R.id.cb_show_system);
		cbAutoKill = (CheckBox) findViewById(R.id.cb_auto_kill);
		//
		// boolean showSystem = PrefUtils.getBoolean("show_system", true, this);
		// if (showSystem) {
		// cbShowSystem.setChecked(true);
		// cbShowSystem.setText("显示系统进程");
		// } else {
		// cbShowSystem.setChecked(false);
		// cbShowSystem.setText("不显示系统进程");
		// }
		//
		// cbShowSystem.setOnCheckedChangeListener(new OnCheckedChangeListener()
		// {
		//
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView,
		// boolean isChecked) {
		// if (isChecked) {
		// cbShowSystem.setText("显示系统进程");
		// PrefUtils.putBoolean("show_system", true,
		// getApplicationContext());
		// } else {
		// cbShowSystem.setText("不显示系统进程");
		// PrefUtils.putBoolean("show_system", false,
		// getApplicationContext());
		// }
		// }
		// });
		//
		boolean serviceRunning = ServiceStatusUtils.isRunning(this,
				AutoKillService.class.getName());
		if (serviceRunning) {
			cbAutoKill.setChecked(true);
			cbAutoKill.setText("锁屏清理已开启");
		} else {
			cbAutoKill.setChecked(false);
			cbAutoKill.setText("锁屏清理已关闭");
		}
		//
		cbAutoKill.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Intent service = new Intent(getApplicationContext(),
						AutoKillService.class);
				if (isChecked) {
					cbAutoKill.setText("锁屏清理已开启");
					startService(service);
				} else {
					cbAutoKill.setText("锁屏清理已关闭");
					stopService(service);
				}
			}
		});

	}

	@Override
	public void initLinstener() {
		// TODO Auto-generated method stub

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
