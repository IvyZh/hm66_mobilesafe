package com.ivy.mobilesafe.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.ivy.mobilesafe.R;
import com.ivy.mobilesafe.base.BaseSetupActivity;
import com.ivy.mobilesafe.utils.CursorUtils;
import com.ivy.mobilesafe.utils.SharedPreUtils;
import com.ivy.mobilesafe.utils.ToastUtils;

public class Setup3Activity extends BaseSetupActivity {

	private EditText etPhone;

	@Override
	public void goNextActivity(View v) {
		//判断安全号码是否填写正确
		
		String phone = etPhone.getText().toString().trim();
		if (!TextUtils.isEmpty(phone)) {
			SharedPreUtils.putString(this,"safe_phone", phone);

			Intent intent = new Intent(this,Setup4Activity.class);
			startActivity(intent);
			finish();
			
			overridePendingTransition(R.anim.anim_next_in, R.anim.anim_next_out);
		} else {
			ToastUtils.show(this, "安全号码不能为空!");
		}
		
		
	}

	@Override
	public void goPreActivity(View v) {
		Intent intent = new Intent(this,Setup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.anim_pre_in, R.anim.anim_pre_out);
	}

	@Override
	public void initView() {
		setContentView(R.layout.activity_setup3);

		etPhone = (EditText) findViewById(R.id.et_phone);
		String phone = SharedPreUtils.getString(this,"safe_phone");
		etPhone.setText(phone);
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
		// TODO Auto-generated method stub

	}
	
	public void selectContact(View v){
		// 选择联系人，这里就用系统的PickContacts
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_PICK);   
		intent.setData(Contacts.CONTENT_URI);
		startActivityForResult(intent, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==0){
			if(data!=null){
				//Intent { dat=content://contacts/people/2 flg=0x1 (has extras) }
				// 需要用内容提供者再进行查询 TODO
				
				Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
				CursorUtils.print(cursor);
				
				
				cursor.close();
				
				
				// 这里先假设取到了昵称和号码
				
				etPhone.setText("5556");
			}
		}
	}
}
