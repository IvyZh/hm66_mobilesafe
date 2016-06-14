package com.ivy.mobilesafe.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.SAXParserFactory;

import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;
import android.view.View;
import android.widget.ProgressBar;

import com.ivy.mobilesafe.R;
import com.ivy.mobilesafe.base.BaseActivity;
import com.ivy.mobilesafe.domain.Sms;
import com.ivy.mobilesafe.log.L;
import com.ivy.mobilesafe.utils.CursorUtils;

public class AToolsActivity extends BaseActivity {
	private ArrayList<Sms> smsList;
	private ProgressBar pb;

	@Override
	public void initView() {
		setContentView(R.layout.activity_atools);
		
		pb = (ProgressBar) findViewById(R.id.pb_progress);
	}

	@Override
	public void initLinstener() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void processClick(View v) {

	}

	
	/**
	 * 常用号码查询
	 */
	public void commonNumberQuery(View view) {
		startActivity(new Intent(this, CommonNumberActivity.class));
	}
	
	/**
	 * 程序锁
	 * @param view
	 */
	public void appLock(View view) {
		startActivity(new Intent(this, AppLockActivity.class));
	}
	// 归属地查询
	public void addressQuery(View v) {
		startActivity(new Intent(this, AddressQueryActivity.class));
	}

	// 短信备份
	public void smsBackup(View v) {
		
		pb.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// 获取所有短信内容封装到短信业务Bean里面
				ContentResolver resolver = getContentResolver();
				Cursor cursor = resolver.query(
						Uri.parse("content://sms"),
						new String[] { "_id", "address", "body", "date", "type" },
						null, null, null);
//				CursorUtils.print(cursor);
				smsList = new ArrayList<Sms>();
				while (cursor.moveToNext()) {
					L.v("add...");
					Sms sms = Sms.createFromCursor(cursor);
					smsList.add(sms);
				}
				System.out.println("---done---");
				writeToLocal();
			}
		}).start();
	}

	/**
	 * 本地化短信内容
	 */
	protected void writeToLocal() {
		L.v("b本地化短信内容");
		
		pb.setMax(smsList.size());
		
		
		try {
			XmlSerializer serializer = Xml.newSerializer();
			File file = new File(getFilesDir(), "sms_backup.xml");
			serializer.setOutput(new FileOutputStream(file ), "utf-8");
			serializer.startDocument("utf-8", false);
			serializer.startTag(null, "smss");
			for (int i=0;i<smsList.size();i++) {
				Sms sms = smsList.get(i);
				pb.setProgress(i);
				Thread.sleep(50);
				serializer.startTag(null, "sms");
				
				serializer.startTag(null, "_id");
				serializer.text(sms.get_id()+"");
				serializer.endTag(null, "_id");
				
				serializer.startTag(null, "type");
				serializer.text(sms.getType()+"");
				serializer.endTag(null, "type");
				
				serializer.startTag(null, "address");
				serializer.text(sms.getAddress());
				serializer.endTag(null, "address");
				
				serializer.startTag(null, "body");
				serializer.text(sms.getBody());
				serializer.endTag(null, "body");
				
				serializer.startTag(null, "date");
				serializer.text(sms.getDate()+"");
				serializer.endTag(null, "date");
				
				serializer.endTag(null, "sms");
				
				System.out.println("finish---");
			}
			
			serializer.endTag(null, "smss");
			serializer.endDocument();
			
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					pb.setVisibility(View.GONE);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
