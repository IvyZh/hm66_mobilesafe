package com.ivy.mobilesafe.activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivy.mobilesafe.R;
import com.ivy.mobilesafe.base.BaseActivity;
import com.ivy.mobilesafe.utils.ToastUtils;

public class EnterPwdActivity extends BaseActivity {
	private TextView tvName;
	private ImageView ivIcon;
	private EditText etPwd;
	private Button btnOK;
	private String packageName;

	@Override
	public void initView() {
		setContentView(R.layout.activity_enter_pwd);

		tvName = (TextView) findViewById(R.id.tv_name);
		ivIcon = (ImageView) findViewById(R.id.iv_icon);
		etPwd = (EditText) findViewById(R.id.et_pwd);
		btnOK = (Button) findViewById(R.id.btn_ok);

	}

	@Override
	public void initLinstener() {
		btnOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String pwd = etPwd.getText().toString().trim();
				if (!TextUtils.isEmpty(pwd)) {
					if (pwd.equals("123")) {
						// 通知看门狗,跳过当前包名的验证
						Intent intent = new Intent();
						intent.setAction(getPackageName() + ".SKIP");
						intent.putExtra("package", packageName);// 传递包名
						sendBroadcast(intent);

						finish();
					} else {
						ToastUtils.show(getApplicationContext(),
								"密码错误,密码是123哦!");
					}
				} else {
					ToastUtils.show(getApplicationContext(), "输入内容不能为空!");
				}
			}
		});

	}

	@Override
	public void initData() {
		packageName = getIntent().getStringExtra("package");
		if (packageName != null) {
			PackageManager pm = getPackageManager();
			ApplicationInfo info;
			try {
				info = pm.getApplicationInfo(packageName, 0);
				Drawable drawable = info.loadIcon(pm);
				String name = info.loadLabel(pm).toString();

				tvName.setText(name);
				ivIcon.setImageDrawable(drawable);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void processClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBackPressed() {
		// 跳到桌面
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
		finish();
	}
}
