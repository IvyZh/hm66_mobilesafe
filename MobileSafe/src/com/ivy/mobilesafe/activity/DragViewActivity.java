package com.ivy.mobilesafe.activity;

import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ivy.mobilesafe.R;
import com.ivy.mobilesafe.base.BaseActivity;
import com.ivy.mobilesafe.log.L;
import com.ivy.mobilesafe.utils.SharedPreUtils;

public class DragViewActivity extends BaseActivity {

	private ImageView ivDrag;
	private TextView tvTop;
	private TextView tvBottom;
	private int mHeight;
	private int mWidth;
	private int startX;
	private int startY;
	long[] mHits = new long[2];// 数组长度就是多击次数

	@Override
	public void initView() {
		setContentView(R.layout.activity_drag_view);

		ivDrag = (ImageView) findViewById(R.id.iv_drag);
		tvTop = (TextView) findViewById(R.id.tv_top);
		tvBottom = (TextView) findViewById(R.id.tv_bottom);

		mWidth = getWindowManager().getDefaultDisplay().getWidth();
		mHeight = getWindowManager().getDefaultDisplay().getHeight();

		int pos_x = SharedPreUtils.getInt(this, "pos_x");

		int pos_y = SharedPreUtils.getInt(this, "pos_y");

		if (pos_x < 0) {
			pos_x = 0;
		}
		if (pos_x > mWidth) {
			pos_x = mWidth;
		}

		if (pos_y < 0) {
			pos_y = 0;
		}
		if (pos_y > mHeight) {
			pos_y = mHeight;
		}

		// 根据当前位置,显示文本框提示
		if (pos_y > mHeight / 2) {
			// 下方
			tvTop.setVisibility(View.VISIBLE);
			tvBottom.setVisibility(View.INVISIBLE);
		} else {
			// 上方
			tvTop.setVisibility(View.INVISIBLE);
			tvBottom.setVisibility(View.VISIBLE);
		}

		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) ivDrag
				.getLayoutParams();
		params.topMargin = pos_y;
		params.leftMargin = pos_x;

		L.v("pos_x:" + pos_x);
		L.v("pos_y:" + pos_y);
		ivDrag.setLayoutParams(params);

	}

	@Override
	public void initLinstener() {
		ivDrag.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:

					startX = (int) event.getRawX();
					startY = (int) event.getRawY();

					break;
				case MotionEvent.ACTION_MOVE:
					int endX = (int) event.getRawX();
					int endY = (int) event.getRawY();

					int dx = endX - startX;
					int dy = endY - startY;

					int top = ivDrag.getTop() + dy;
					int left = ivDrag.getLeft() + dx;
					int bottom = ivDrag.getBottom() + dy;
					int right = ivDrag.getRight() + dx;

					L.v(top + "," + left + "," + bottom + "," + right);
					// 310,-31,343,382

					// 06-07 11:13:45.694: V/ivy(6323): 163,220,196,442
					// 06-07 11:14:07.733: V/ivy(6323): 166,286,199,381

					if (bottom > mHeight || top < 0) {
						return false;
					}

					if (right > mWidth || left < 0) {
						return false;
					}

					if (top < mHeight / 2) {
						tvBottom.setVisibility(View.VISIBLE);
						tvTop.setVisibility(View.INVISIBLE);
					} else {
						tvTop.setVisibility(View.VISIBLE);
						tvBottom.setVisibility(View.INVISIBLE);
					}

					ivDrag.layout(left, top, right, bottom);

					startX = (int) event.getRawX();
					startY = (int) event.getRawY();

					break;
				case MotionEvent.ACTION_UP:

					int pos_x = ivDrag.getLeft();
					int pos_y = ivDrag.getTop();

					SharedPreUtils.putInt(getApplicationContext(), "pos_x",
							pos_x);
					SharedPreUtils.putInt(getApplicationContext(), "pos_y",
							pos_y);
					break;

				}

				return false;
			}
		});

		ivDrag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 数组拷贝:参1:原数组;参2:原数组拷贝起始位置;参3:目标数组;参4:目标数组起始拷贝位置;参5:拷贝数组长度
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();// 手机开机时间
				if (SystemClock.uptimeMillis() - mHits[0] <= 500) {
					// 布局居中显示
					center();
				}

			}
		});

	}

	protected void center() {
		ivDrag.layout(mWidth / 2 - ivDrag.getWidth() / 2, ivDrag.getTop(),
				mWidth / 2 + ivDrag.getWidth() / 2, ivDrag.getBottom());
	}

	@Override
	public void initData() {

	}

	@Override
	public void processClick(View v) {

	}

}
