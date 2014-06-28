package com.tx.example.nlp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Menu;

import com.google.android.location.R;
import com.tx.example.nlp._40.TencentLocationProvider;

public class AlertActivity extends Activity implements OnClickListener {

	private boolean mAgreed = false;

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
		TencentLocationProvider.userConfirm(mAgreed);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
