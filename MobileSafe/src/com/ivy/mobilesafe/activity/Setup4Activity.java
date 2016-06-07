package com.ivy.mobilesafe.activity;

import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.ivy.mobilesafe.R;
import com.ivy.mobilesafe.base.BaseSetupActivity;
import com.ivy.mobilesafe.utils.SharedPreUtils;

public class Setup4Activity extends BaseSetupActivity {

	private CheckBox cbProtect;

	@Override
	public void goNextActivity(View v) {
		
		SharedPreUtils.putBoolean(this, "isConfig", true);
		Intent intent = new Intent(this,LostAndFindActivity.class);
		startActivity(intent);
		finish();
		
		
		overridePendingTransition(R.anim.anim_next_in, R.anim.anim_next_out);
	}

	@Override
	public void goPreActivity(View v) {
		
		Intent intent = new Intent(this,Setup3Activity.class);
		startActivity(intent);
		finish();

		overridePendingTransition(R.anim.anim_pre_in, R.anim.anim_pre_out);
	}

	@Override
	public void initView() {
		setContentView(R.layout.activity_setup4);
		
		cbProtect = (CheckBox) findViewById(R.id.cb_protect);

		boolean protect = SharedPreUtils.getBoolean(this,"protect");
		cbProtect.setChecked(protect);
		if (protect) {
			cbProtect.setText("防盗保护已经开启");
		} else {
			cbProtect.setText("您没有开启防盗保护");
		}

		cbProtect.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					cbProtect.setText("防盗保护已经开启");
					SharedPreUtils.putBoolean(getApplicationContext(),"protect", true);
				} else {
					cbProtect.setText("您没有开启防盗保护");
					SharedPreUtils.putBoolean(getApplicationContext(),"protect", false);
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
