package com.ivy.mobilesafe.activity;

import java.util.ArrayList;

import android.app.ActivityManager;
import android.content.Intent;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ivy.mobilesafe.R;
import com.ivy.mobilesafe.activity.AppManagerActivity.HeaderHolder;
import com.ivy.mobilesafe.base.BaseActivity;
import com.ivy.mobilesafe.domain.ProcessInfo;
import com.ivy.mobilesafe.log.L;
import com.ivy.mobilesafe.utils.ProcessInfoProvider;
import com.ivy.mobilesafe.utils.ToastUtils;

public class ProcessManagerActivity extends BaseActivity {

	private TextView tvRunningNum;
	private TextView tvMemoInfo;
	private ListView lvList;
	private TextView tvHeader;
	private LinearLayout llLoading;
	private int mRunningProcessNum;
	private long mTotalMemory;
	private long mAvailMemory;
	private ArrayList<ProcessInfo> processList;
	private ArrayList<ProcessInfo> userList;
	private ArrayList<ProcessInfo> systemList;
	private ProcessInfoAdapter adapter;

	@Override
	public void initView() {
		setContentView(R.layout.activity_process_manager);

		tvRunningNum = (TextView) findViewById(R.id.tv_running_num);
		tvMemoInfo = (TextView) findViewById(R.id.tv_memo_info);
		lvList = (ListView) findViewById(R.id.lv_list);
		tvHeader = (TextView) findViewById(R.id.tv_header);
		llLoading = (LinearLayout) findViewById(R.id.ll_loading);

	}

