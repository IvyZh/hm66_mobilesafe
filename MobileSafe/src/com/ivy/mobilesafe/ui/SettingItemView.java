package com.ivy.mobilesafe.ui;

import android.R.attr;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ivy.mobilesafe.R;
import com.ivy.mobilesafe.log.L;

public class SettingItemView extends RelativeLayout {

	private TextView tvTitle;
	private TextView tvDesc;
	private CheckBox cbCheck;

	private static final String NAME_SPACE = "http://schemas.android.com/apk/res/com.ivy.mobilesafe";
	private String desc_off;
	private String desc_on;
	private String title;
	// 布局文件+样式
	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	// 布局文件
	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		desc_off = attrs.getAttributeValue(NAME_SPACE, "desc_off");
		desc_on = attrs.getAttributeValue(NAME_SPACE, "desc_on");
		title = attrs.getAttributeValue(NAME_SPACE, "title");
		
		initView(context, attrs);
	}

	// new 出来
	public SettingItemView(Context context) {
		super(context);
	}

	private void initView(Context context, AttributeSet attrs) {
		View child = View.inflate(context, R.layout.setting_item_view,
				null);// 初始化组合控件布局

		tvTitle = (TextView) child.findViewById(R.id.tv_title);
		tvDesc = (TextView) child.findViewById(R.id.tv_desc);
		cbCheck = (CheckBox) child.findViewById(R.id.cb_check);
		
		tvTitle.setText(title);
		tvDesc.setText(desc_off);

		this.addView(child);// 将布局添加给当前的RelativeLayout对象
		
	}
	
	/**
	 * 获取CheckBox是否已经勾选
	 * @return
	 */
	public boolean getChecked(){
		return cbCheck.isChecked();
	}

	/**
	 * 这种cb的勾状态
	 * @param b
	 */
	public void setChecked(boolean b) {
		cbCheck.setChecked(b);
		if(b){
			tvDesc.setText(desc_on);
		}else{
			tvDesc.setText(desc_off);
		}
		
	}
	

}
