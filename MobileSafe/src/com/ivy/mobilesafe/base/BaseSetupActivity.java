package com.ivy.mobilesafe.base;

import android.view.View;

public abstract class BaseSetupActivity extends BaseActivity {

	public void next(View v) {
		goNextActivity(v);
	}

	public void previous(View v) {
		goPreActivity(v);
	}

	/**
	 * 下一个界面
	 * 
	 * @param v
	 */
	public abstract void goNextActivity(View v);

	public abstract void goPreActivity(View v);
}
