package com.ivy.mobilesafe.activity;

import android.content.Intent;
import android.view.View;

import com.ivy.mobilesafe.R;
import com.ivy.mobilesafe.base.BaseSetupActivity;


public class Setup1Activity extends BaseSetupActivity {

	@Override
	public void initView() {
		setContentView(R.layout.activity_setup1);
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

	@Override
	public void goNextActivity(View v) {
		Intent intent = new Intent(this,Setup2Activity.class);
		startActivity(intent);
		finish();
		
		overridePendingTransition(R.anim.anim_next_in, R.anim.anim_next_out);
	}

	@Override
	public void goPreActivity(View v) {
		
	}

}
