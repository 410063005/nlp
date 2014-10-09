package com.tx.example.nlp._40;

import java.util.HashSet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.WorkSource;
import android.provider.Settings;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.map.geolocation.internal.TencentExtraKeys;
import com.tx.example.nlp.AlertActivity;
import com.tx.example.nlp.util.Debug;

public class TencentLocationProvider extends BaseTencentLocationProvider
		implements TencentLocationListener {
	private static final String TAG = "TencentLocationProvider";

	private static TencentLocationProvider sInstance;

	private final Context mContext;
	private final ProviderHandler mHandler;
	private final TencentLocationManager mLocationManager;
	private final TencentLocationRequest mLocationRequest;
	private final HashSet<Integer> mListenerIds;

	private final Object mLock = new Object();
	// private boolean mStarted;
	private int mMinTimeSeconds = 2147483647;
	private int mNetworkState;
	private int mStatus = 2;
	private long mStatusUpdateTime = 0L;

	private boolean mSystemNlpEnabled;

	public TencentLocationProvider(Context context) {
		super();

		// init final fields
		mContext = context;
		mHandler = new ProviderHandler();
		mLocationManager = TencentLocationManager.getInstance(context);
		mLocationRequest = TencentLocationRequest.create().setRequestLevel(
				TencentLocationRequest.REQUEST_LEVEL_GEO);
		TencentExtraKeys.setAllowGps(mLocationRequest, false);
		mListenerIds = new HashSet<Integer>();

		// TODO 哪个坐标系??

		mSystemNlpEnabled = Settings.Secure.isLocationProviderEnabled(
				context.getContentResolver(), LocationManager.NETWORK_PROVIDER);
		sInstance = this; // trick
	}

	// =================== callback method from LocationProvider
	@Override
	public void onDisable() {
		Debug.i(TAG, "onDisable", true);

		Binder.clearCallingIdentity();
		mHandler.obtainMessage(ProviderHandler.MSG_ID_DISABLE).sendToTarget();
	}

	@Override
	public void onEnable() {
		Debug.i(TAG, "onEnable", true);

		Binder.clearCallingIdentity();
		// maybe called from a non-main thread
		mHandler.obtainMessage(ProviderHandler.MSG_ID_ENABLE).sendToTarget();
	}

	@Override
	public void onEnableLocationTracking(boolean enabled) {
		Binder.clearCallingIdentity();
		if (enabled) {

			// TODO important 调整定位周期

			synchronized (mLock) {
				updateStatusLocked(2);
			}
		}
	}

	@Override
	public String onGetInternalState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onGetStatus(Bundle arg0) {
		Binder.clearCallingIdentity();

		synchronized (mLock) {
			return mStatus;
		}
	}

	@Override
	public long onGetStatusUpdateTime() {
		Binder.clearCallingIdentity();

		synchronized (mLock) {
			return mStatusUpdateTime;
		}
	}

	@Override
	public void onSetMinTime(long minTime, WorkSource arg1) {
		Binder.clearCallingIdentity();
		long l = minTime / 1000L;
		int i = (int) l;
		if (l != i) {
			throw new RuntimeException("onSetMinTime: minTime is too big " + l);
		}
		synchronized (this.mLock) {
			this.mMinTimeSeconds = Math.max(i, 20);
			Message.obtain(this.mHandler, ProviderHandler.MSG_ID_SET_MIN_TIME)
					.sendToTarget();
		}

	}

	@Override
	public void onUpdateLocation(Location location) {
		if (location == null) {
			return;
		}
		if (LocationManager.GPS_PROVIDER.equals(location.getProvider())) {
			// TODO 系统可能将gps等其他模块的定位结果通过这个接口传入进来
			// TODO
		}
	}

	// FIXME 如果我们的sdk足够灵活, 比如能支持"长时间暂停", 完全可以不必实现这两个方法
	@Override
	public void onRemoveListener(int arg0, WorkSource arg1) {
		Binder.clearCallingIdentity();
		synchronized (mLock) {
			mListenerIds.remove(arg0);
			Debug.i(TAG, "onRemoveListener: " + arg0 + ", " + arg1.toString());

			if (mListenerIds.size() == 0) {
				mLocationManager.removeUpdates(this); // 停止定位
				Debug.i(TAG, "onRemoveListener: stop location");
			}
		}
	}

	@Override
	public void onAddListener(int arg0, WorkSource arg1) {
		Binder.clearCallingIdentity();

		if (!mSystemNlpEnabled) {
			Debug.i(TAG, "onAddListener: ignore location request cause nlp disabled");
			return;
		}

		synchronized (mLock) {
			mListenerIds.add(arg0);
			Debug.i(TAG, "onAddListener: " + arg0 + ", " + arg1.toString());

			if (mStatus == 2) {
				mLocationManager.requestLocationUpdates(mLocationRequest, this);
			}
		}
	}

	@Override
	public void onUpdateNetworkState(int state, NetworkInfo info) {
		Binder.clearCallingIdentity();
		synchronized (mLock) {
			mNetworkState = state;
			updateStatusLocked(state);
		}
	}

	// =================== callback method from LocationProvider

	// =================== callback method from TencentLocationListener
	@Override
	public void onLocationChanged(TencentLocation location, int error,
			String reason) {
		// tencent 定位sdk的结果
		Debug.i(TAG, "onLocationChanged: tencent location error = " + error);
		if (error == 0) {
			Location l = new Location(LocationManager.NETWORK_PROVIDER);
			l.setLatitude(location.getLatitude());
			l.setLongitude(location.getLongitude());
			l.setAltitude(location.getAltitude());
			l.setAccuracy(location.getAccuracy());
			l.setTime(location.getTime());
			reportLocation(l); // 向系统汇报
		}
	}

	@Override
	public void onStatusUpdate(String name, int status, String desc) {
		// ignore
	}

	// =================== callback method from TencentLocationListener

	private void handleEnable() {
		if (!mSystemNlpEnabled) {
			// TODO 兼容 google 应用
			Debug.i(TAG, "handleEnable: start AlertActivity");

			AlertActivity.start(mContext);
		}
	}

	private void handleDisable() {
		Debug.i(TAG, "handleDisable");

		enableSystemNlp(false);
		// TODO
		updateStatusLocked(1);
	}

	private void handleSetMinTime() {
		synchronized (mLock) {
			// important 调整定位周期
			int i = mMinTimeSeconds;
			Debug.i(TAG, "handleSetMinTime: set min time to " + i + "s");

			if (i > 3600) {
				mLocationManager.removeUpdates(this); // 周期太长的话, 直接取消定位
			} else {
				mLocationManager.requestLocationUpdates(
						mLocationRequest.setInterval(i * 1000), this);
			}
		}
	}

	private void enableSystemNlp(boolean enabled) {
		mSystemNlpEnabled = enabled;
		Settings.Secure.setLocationProviderEnabled(
				mContext.getContentResolver(),
				LocationManager.NETWORK_PROVIDER, enabled);
		if (enabled) {
			// setUserConfirmedPreference(true);
			// TODO
		}
	}

	private void updateStatusLocked(int newStatus) {
		if (this.mStatus != newStatus) {
			this.mStatus = newStatus;
			this.mStatusUpdateTime = SystemClock.elapsedRealtime();
		}
	}

	public static void userConfirm(boolean enabled) {
		if (sInstance != null) {
			sInstance.enableSystemNlp(enabled);
		}
	}

	@SuppressLint("HandlerLeak")
	private final class ProviderHandler extends Handler {
		private static final int MSG_ID_DISABLE = 2;
		private static final int MSG_ID_ENABLE = 1;
		private static final int MSG_ID_SET_MIN_TIME = 3;

		private static final int MSG_ID_ADD_LISTENER = 7;
		private static final int MSG_ID_REMOVE_LISTENER = 4;
		private static final int MSG_ID_ENABLE_LOCATION_TRACKING = 5;
		private static final int MSG_ID_GET_INTERNAL_STATE = 6;
		private static final int MSG_ID_UPDATE_LOCATION = 8;

		private ProviderHandler() {
		}

		public void handleMessage(Message paramMessage) {
			switch (paramMessage.what) {

			case MSG_ID_ENABLE:
				handleEnable();
				break;
			case MSG_ID_DISABLE:
				handleDisable();
				break;
			default:
				handleSetMinTime();
				break;
			}
		}
	}

}
