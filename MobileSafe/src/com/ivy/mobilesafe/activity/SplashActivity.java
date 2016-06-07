package com.ivy.mobilesafe.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.ivy.mobilesafe.R;
import com.ivy.mobilesafe.base.BaseActivity;
import com.ivy.mobilesafe.constant.Constant;
import com.ivy.mobilesafe.log.L;
import com.ivy.mobilesafe.utils.SharedPreUtils;
import com.ivy.mobilesafe.utils.StreamUtils;
import com.ivy.mobilesafe.utils.ToastUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class SplashActivity extends BaseActivity {

	private String desc;
	private String updateUrl;
	private int newVersionCode;
	private String newVersionName;

	protected static final int ENTER_HOME = 0;

	protected static final int URL_ERROR = 1;

	protected static final int PROTOCOL_ERROR = 2;

	protected static final int IO_ERROR = 3;

	protected static final int JSON_ERROR = 4;

	protected static final int UPDATE_APP = 5;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case ENTER_HOME:
				ToastUtils.show(getApplicationContext(), "已经是最新版本，进入主界面");
				enterHome();
				break;
			case URL_ERROR:
				ToastUtils.show(getApplicationContext(), "更新地址有误");
				enterHome();
				break;
			case PROTOCOL_ERROR:
				ToastUtils.show(getApplicationContext(), "服务器异常protocol");
				enterHome();
				break;
			case IO_ERROR:
				ToastUtils.show(getApplicationContext(), "服务器异常");
				enterHome();
				break;
			case JSON_ERROR:
				ToastUtils.show(getApplicationContext(), "数据解析失败");
				enterHome();
				break;
			case UPDATE_APP:
				ToastUtils.show(getApplicationContext(), "更新APP");
				showUpdateDialog();
				break;
			}
		};
	};

	private TextView tvVersion;

	@Override
	public void initView() {
		setContentView(R.layout.activity_splash);
		tvVersion = (TextView) findViewById(R.id.textView1);
	}

	@Override
	public void initLinstener() {

	}

	@Override
	public void initData() {
		String versionName = getVersionName();
		tvVersion.setText("版本号：" + versionName);

		// 判断是否需要自动更新
		boolean autoupdate = SharedPreUtils.getBoolean(this, "autoupdate");
		if (autoupdate) {
			// 版本检查
			checkVersion();
		} else {
			enterHome();
		}

		// 拷贝数据库
		copyDB("address.db");
	}

	/**
	 * 数据库拷贝
	 * 
	 * @param string
	 */
	private void copyDB(final String name) {
		final File file = new File(getFilesDir(), name);
		
		L.v("copy db "+file.length());
		if(!file.exists()){
			new Thread(new Runnable() {

				private FileOutputStream fos;
				private InputStream is;

				@Override
				public void run() {
					AssetManager assets = getAssets();
					try {
						is = assets.open(name);
						fos = new FileOutputStream(file);
						byte[] b = new byte[1024*1024];
						int len = 0;

						while ((len = is.read(b)) != -1) {
							L.v("copy---"+len);
							fos.write(b, 0, len);
						}
						
						L.v("数据库 "+ name+" 拷贝成功");

					} catch (IOException e) {
						e.printStackTrace();
					} finally {

						try {
							is.close();
							fos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
		}else{
			L.v("数据库 "+name+" 已经存在，无需拷贝");
		}
	}

	private void checkVersion() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = Message.obtain();
				try {
					URL url = new URL(Constant.REQ_APK_UPDATE);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setRequestMethod("GET");
					conn.setReadTimeout(5000);
					conn.setConnectTimeout(5000);
					conn.connect();

					int code = conn.getResponseCode();
					if (code == 200) {
						InputStream is = conn.getInputStream();
						String text = StreamUtils.InputStream2String(is);
						L.v(text);
						JSONObject object = new JSONObject(text);
						desc = object.getString("desc");
						updateUrl = object.getString("updateUrl");
						newVersionCode = object.getInt("versionCode");
						newVersionName = object.getString("versionName");

						int localVersionCode = getVersionCode();
						if (newVersionCode > localVersionCode) {
							msg.what = UPDATE_APP;
						} else {
							msg.what = ENTER_HOME;
						}
					}
				} catch (MalformedURLException e) {
					msg.what = URL_ERROR;
					e.printStackTrace();
				} catch (ProtocolException e) {
					msg.what = PROTOCOL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					msg.what = IO_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					msg.what = JSON_ERROR;
					e.printStackTrace();
				} finally {
					handler.sendMessage(msg);
				}
			}
		}).start();

	}

	/**
	 * 根据URL下载APK
	 * 
	 * @param updateUrl
	 */
	protected void downLoadApk(final String updateUrl) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				L.v("开始下载：" + updateUrl);
				String state = Environment.getExternalStorageState();
				if (state.equals(Environment.MEDIA_MOUNTED)) {

					// 模拟器的SdCard没法写入，因此就先在在cache里面,但是又会导致安装apk的时候，无法读取dada/data目录
					// File file = new File(getCacheDir(), "mobilesafe.apk");
					File file = new File(
							Environment.getExternalStorageDirectory(),
							"mobilesafe.apk");

					HttpUtils httpUtils = new HttpUtils();
					httpUtils.download(updateUrl, file.getAbsolutePath(),
							new RequestCallBack<File>() {

								@Override
								public void onSuccess(ResponseInfo<File> arg0) {
									ToastUtils.show(getApplicationContext(),
											"下载成功，准备更新");
									installApp(arg0.result);
								}

								@Override
								public void onFailure(HttpException arg0,
										String arg1) {
									ToastUtils.show(getApplicationContext(),
											"下载失败");
								}

								@Override
								public void onLoading(long total, long current,
										boolean isUploading) {
									super.onLoading(total, current, isUploading);
									L.v("loading--" + current + " / " + total);
								}
							});
				} else {
					ToastUtils.show(getApplicationContext(), "SdCard不可用");
				}
			}
		}).start();

	}

	/**
	 * 安装APK
	 * 
	 * @param result
	 */
	private void installApp(File result) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(result),
				"application/vnd.android.package-archive");
		startActivityForResult(intent, 0);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		enterHome();
	}

	/**
	 * 显示更新APP的Dialog
	 */
	protected void showUpdateDialog() {
		Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(desc);
		builder.setTitle("发现新版本：" + newVersionCode);

		builder.setPositiveButton("立即更新", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				downLoadApk(updateUrl);
			}
		});

		builder.setNegativeButton("下次再说", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				enterHome();
			}
		});

		AlertDialog dialog = builder.show();

		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				enterHome();
			}
		});

	}

	/**
	 * 进入Home主界面
	 */
	private void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);

		View splashRoot = findViewById(R.id.splash_root);
		AlphaAnimation aa = new AlphaAnimation(1.0f, 0.0f);
		aa.setDuration(200);
		splashRoot.startAnimation(aa);

		finish();
	}

	/**
	 * 版本号
	 * 
	 * @return
	 */
	private int getVersionCode() {
		PackageManager pm = getPackageManager();
		PackageInfo info;
		try {
			info = pm.getPackageInfo(getPackageName(), 0);
			int versionCode = info.versionCode;
			return versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 版本名称
	 * 
	 * @return
	 */
	private String getVersionName() {
		PackageManager pm = getPackageManager();
		PackageInfo info;
		try {
			info = pm.getPackageInfo(getPackageName(), 0);
			String versionName = info.versionName;
			return versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void processClick(View v) {

	}

}
