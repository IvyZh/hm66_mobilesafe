package com.ivy.mobilesafe.activity;

import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ivy.mobilesafe.R;
import com.ivy.mobilesafe.base.BaseActivity;
import com.ivy.mobilesafe.db.dao.AddressDao;
import com.ivy.mobilesafe.utils.ToastUtils;

public class AddressQueryActivity extends BaseActivity {

	private EditText etNumber;
	private Button btnStart;
	private TextView tvResult;

	@Override
	public void initView() {
		setContentView(R.layout.activity_address_query);
		etNumber = (EditText) findViewById(R.id.et_number);
		btnStart = (Button) findViewById(R.id.btn_start);
		tvResult = (TextView) findViewById(R.id.tv_result);
	}

	@Override
	public void initLinstener() {
		btnStart.setOnClickListener(this);
		
		etNumber.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				String number = s.toString();
				if(!TextUtils.isEmpty(number)){
					String location = AddressDao.getLocationByNumber(AddressQueryActivity.this, number);
					tvResult.setText(location);
				}
			}
		});

	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void processClick(View v) {
		switch (v.getId()) {
		case R.id.btn_start:
			String number = etNumber.getText().toString().trim();
			if(!TextUtils.isEmpty(number)){
				String location = AddressDao.getLocationByNumber(this, number);
				tvResult.setText(location);
			}else{
				// 震动+抖动效果
				ToastUtils.show(this, "查询号码不能为空");
				
				Animation animation = AnimationUtils.loadAnimation(this, R.anim.shake);
				
				etNumber.startAnimation(animation);
				
				vibrator();
				
			}
			
			break;

		}

	}

	/**
	 * 震动
	 */
	private void vibrator() {
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(2000);
	}

	
	
	
	
	
	
	
	
	
	
	
}
