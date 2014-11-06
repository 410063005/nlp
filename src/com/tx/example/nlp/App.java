package com.tx.example.nlp;

import android.app.Application;
import android.util.Log;

import com.google.android.location.BuildConfig;
import com.tencent.map.geolocation.internal.TencentExtraKeys;
import com.tencent.map.geolocation.internal.TencentLog;

public class App extends Application implements TencentLog {

	/**
	 * 配置项, 控制是否弹出 AlertActivity
	 */
	public static final boolean CONFIG_DONT_SHOW_ALERT = false;

	@Override
	public void onCreate() {
		super.onCreate();

		if (BuildConfig.DEBUG) {
			TencentExtraKeys.setTencentLog(this);
			Log.i("App",
					"onCreate: is debug mode? "
							+ TencentExtraKeys.isDebugEnabled());
		}
	}

	@Override
	public void println(String tag, int level, String msg) {
		if (level == Log.INFO) {
			Log.i(tag, msg);
		} else {
			Log.e(tag, msg);
		}
	}

}