	@Override
	public void initLinstener() {
		lvList.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (userList != null && systemList != null) {
					if (firstVisibleItem <= userList.size()) {
						tvHeader.setText("用户进程(" + userList.size() + ")");
					} else {
						tvHeader.setText("系统进程(" + systemList.size() + ")");
					}
				}
			}
		});

		lvList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ProcessInfo info = adapter.getItem(position);
				if (info != null) {
					if (info.packageName.equals(getPackageName())) {
						// 过滤掉手机卫士
						return;
					}

					info.isChecked = !info.isChecked;
					// mAdapter.notifyDataSetChanged();//全局刷新,性能不太好
					// 局部更新checkbox
					CheckBox cbCheck = (CheckBox) view
							.findViewById(R.id.cb_check);
					cbCheck.setChecked(info.isChecked);
				}
			}
		});

	}

	@Override
	public void initData() {
		mRunningProcessNum = ProcessInfoProvider.getRunningProcessNum(this);
		// tvRunningNum.setText("运行中的进程:" + mRunningProcessNum + "个");
		tvRunningNum.setText(String.format("运行中的进程:%d个", mRunningProcessNum));

		mAvailMemory = ProcessInfoProvider.getAvailMemory(this);
		mTotalMemory = ProcessInfoProvider.getTotalMemory(this);
		tvMemoInfo.setText(String.format("剩余/总内存:%s/%s",
				Formatter.formatFileSize(this, mAvailMemory),
				Formatter.formatFileSize(this, mTotalMemory)));

		new Thread(new Runnable() {

			@Override
			public void run() {
				processList = ProcessInfoProvider
						.getRunningProcess(ProcessManagerActivity.this);
				userList = new ArrayList<ProcessInfo>();
				systemList = new ArrayList<ProcessInfo>();
				for (ProcessInfo info : processList) {
					L.v("---info--" + info.name + "," + info.isUser);

					if (info.isUser) {
						userList.add(info);
					} else {
						systemList.add(info);
					}
				}

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						adapter = new ProcessInfoAdapter();

						lvList.setAdapter(adapter);
						llLoading.setVisibility(View.GONE);
					}
				});

			}
		}).start();
	}

	@Override
	public void processClick(View v) {
		// TODO Auto-generated method stub

	}

	class ProcessInfoAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return userList.size() + systemList.size() + 2;
			// boolean showSystem = PrefUtils.getBoolean("show_system", true,
			// getApplicationContext());
			// if (showSystem) {// 显示系统进程
			// return mUserList.size() + mSystemList.size() + 2;// 增加两个标题栏
			// } else {
			// return mUserList.size() + 1;
			// }
		}

		@Override
		public ProcessInfo getItem(int position) {
			if (position == 0 || position == userList.size() + 1) {
				// 碰到标题栏了
				return null;
			}

			if (position < userList.size() + 1) {
				return userList.get(position - 1);// 减掉1个标题栏
			} else {
				return systemList.get(position - userList.size() - 2);// 减掉两个标题栏
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		// 表示listview展示的布局种类数量, 只有重写此方法,系统才会缓存相应个数的convertView
		@Override
		public int getViewTypeCount() {
			return 2;
		}

		// 根据当前位置返回不同布局类型
		// 注意: 类型必须从0开始计数
		@Override
		public int getItemViewType(int position) {
			if (position == 0 || position == userList.size() + 1) {
				// 碰到标题栏了
				return 0;// 标题栏类型
			} else {
				return 1;// 普通类型
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 判断当前布局类型, 系统会根据当前布局类型,返回响应类型的convertView对象
			int type = getItemViewType(position);
			switch (type) {
			case 0:// 标题类型
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
					hHolder.tvHeader.setText("用户进程(" + userList.size() + ")");
				} else {
					hHolder.tvHeader.setText("系统进程(" + systemList.size() + ")");
				}
				break;
			case 1:// 普通类型
					// View view = null;
				ViewHolder holder = null;
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(),
							R.layout.list_item_processinfo, null);

					holder = new ViewHolder();
					holder.tvName = (TextView) convertView
							.findViewById(R.id.tv_name);
					holder.tvMemory = (TextView) convertView
							.findViewById(R.id.tv_memory);
					holder.ivIcon = (ImageView) convertView
							.findViewById(R.id.iv_icon);
					holder.cbCheck = (CheckBox) convertView
							.findViewById(R.id.cb_check);

					convertView.setTag(holder);
				} else {
					// view = convertView;
					holder = (ViewHolder) convertView.getTag();
				}

				ProcessInfo info = getItem(position);
				holder.tvName.setText(info.name);
				holder.ivIcon.setImageDrawable(info.icon);
				holder.tvMemory.setText(Formatter.formatFileSize(
						getApplicationContext(), info.memory));

				if (info.packageName.equals(getPackageName())) {
					// 当前应用, 不显示checkbox,过滤掉手机卫士
					holder.cbCheck.setVisibility(View.INVISIBLE);
				} else {
					holder.cbCheck.setVisibility(View.VISIBLE);
					holder.cbCheck.setChecked(info.isChecked);
				}
				break;
			}

			return convertView;
		}
	}

	static class ViewHolder {
		public TextView tvName;
		public TextView tvMemory;
		public ImageView ivIcon;
		public CheckBox cbCheck;
	}

	// 全选
	public void selectAll(View view) {
		for (ProcessInfo info : userList) {
			if (info.packageName.equals(getPackageName())) {
				// 过滤掉手机卫士
				continue;
			}

			info.isChecked = true;
		}

		for (ProcessInfo info : systemList) {
			info.isChecked = true;
		}

		adapter.notifyDataSetChanged();
	}

	// 反选
	public void reverseSelect(View view) {
		for (ProcessInfo info : userList) {
			if (info.packageName.equals(getPackageName())) {
				// 过滤掉手机卫士
				continue;
			}

			info.isChecked = !info.isChecked;
		}

		for (ProcessInfo info : systemList) {
			info.isChecked = !info.isChecked;
		}

		adapter.notifyDataSetChanged();
	}

	public void killAll(View view) {

		ArrayList<ProcessInfo> killProcess = new ArrayList<ProcessInfo>();
		for (ProcessInfo info : processList) {
			if (info.isChecked) {
				killProcess.add(info);
			}
		}

		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		long savedMemory = 0;
		for (ProcessInfo info : killProcess) {
			if(info.isUser){
				userList.remove(info);
			}else{
				systemList.remove(info);
			}

			am.killBackgroundProcesses(info.packageName);
			savedMemory += info.memory;
		}

		adapter.notifyDataSetChanged();

		ToastUtils.show(this,
				String.format("帮您杀死了%d个进程,共节省%s空间!", killProcess.size(),
						Formatter.formatFileSize(this, savedMemory)));

		// 更新文本信息
		mRunningProcessNum -= killProcess.size();
		mAvailMemory += savedMemory;
		tvRunningNum.setText(String.format("运行中的进程:%d个", mRunningProcessNum));
		tvMemoInfo.setText(String.format("剩余/总内存:%s/%s",
				Formatter.formatFileSize(this, mAvailMemory),
				Formatter.formatFileSize(this, mTotalMemory)));

	}

	// 设置
	public void setting(View view) {
		startActivityForResult(new Intent(this, ProcessSettingActivity.class),
				0);
	}
}
