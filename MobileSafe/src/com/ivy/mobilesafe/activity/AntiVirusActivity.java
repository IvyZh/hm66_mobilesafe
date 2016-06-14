package com.ivy.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ivy.mobilesafe.R;
import com.ivy.mobilesafe.base.BaseActivity;
import com.ivy.mobilesafe.db.dao.AntiVirusDao;
import com.ivy.mobilesafe.log.L;
import com.ivy.mobilesafe.utils.MD5Utils;
import com.ivy.mobilesafe.utils.ToastUtils;

public class AntiVirusActivity extends BaseActivity {

	private static final int STATE_UPDATE_STATUS = 1;// 更新扫描状态
	private static final int STATE_SCAN_FINISH = 2;// 扫描结束

	private ImageView ivScanning;
	private TextView tvStatus;
	private ProgressBar pbProgress;
	private LinearLayout llContainer;

	// 病毒集合
	private ArrayList<ScanInfo> mVirusList = new ArrayList<AntiVirusActivity.ScanInfo>();

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case STATE_UPDATE_STATUS:
				ScanInfo info = (ScanInfo) msg.obj;
				tvStatus.setText("正在扫描:" + info.name);

				// 动态给容器llContainer添加TextView
				TextView view = new TextView(getApplicationContext());

				// 判断是否是病毒
				if (info.isVirus) {
					view.setText("发现病毒:" + info.name);
					view.setTextColor(Color.RED);
				} else {
					view.setText("扫描安全:" + info.name);
					view.setTextColor(Color.BLACK);
				}

				// llContainer.addView(view);
				llContainer.addView(view, 0);// 将view添加在第一个位置
				break;
			case STATE_SCAN_FINISH:
				tvStatus.setText("扫描完毕");
				ivScanning.clearAnimation();// 停止当前动画

				if (!mVirusList.isEmpty()) {
					 showAlertDialog();
					ToastUtils.show(getApplicationContext(), "您的手机有病毒!");
				} else {
					ToastUtils.show(getApplicationContext(), "您的手机很安全,请放心使用!");
				}

				break;

			default:
				break;
			}
		};
	};
	
	/**
	 * 发现病毒的警告弹窗
	 */
	protected void showAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("严重警告!");
		builder.setMessage("发现" + mVirusList.size() + "个病毒, 建议立即处理!!!");
		builder.setCancelable(false);
		builder.setPositiveButton("立即处理",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 卸载病毒
						for (ScanInfo info : mVirusList) {
							Intent intent = new Intent(Intent.ACTION_DELETE);
							intent.setData(Uri.parse("package:"
									+ info.packageName));
							startActivity(intent);
						}
					}
				});

		builder.setNegativeButton("以后再说", null);
		builder.show();
	}

	@Override
	public void initView() {
		setContentView(R.layout.activity_anti_virus);

		ivScanning = (ImageView) findViewById(R.id.iv_scanning);
		tvStatus = (TextView) findViewById(R.id.tv_status);
		pbProgress = (ProgressBar) findViewById(R.id.pb_progress);
		llContainer = (LinearLayout) findViewById(R.id.ll_container);

		RotateAnimation anim = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		anim.setDuration(2000);
		anim.setInterpolator(new LinearInterpolator());// 匀速运动
		anim.setRepeatCount(Animation.INFINITE);// 无限循环
		ivScanning.startAnimation(anim);

	}

	@Override
	public void initLinstener() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initData() {

		new Thread(new Runnable() {

			@Override
			public void run() {

				int progress = 0;
				PackageManager pm = getPackageManager();
				List<PackageInfo> installedPackages = pm
						.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
				pbProgress.setMax(installedPackages.size());

				for (PackageInfo packageInfo : installedPackages) {
					ScanInfo scanInfo = new ScanInfo();
					String packageName = packageInfo.packageName;
					String name = packageInfo.applicationInfo.loadLabel(pm)
							.toString();

					String sourceDir = packageInfo.applicationInfo.sourceDir;

					String md5 = MD5Utils.encodeFile(sourceDir);

					scanInfo.name = name;
					scanInfo.packageName = packageName;
					if (AntiVirusDao.isVirus(md5,getApplicationContext())) {
						scanInfo.isVirus = true;
						mVirusList.add(scanInfo);
					} else {
						scanInfo.isVirus = false;
					}
					L.v(packageName + "--" + md5);
					progress++;
					pbProgress.setProgress(progress);

					// 更新扫描状态
					Message msg = Message.obtain();
					msg.what = STATE_UPDATE_STATUS;
					msg.obj = scanInfo;
					mHandler.sendMessage(msg);

					SystemClock.sleep(50 + new Random().nextInt(50));// 休息50-100随机时间

				}

				Message msg = Message.obtain();
				msg.what = STATE_SCAN_FINISH;
				mHandler.sendMessage(msg);

			}
		}).start();
	}

	@Override
	public void processClick(View v) {
		// TODO Auto-generated method stub

	}

	class ScanInfo {
		public boolean isVirus;
		public String name;
		public String packageName;
	}

}
