package com.ivy.mobilesafe.activity;

import java.util.ArrayList;
import java.util.Random;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ivy.mobilesafe.R;
import com.ivy.mobilesafe.base.BaseActivity;
import com.ivy.mobilesafe.constant.Constant;
import com.ivy.mobilesafe.db.dao.BlackNumberDao;
import com.ivy.mobilesafe.domain.BlackNumber;
import com.ivy.mobilesafe.log.L;

public class BlackNumberActivity extends BaseActivity {

	protected static final int DONE = 0;
	private BlackNumberAdapter mAdapter;
	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case DONE:
				if (mAdapter == null) {
					mAdapter = new BlackNumberAdapter();
					lvBlack.setAdapter(mAdapter);
				} else {
					mAdapter.notifyDataSetChanged();
				}
				loading = false;
				pbLoading.setVisibility(View.GONE);
				mIndex = mList.size();
				break;

			default:
				break;
			}

		};
	};
	private ListView lvBlack;
	private ArrayList<BlackNumber> mList;
	private ProgressBar pbLoading;
	private boolean loading;
	protected int mIndex = 0;

	@Override
	public void initView() {
		setContentView(R.layout.activity_black_number);

		lvBlack = (ListView) findViewById(R.id.lv_black_number);
		pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
	}

	@Override
	public void initLinstener() {

		lvBlack.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (mList != null) {
					int count = lvBlack.getCount();
					if (lvBlack.getLastVisiblePosition() == count - 1) {
						L.v("---准备加载下一页");
						if (!loading) {
							int totalCount = BlackNumberDao
									.getTotalCount(getApplicationContext());
							if (mList.size() < totalCount) {
								initData();
							}
						}
					}
				}

			}
		});

	}

	@Override
	public void initData() {

		loading = true;
		pbLoading.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {

			@Override
			public void run() {

				L.v("initData--"+mIndex);
				// mList = BlackNumberDao.findAll(BlackNumberActivity.this);
				if (mList == null) {
					mList = BlackNumberDao.findPart(getApplicationContext(),
							mIndex);
				} else {
					ArrayList<BlackNumber> part = BlackNumberDao.findPart(
							getApplicationContext(), mIndex);
					mList.addAll(part);
				}

				handler.sendEmptyMessage(DONE);
			}
		}).start();
	}

	@Override
	public void processClick(View v) {

	}

	class BlackNumberAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public BlackNumber getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		// listview重用
		// 1. 重用convertView, 保证view不会创建多次,造成内存溢出
		// 2. 使用ViewHolder, 减少findviewbyid的次数
		// 3. 将ViewHolder写成static, 保证只在内存中加载一次
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			ViewHolder holder = null;
			if (convertView == null) {
				view = View.inflate(getApplicationContext(),
						R.layout.list_item_black_number, null);

				holder = new ViewHolder();

				TextView tvNumber = (TextView) view
						.findViewById(R.id.tv_number);
				TextView tvMode = (TextView) view.findViewById(R.id.tv_mode);
				ImageView ivDelete = (ImageView) view
						.findViewById(R.id.iv_delete);

				holder.tvNumber = tvNumber;
				holder.tvMode = tvMode;
				holder.ivDelete = ivDelete;

				view.setTag(holder);// 将holder对象,通过打标记的方式保存在view中, 和view绑定在一起了
				// System.out.println("初始化view");
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();// 从view中取出holder对象
				// System.out.println("重用view");
			}

			final BlackNumber info = getItem(position);
			holder.tvNumber.setText(info.number);

			switch (info.type) {
			case 1:
				holder.tvMode.setText("拦截电话");
				break;
			case 2:
				holder.tvMode.setText("拦截短信");
				break;
			case 3:
				holder.tvMode.setText("拦截全部");
				break;
			}

			// 给删除按钮添加点击事件
			holder.ivDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 1.从数据库删除
					// 2.从集合删除
					// 3.刷机listview
					int delete = BlackNumberDao.delete(info.number,
							BlackNumberActivity.this);
					L.v("--删除---" + delete);
					mList.remove(info);
					mAdapter.notifyDataSetChanged();
				}
			});

			return view;
		}
	}

	static class ViewHolder {
		public TextView tvNumber;
		public TextView tvMode;
		public ImageView ivDelete;
	}

	public void addBlackNumber(View v) {
		ContentResolver cr = getContentResolver();

		ContentValues values = new ContentValues();
		values.put("type", 1);
		values.put("number", "1"+new Random().nextInt(100000)+new Random().nextInt(1000));
		cr.insert(Uri.parse(Constant.URI.BLACK_NUMBER), values);
		
		initData();
	}
}
