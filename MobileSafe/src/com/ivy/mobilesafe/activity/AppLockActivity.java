package com.ivy.mobilesafe.activity;

import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ivy.mobilesafe.R;
import com.ivy.mobilesafe.base.BaseActivity;
import com.ivy.mobilesafe.db.dao.AppLockDao;
import com.ivy.mobilesafe.domain.AppInfo;
import com.ivy.mobilesafe.log.L;
import com.ivy.mobilesafe.utils.AppInfoUtils;

public class AppLockActivity extends BaseActivity {

	private Button btnUnlock;
	private Button btnLock;

	private LinearLayout llUnlock;
	private LinearLayout llLock;

	private ListView lvUnlock;
	private ListView lvLock;

	private ArrayList<AppInfo> mUnlockList;
	private ArrayList<AppInfo> mLockList;
	private ArrayList<AppInfo> appList;
	private TextView tvUnlock;
	private TextView tvLock;
	private AppLockDao mDao;
	private AppLockAdapter unlockAdapter;
	private AppLockAdapter lockAdapter;
	@Override
	public void initView() {
		setContentView(R.layout.activity_applock);
		btnUnlock = (Button) findViewById(R.id.btn_unlock);
		btnLock = (Button) findViewById(R.id.btn_lock);
		btnUnlock.setOnClickListener(this);
		btnLock.setOnClickListener(this);

		llUnlock = (LinearLayout) findViewById(R.id.ll_unlock);
		llLock = (LinearLayout) findViewById(R.id.ll_lock);

		lvUnlock = (ListView) findViewById(R.id.lv_unlock);
		lvLock = (ListView) findViewById(R.id.lv_lock);

		tvUnlock = (TextView) findViewById(R.id.tv_unlock);
		tvLock = (TextView) findViewById(R.id.tv_lock);
	}

	@Override
	public void initLinstener() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initData() {

		mDao = AppLockDao.getInstance(getApplicationContext());
		new Thread(new Runnable() {

			@Override
			public void run() {
				appList = AppInfoUtils.getInstallApp(getApplicationContext());
				mLockList = new ArrayList<AppInfo>();
				mUnlockList = new ArrayList<AppInfo>();
				for (AppInfo app : appList) {
					if (mDao.findByPackage(app.packageName)) {
						mLockList.add(app);
					} else {
						mUnlockList.add(app);
					}
				}

				runOnUiThread(new Runnable() {


					@Override
					public void run() {

						unlockAdapter = new AppLockAdapter(false);
						lvUnlock.setAdapter(unlockAdapter);

						lockAdapter = new AppLockAdapter(true);
						lvLock.setAdapter(lockAdapter);
					}
				});

			}
		}).start();

	}

	@Override
	public void processClick(View v) {
		switch (v.getId()) {
		case R.id.btn_lock:
			llUnlock.setVisibility(View.GONE);
			llLock.setVisibility(View.VISIBLE);

			btnUnlock.setBackgroundResource(R.drawable.tab_left_default);
			btnLock.setBackgroundResource(R.drawable.tab_right_pressed);
			break;
		case R.id.btn_unlock:

			llUnlock.setVisibility(View.VISIBLE);
			llLock.setVisibility(View.GONE);

			btnUnlock.setBackgroundResource(R.drawable.tab_left_pressed);
			btnLock.setBackgroundResource(R.drawable.tab_right_default);

			break;

		}

	}

	class AppLockAdapter extends BaseAdapter {

		private boolean isLock;

		private TranslateAnimation animRight;
		private TranslateAnimation animLeft;

		public AppLockAdapter(boolean isLock) {
			this.isLock = isLock;

			// 右移动画
			animRight = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
					Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF,
					0, Animation.RELATIVE_TO_SELF, 0);

			animRight.setDuration(500);

			// 左移动画
			animLeft = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
					Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF,
					0, Animation.RELATIVE_TO_SELF, 0);

			animLeft.setDuration(500);
		}

		@Override
		public int getCount() {
			// 每次刷新都会走此方法, 在这里更新最新的加锁数量
			updateLockNum();

			if (isLock) {
				return mLockList.size();
			} else {
				return mUnlockList.size();
			}
		}

		@Override
		public AppInfo getItem(int position) {
			if (isLock) {
				return mLockList.get(position);
			} else {
				return mUnlockList.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.list_item_applock, null);
				holder = new ViewHolder();
				holder.tvName = (TextView) convertView
						.findViewById(R.id.tv_name);
				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.iv_icon);
				holder.ivLock = (ImageView) convertView
						.findViewById(R.id.iv_lock);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final View itemView = convertView;
			holder.ivLock.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					final String packageName = getItem(position).packageName;
					if (!isLock) {
						L.v("加锁---" + packageName);

						animRight.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationStart(Animation animation) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onAnimationRepeat(Animation animation) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onAnimationEnd(Animation animation) {
								mDao.add(packageName);
								mLockList.add(getItem(position));
								mUnlockList.remove(getItem(position));
								
								
								unlockAdapter.notifyDataSetChanged();
								lockAdapter.notifyDataSetChanged();

							}
						});

						itemView.startAnimation(animRight);
					} else {
						L.v("解锁---" + packageName);

						animLeft.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationStart(Animation animation) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onAnimationRepeat(Animation animation) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onAnimationEnd(Animation animation) {
								mDao.delete(packageName);

								mUnlockList.add(getItem(position));
								mLockList.remove(getItem(position));
								
								unlockAdapter.notifyDataSetChanged();
								lockAdapter.notifyDataSetChanged();
							}
						});

						itemView.startAnimation(animLeft);
					}

					

				}
			});

			final AppInfo info = getItem(position);
			holder.tvName.setText(info.name);
			holder.ivIcon.setImageDrawable(info.logo);

			if (isLock) {
				holder.ivLock.setImageResource(R.drawable.unlock);
			} else {
				holder.ivLock.setImageResource(R.drawable.lock);
			}

			return convertView;
		}
	}

	static class ViewHolder {
		public TextView tvName;
		public ImageView ivIcon;
		public ImageView ivLock;
	}

	/**
	 * 更新已加锁未加锁数量
	 */
	private void updateLockNum() {
		tvUnlock.setText("未加锁软件:" + mUnlockList.size() + "个");
		tvLock.setText("已加锁软件:" + mLockList.size() + "个");
	}
}
