package com.ivy.mobilesafe.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivy.mobilesafe.R;
import com.ivy.mobilesafe.base.BaseActivity;
import com.ivy.mobilesafe.utils.MD5Utils;
import com.ivy.mobilesafe.utils.SharedPreUtils;
import com.ivy.mobilesafe.utils.ToastUtils;

public class HomeActivity extends BaseActivity {

	private GridView gvHome;

	private String[] mHomeNames = new String[] { "手机防盗", "通讯卫士", "软件管理",
			"进程管理", "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心" };

	private int[] mImageIds = new int[] { R.drawable.home_safe,
			R.drawable.home_callmsgsafe, R.drawable.home_apps,
			R.drawable.home_taskmanager, R.drawable.home_netmanager,
			R.drawable.home_trojan, R.drawable.home_sysoptimize,
			R.drawable.home_tools, R.drawable.home_settings };

	@Override
	public void initView() {
		setContentView(R.layout.activity_home);

		gvHome = (GridView) findViewById(R.id.gv_home);

	}

	@Override
	public void initLinstener() {

		gvHome.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					showSafeDialog();
					break;
				case 1:
					startActivity(new Intent(getApplicationContext(),
							BlackNumberActivity.class));
					break;
				case 2:

					break;
				case 3:

					break;
				case 4:

					break;
				case 5:

					break;
				case 6:

					break;
				case 7:
					startActivity(new Intent(getApplicationContext(),
							AToolsActivity.class));//高级工具
					break;
				case 8:
					startActivity(new Intent(getApplicationContext(),
							SettingActivity.class));
					break;

				}
			}
		});

	}

	/**
	 * 显示设置密码或输入密码对话框
	 */
	protected void showSafeDialog() {
		boolean setpwd = SharedPreUtils.getBoolean(this, "setpwd");
		if (setpwd) {
			// 显示设置密码
			showEnterPwdDialog();
		} else {
			// 显示输入密码
			showSetPwdDialog();
		}
	}

	/**
	 * 输入密码对话框
	 */
	private void showEnterPwdDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(this, R.layout.dialog_input_pwd, null);// 给dialog设定特定布局
		// dialog.setView(view);
		dialog.setView(view, 0, 0, 0, 0);// 去掉上下左右边距, 兼容2.x版本

		Button btnOK = (Button) view.findViewById(R.id.btn_ok);
		Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

		final EditText etPwd = (EditText) view.findViewById(R.id.et_pwd);

		btnOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String pwd = etPwd.getText().toString().trim();
				if (!TextUtils.isEmpty(pwd)) {
					String savePwd = SharedPreUtils.getString(
							getApplicationContext(), "password");
					if (MD5Utils.encode(pwd).equals(savePwd)) {
						// 密码正确
						dialog.dismiss();

						// 跳到手机防盗
						startActivity(new Intent(getApplicationContext(),
								LostAndFindActivity.class));
					} else {
						ToastUtils.show(getApplicationContext(), "密码错误!");
					}
				} else {
					ToastUtils.show(getApplicationContext(), "输入内容不能为空!");
				}
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	/**
	 * 设置密码对话框
	 */
	private void showSetPwdDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(this, R.layout.dialog_set_pwd, null);// 给dialog设定特定布局
		// dialog.setView(view);
		dialog.setView(view, 0, 0, 0, 0);// 去掉上下左右边距, 兼容2.x版本

		Button btnOK = (Button) view.findViewById(R.id.btn_ok);
		Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

		final EditText etPwd = (EditText) view.findViewById(R.id.et_pwd);
		final EditText etPwdConfirm = (EditText) view
				.findViewById(R.id.et_pwd_confirm);

		btnOK.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String pwd = etPwd.getText().toString().trim();
				String pwdConfirm = etPwdConfirm.getText().toString().trim();

				if (!TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(pwdConfirm)) {
					if (pwd.equals(pwdConfirm)) {
						//保存设置过的密码的标签
						SharedPreUtils.putBoolean(getApplicationContext(), "setpwd", true);
						
						// 保存密码
						SharedPreUtils.putString(getApplicationContext(),
								"password", MD5Utils.encode(pwd));
						dialog.dismiss();
						// 跳到手机防盗
						startActivity(new Intent(getApplicationContext(),
								LostAndFindActivity.class));
					} else {
						ToastUtils.show(getApplicationContext(), "两次密码不一致!");
					}
				} else {
					ToastUtils.show(getApplicationContext(), "输入内容不能为空!");
				}
			}
		});

		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();

	}

	@Override
	public void initData() {

		gvHome.setAdapter(new HomeAdapter());
	}

	@Override
	public void processClick(View v) {

	}

	class HomeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mHomeNames.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(),
					R.layout.list_item_home, null);

			TextView tvName = (TextView) view.findViewById(R.id.tv_name);
			ImageView ivIcon = (ImageView) view.findViewById(R.id.iv_icon);

			tvName.setText(mHomeNames[position]);
			ivIcon.setImageResource(mImageIds[position]);

			return view;
		}

	}

}
