package com.ivy.mobilesafe.activity;

import android.net.TrafficStats;
import android.view.View;
import android.widget.TextView;

import com.ivy.mobilesafe.R;
import com.ivy.mobilesafe.base.BaseActivity;

public class TrafficStatsActivity extends BaseActivity {

	private TextView tvTcp;

	@Override
	public void initView() {
		setContentView(R.layout.activity_traffic_stats);
		tvTcp = (TextView) findViewById(R.id.tv_tcp);

	}

	@Override
	public void initLinstener() {

	}

	@Override
	public void initData() {
		
		String totalRxBytes = "总下载流量:" + TrafficStats.getTotalRxBytes();// wifi+3g
		String totalTxBytes = "总上传流量:" + TrafficStats.getTotalTxBytes();// wifi+3g

		String mobileRxBytes = "移动下载流量" + TrafficStats.getMobileRxBytes();// 3g下载流量
		String mobileTxBytes = "移动上传流量" + TrafficStats.getMobileTxBytes();// 3g下载流量

		// 具体微信uid以真机为准
		String uidRxBytes = "微信下载流量:" + TrafficStats.getUidRxBytes(10088);// wifi+3g
		String uidTxBytes = "微信上传流量:" + TrafficStats.getUidTxBytes(10088);// wifi+3g
		
		
		
		StringBuffer sb = new StringBuffer();
		sb.append(totalRxBytes).append(totalTxBytes).append(mobileRxBytes).append(mobileTxBytes).append(uidRxBytes)
		.append(uidTxBytes);
		tvTcp.setText(sb.toString());
	}

	@Override
	public void processClick(View v) {
		// TODO Auto-generated method stub

	}

}
