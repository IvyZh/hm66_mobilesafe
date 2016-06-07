package com.ivy.mobilesafe.activity;

import android.content.Intent;
import android.view.View;

import com.ivy.mobilesafe.R;
import com.ivy.mobilesafe.base.BaseActivity;

public class AToolsActivity extends BaseActivity {

	@Override
	public void initView() {
		setContentView(R.layout.activity_atools);
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
	
	// 归属地查询
	public void addressQuery(View v){
		startActivity(new Intent(this, AddressQueryActivity.class));
	}

}
