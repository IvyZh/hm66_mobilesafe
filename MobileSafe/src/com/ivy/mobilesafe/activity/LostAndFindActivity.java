package com.ivy.mobilesafe.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.ivy.mobilesafe.R;
import com.ivy.mobilesafe.base.BaseActivity;
import com.ivy.mobilesafe.utils.SharedPreUtils;

public class LostAndFindActivity extends BaseActivity {

	@Override
	public void initView() {
		boolean isConfig = SharedPreUtils.getBoolean(getApplicationContext(), "isConfig");//是否已经设置过手机防盗
		if(isConfig){
			//展示功能界面
			setContentView(R.layout.activity_lost_and_find);
			boolean protect = SharedPreUtils.getBoolean(getApplicationContext(), "protect");
			ImageView ivLock = (ImageView) findViewById(R.id.iv_lock);
			if(protect){
				ivLock.setImageResource(R.drawable.lock);
			}else{
				ivLock.setImageResource(R.drawable.unlock);
			}
		}else{
			//进入引导页面
			startActivity(new Intent(this, Setup1Activity.class));
			finish();
		}
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

	}
	
	public void reSetup(View v){
		startActivity(new Intent(this, Setup1Activity.class));
		finish();
	}
	

}
