package com.ivy.mobilesafe.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.ivy.mobilesafe.R;
import com.ivy.mobilesafe.base.BaseActivity;
import com.ivy.mobilesafe.db.dao.CommonNumDao;
import com.ivy.mobilesafe.db.dao.CommonNumDao.ChildInfo;
import com.ivy.mobilesafe.db.dao.CommonNumDao.GroupInfo;
import com.ivy.mobilesafe.log.L;

public class CommonNumberActivity extends BaseActivity {

	private ExpandableListView elv;
	private ArrayList<GroupInfo> list;

	@Override
	public void initView() {
		setContentView(R.layout.activity_common_number);
		
		elv = (ExpandableListView) findViewById(R.id.elv_list);
	}

	@Override
	public void initLinstener() {
		
		elv.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				
				if(list!=null){
					String number = list.get(groupPosition).children.get(childPosition).number;
					L.v("--number--"+number);
					
					Intent intent = new Intent(Intent.ACTION_DIAL);
					intent.setData(Uri.parse("tel://"+number));
					startActivity(intent);
					
				}
				
				return true;
			}
		});

	}

	@Override
	public void initData() {
		
		new Thread(new Runnable() {
			
			

			@Override
			public void run() {
				list = CommonNumDao.getCategory(getApplicationContext());
				
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						MyAdapter adapter = new MyAdapter();
						elv.setAdapter(adapter);
					}
				});
				
			}
		}).start();
		

	}

	@Override
	public void processClick(View v) {

	}

	
	
	class MyAdapter extends BaseExpandableListAdapter{

		@Override
		public int getGroupCount() {
			return list.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return list.get(groupPosition).children.size();
		}

		@Override
		public GroupInfo getGroup(int groupPosition) {
			return list.get(groupPosition);
		}

		@Override
		public ChildInfo  getChild(int groupPosition, int childPosition) {
			return list.get(groupPosition).children.get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView view = new TextView(getApplicationContext());
			view.setTextColor(Color.RED);
			view.setTextSize(20);
			// view.setText("       第" + groupPosition + "组");
			GroupInfo group = getGroup(groupPosition);
			view.setText("      " + group.name);
			return view;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView view = new TextView(getApplicationContext());
			view.setTextColor(Color.BLACK);
			view.setTextSize(18);
			// view.setText("第" + groupPosition + "组-第" + childPosition + "项");
			ChildInfo child = getChild(groupPosition, childPosition);
			view.setText(child.name + "\n" + child.number);
			return view;
		}

		// 表示孩子是否可以点击
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
		
	}
}
