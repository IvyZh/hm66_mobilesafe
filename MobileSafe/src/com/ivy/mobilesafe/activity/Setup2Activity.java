package com.ivy.mobilesafe.activity;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;

import com.ivy.mobilesafe.R;
import com.ivy.mobilesafe.base.BaseActivity;
import com.ivy.mobilesafe.base.BaseSetupActivity;
import com.ivy.mobilesafe.ui.SettingItemView;
import com.ivy.mobilesafe.utils.SharedPreUtils;
import com.ivy.mobilesafe.utils.ToastUtils;

public class Setup2Activity extends BaseSetupActivity {

	private SettingItemView siv;

	@Override
	public void initView() {
		setContentView(R.layout.activity_setup2);
		
		siv = (SettingItemView) findViewById(R.id.siv_bind);
		
		boolean bind = SharedPreUtils.getBoolean(this, "bindsim");
		
		siv.setChecked(bind);
	}

	@Override
	public void initLinstener() {
		siv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean checked = siv.getChecked();
				siv.setChecked(!checked);
				SharedPreUtils.putBoolean(Setup2Activity.this, "bindsim", !checked);
			}
		});

	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void processClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void goNextActivity(View v) {
		//点击下一步的时候要判断cb是否勾选
		if(siv.getChecked()){
			Intent intent = new Intent(this,Setup3Activity.class);
			startActivity(intent);
			finish();
		}else{
			ToastUtils.show(this, "必须绑定sim卡");
		}
		overridePendingTransition(R.anim.anim_next_in, R.anim.anim_next_out);
		
		saveSimInfo();
		
	}

	private void saveSimInfo() {
		if( siv.getChecked()){
			// 如果sim卡绑定了，就要获取信息存在sp里面
			TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			String line1Number = telephonyManager.getLine1Number();//电话号码
			String simSerialNumber = telephonyManager.getSimSerialNumber();//sim卡序列号
			SharedPreUtils.putString(getApplicationContext(), "simSerialNumber", simSerialNumber);
		}else{
			// 如果sim卡没有绑定，就要获移除存在sp里面的信息
			SharedPreUtils.remove(getApplicationContext(), "simSerialNumber");
		}
	}

	@Override
	public void goPreActivity(View v) {
		Intent intent = new Intent(this,Setup1Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.anim_pre_in, R.anim.anim_pre_out);
		saveSimInfo();
	}

}
