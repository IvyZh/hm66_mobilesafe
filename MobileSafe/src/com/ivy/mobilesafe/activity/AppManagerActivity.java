package com.ivy.mobilesafe.activity;

import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ivy.mobilesafe.R;
import com.ivy.mobilesafe.base.BaseActivity;
import com.ivy.mobilesafe.domain.AppInfo;
import com.ivy.mobilesafe.utils.AppInfoUtils;
import com.ivy.mobilesafe.utils.ToastUtils;

/**
 * 软件管理
 * 
 * 手机apk安装流程: 1. 将apk拷贝到/data/app(用户) 2. 在/data/system中注册,
 * packages.list(系统用户应用,包名,项目路径), packages.xml(注册每个应用的权限)
 * 
 * 系统apk目录: /system/app(厂商内置), 不允许安装
 * 
 * 安装位置: android:installLocation="internalOnly"//只允许安装在手机内存(默认);preferExternal(
 * 优先sdcard);auto(优先手机内存,其次sdcard)
 * 
 * @author Kevin
 * 
 */
public class AppManagerActivity extends BaseActivity {
	private ArrayList<AppInfo> appList;
	private View loadingView;
	private ListView lvApp;
	private MyAdapter adapter;
	private ArrayList<AppInfo> systemAppList;
	private ArrayList<AppInfo> userAppList;
	private TextView tvHeader;
	private AppInfo mCurrentApp;

	@Override
	public void initView() {
		setContentView(R.layout.activity_app_manager);

		TextView tvSdcard = (TextView) findViewById(R.id.tv_sdcard_avail);
		TextView tvRom = (TextView) findViewById(R.id.tv_rom_avail);

		tvHeader = (TextView) findViewById(R.id.tv_header);
		loadingView = findViewById(R.id.ll_loading);
		lvApp = (ListView) findViewById(R.id.lv_list);

		tvSdcard.setText("SdCard可用："
				+ getStorage(Environment.getExternalStorageDirectory()
						.getAbsolutePath()));
		tvRom.setText("内部存储："
				+ getStorage(Environment.getDataDirectory().getAbsolutePath()));
	}

	/**
	 * 获取SdCard的容量
	 */
	private String getStorage(String path) {
		StatFs statFs = new StatFs(path);

		int blockCount = statFs.getBlockCount();
		int availableBlocks = statFs.getAvailableBlocks();
		int blockSize = statFs.getBlockSize();
		String allSize = Formatter.formatFileSize(this, blockSize * blockCount);
		String avaSize = Formatter.formatFileSize(this, blockSize
				* availableBlocks);

		return avaSize;
	}

