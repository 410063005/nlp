package com.tx.example.nlp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.os.Bundle;

import com.google.android.location.R;
import com.tx.example.nlp._40.TencentLocationProvider;
import com.tx.example.nlp.util.Debug;

public class AlertActivity extends Activity implements OnClickListener {

	private static final String TAG = "AlertActivity";
	private boolean mAgreed = false;

	public static void start(Context context) {
		Intent intent = new Intent(context, AlertActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// dont nee content view
		// setContentView(R.layout.activity_main);
		// TODO resource
		new AlertDialog.Builder(this).setTitle("同意记录地点信息")
				.setIcon(R.drawable.ic_launcher)
				.setMessage("允许腾讯的位置服务收集匿名地点数据。系统可能会将部分数据存储在您的设备上。")
				.setPositiveButton("同意", this).setNegativeButton("不同意", this)
				.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						if (!AlertActivity.this.isFinishing()) {
							AlertActivity.this.finish();
						}
					}
				}).show();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Debug.i(TAG, "onPause: agree enable? " + mAgreed);
		// TODO trick
		if (Build.VERSION.SDK_INT >= 17) { // 4.2 及以上, 17
			com.tx.example.nlp._42.TencentLocationProvider.userConfirm(mAgreed);
		} else { // 4.0 及以下
			TencentLocationProvider.userConfirm(mAgreed);
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			mAgreed = true;
		} else {
			mAgreed = false;
		}
		finish();
	}

}