	@Override
	public void initLinstener() {

		lvApp.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (adapter != null) {

					if (firstVisibleItem <= userAppList.size()) {
						tvHeader.setText("用户应用(" + userAppList.size() + ")");
					} else {
						tvHeader.setText("系统应用(" + systemAppList.size() + ")");
					}

					// AppInfo appInfo = adapter.getItem(firstVisibleItem);
					// if (appInfo != null) {
					// if (appInfo.isUser) {
					// tvHeader.setText("用户应用(" + userAppList.size() + ")");
					// } else {
					// tvHeader.setText("系统应用(" + systemAppList.size()
					// + ")");
					// }
					// }
				}
			}
		});

		// 单击条目，弹出操作popupwindow
		lvApp.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (adapter != null) {
					mCurrentApp = adapter.getItem(position);
					if (mCurrentApp != null) {
						showPopupWindow(view);
						// 记录当前点击条目的信息
					}
				}
			}
		});
	}

	PopupWindow popupWindow;

	protected void showPopupWindow(View view) {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
		} else {
			View contentView = View.inflate(this, R.layout.popup_item_appinfo,
					null);
			popupWindow = new PopupWindow(contentView,
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
			popupWindow.setBackgroundDrawable(new ColorDrawable(
					android.R.color.holo_blue_light));

			// 加入监听
			TextView tvUninstall = (TextView) contentView
					.findViewById(R.id.tv_uninstall);
			TextView tvLaunch = (TextView) contentView
					.findViewById(R.id.tv_launch);
			TextView tvShare = (TextView) contentView
					.findViewById(R.id.tv_share);
			tvUninstall.setOnClickListener(this);
			tvLaunch.setOnClickListener(this);
			tvShare.setOnClickListener(this);
			// 加入动画

			ScaleAnimation sa = new ScaleAnimation(0, 1, 0, 1,
					Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
					0);
			AlphaAnimation aa = new AlphaAnimation(0, 1);
			AnimationSet as = new AnimationSet(false);
			as.setDuration(500);
			as.addAnimation(sa);
			as.addAnimation(aa);

			popupWindow.showAsDropDown(view, 50, -view.getHeight());
			contentView.startAnimation(as);
		}

	}

	@Override
	public void initData() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				appList = AppInfoUtils.getInstallApp(AppManagerActivity.this);
				userAppList = new ArrayList<AppInfo>();
				systemAppList = new ArrayList<AppInfo>();

				// 区分系统应用和用户应用
				for (AppInfo appInfo : appList) {
					if (appInfo.isUser) {
						userAppList.add(appInfo);
					} else {
						systemAppList.add(appInfo);
					}
				}

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						loadingView.setVisibility(View.GONE);
						adapter = new MyAdapter();
						lvApp.setAdapter(adapter);
						tvHeader.setText("用户应用(" + userAppList.size() + ")");
					}
				});
			}
		}).start();

	}

	@Override
	public void processClick(View v) {
		popupWindow.dismiss();// 弹窗消失

		switch (v.getId()) {
		case R.id.tv_uninstall:
			System.out.println("卸载" + mCurrentApp.packageName);
			uninstall();
			break;
		case R.id.tv_launch:
			System.out.println("启动" + mCurrentApp.packageName);
			launch();
			break;
		case R.id.tv_share:
			System.out.println("分享" + mCurrentApp.packageName);
			 share();
			break;
		}
	}

	/**
	 * 分享应用
	 */
	private void share() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "分享一个APP，下载地址: https://play.google.com/store/apps/details?id="+mCurrentApp.packageName);
		startActivity(intent);
	}

	/**
	 * 启动应用
	 */
	private void launch() {
		if (mCurrentApp != null) {
			PackageManager pm = getPackageManager();
			Intent intent = pm.getLaunchIntentForPackage(mCurrentApp.packageName);
			if(intent!=null){
				startActivity(intent);
			}else{
				ToastUtils.show(this, "找不到启动界面");
			}
		}
	}

	/***
	 * 卸载应用
	 */
	private void uninstall() {
		if (mCurrentApp != null && mCurrentApp.isUser) {
			Intent intent = new Intent();
			// intent.setAction(Intent.ACTION_UNINSTALL_PACKAGE);//api 14
			intent.setAction(Intent.ACTION_DELETE);
			intent.setData(Uri.parse("package:" + mCurrentApp.packageName));
			startActivityForResult(intent, 0);
		} else {
			ToastUtils.show(this, "系统应用不能删除");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 更新应用列表

		System.out.println("更新应用列表");

		initData();
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return userAppList.size() + systemAppList.size() + 2;
		}

		@Override
		public AppInfo getItem(int position) {
			if (position == 0 || position == userAppList.size() + 1) {
				return null;
			}

			if (position < userAppList.size() + 1) {
				return userAppList.get(position - 1);
			} else {
				return systemAppList.get(position - 2 - userAppList.size());
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0 || position == userAppList.size() + 1) {
				return 0;
			} else {
				return 1;
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// L.v("---getView--" + getItem(position).name + ">"
			// + getItem(position).isUser);

			int type = getItemViewType(position);
			switch (type) {
			case 0:
				HeaderHolder hHolder = null;
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(),
							R.layout.list_item_header, null);
					hHolder = new HeaderHolder();
					hHolder.tvHeader = (TextView) convertView
							.findViewById(R.id.tv_header);
					convertView.setTag(hHolder);
				} else {
					hHolder = (HeaderHolder) convertView.getTag();
				}

				if (position == 0) {
					hHolder.tvHeader
							.setText("用户应用(" + userAppList.size() + ")");
				} else {
					hHolder.tvHeader.setText("系统应用(" + systemAppList.size()
							+ ")");
				}
				break;
			case 1:
				ViewHolder holder = null;
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(),
							R.layout.list_item_appinfo, null);

					holder = new ViewHolder();
					holder.tvName = (TextView) convertView
							.findViewById(R.id.tv_name);
					holder.tvLocation = (TextView) convertView
							.findViewById(R.id.tv_location);
					holder.ivIcon = (ImageView) convertView
							.findViewById(R.id.iv_icon);

					convertView.setTag(holder);
				} else {
					// view = convertView;
					holder = (ViewHolder) convertView.getTag();
				}

				AppInfo info = getItem(position);
				holder.tvName.setText(info.name);
				holder.ivIcon.setImageDrawable(info.logo);

				if (info.isRom) {
					holder.tvLocation.setText("手机内存");
				} else {
					holder.tvLocation.setText("外置存储卡");
				}

				break;
			}
			return convertView;
		}

	}

	static class ViewHolder {
		public TextView tvName;
		public TextView tvLocation;
		public ImageView ivIcon;
	}

	static class HeaderHolder {
		public TextView tvHeader;
	}
}
