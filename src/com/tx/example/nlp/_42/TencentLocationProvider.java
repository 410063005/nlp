package com.tx.example.nlp._42;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.WorkSource;
import android.util.Pair;

import com.android.location.provider.LocationProviderBase;
import com.android.location.provider.ProviderPropertiesUnbundled;
import com.android.location.provider.ProviderRequestUnbundled;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.map.geolocation.internal.TencentExtraKeys;
import com.tx.example.nlp.AlertActivity;
import com.tx.example.nlp.util.Debug;
import com.tx.example.nlp.util.LegacyWrapper;
import com.tx.example.nlp.util.Utils;

public class TencentLocationProvider extends LocationProviderBase implements TencentLocationListener {
	private static final String TAG = TencentLocationProvider.class.getSimpleName();

	private static ProviderPropertiesUnbundled PROPERTIES = ProviderPropertiesUnbundled
			.create(true, false, true, false, false, false, false,
					Criteria.POWER_LOW, Criteria.ACCURACY_HIGH);

	private static TencentLocationProvider sInstance;

	private final Context mContext;
	private final ProviderHandler mHandler;
	private final TencentLocationManager mLocationManager;
	private final TencentLocationRequest mLocationRequest;

	private final Object mLock = new Object();
	private int mMinTimeSeconds = 2147483647;
	private int mStatus = 2;
	private long mStatusUpdateTime = 0L;


	private boolean mSystemNlpEnabled;


	public TencentLocationProvider(Context context) {
		super("NetworkLocationProvider", PROPERTIES);
		// init final fields
		mContext = context;
		mHandler = new ProviderHandler();
		mLocationManager = TencentLocationManager.getInstance(context);
		mLocationRequest = TencentLocationRequest.create().setRequestLevel(
				TencentLocationRequest.REQUEST_LEVEL_GEO);
		TencentExtraKeys.setAllowGps(mLocationRequest, false);

		mSystemNlpEnabled = LegacyWrapper.isNetworkLocationProviderEnabled(context);
		sInstance = this; // trick
	}

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
	public void onSetRequest(ProviderRequestUnbundled req, WorkSource ws) {
		mHandler.obtainMessage(
				ProviderHandler.MSG_ID_SET_REQUEST,
				Pair.create(req, ws))
		.sendToTarget();
	}

	public void _onEnableLocationTracking(boolean enabled) {
//		Binder.clearCallingIdentity();

		if (!mSystemNlpEnabled) {
			return;
		}

		if (enabled) {

			// TODO important 调整定位周期
			Debug.i(TAG, "onEnableLocationTracking: start location");
			mLocationManager.requestLocationUpdates(mLocationRequest, this, mHandler.getLooper());
		} else {
			Debug.i(TAG, "onEnableLocationTracking: stop location and schedule pendingintent");
			mLocationManager.removeUpdates(this);
			// TODO 停止, 但定时发起定位 , 周期为 1 天
			synchronized (mLock) {
				updateStatusLocked(1);
			}
		}
	}

	// =================== callback method from TencentLocationListener
	@Override
	public void onLocationChanged(TencentLocation location, int error,
			String reason) {
		// tencent 定位sdk的结果
		Debug.i(TAG, "onLocationChanged: tencent location error = " + error);
		if (error == 0) {
			Location l = Utils.from(location);
			l.setTime(System.currentTimeMillis());
			updateStatusLocked(2);
			reportLocation(l); // 向系统汇报
		}
	}

	@Override
	public void onStatusUpdate(String name, int status, String desc) {
		// ignore
	}

	// =================== callback method from TencentLocationListener

	private void handleSetRequest(ProviderRequestUnbundled req, WorkSource ws) {
		Debug.i(TAG, "handleSetRequest: interval=" + req.getInterval());
		mMinTimeSeconds = (int) (req.getInterval() / 1000);
		handleSetMinTime();
		_onEnableLocationTracking(req.getInterval() < 3600 * 1000);
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

		// synchronized (mLock) {
			// important 调整定位周期
			final int i = mMinTimeSeconds;

			if (i > 3600) {
				Debug.i(TAG, "handleSetMinTime: stop location");
				mLocationManager.removeUpdates(this); // 周期太长的话, 直接取消定位
			} else {
				Debug.i(TAG, "handleSetMinTime: set sdk interval to " + mMinTimeSeconds + " s");
				mLocationRequest.setInterval(i * 1000); // 否则, 仅更新定位周期
			}
		// }
	}

	private void enableSystemNlp(boolean enabled) {
		mSystemNlpEnabled = enabled;
		LegacyWrapper.setNetworkLocationProviderEnabled(mContext, enabled);
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
		private static final int MSG_ID_SET_REQUEST = 1999;

		private ProviderHandler() {
		}

		public void handleMessage(Message msg) {
			switch (msg.what) {

			case MSG_ID_ENABLE:
				handleEnable();
				break;
			case MSG_ID_DISABLE:
				handleDisable();
				break;
			case MSG_ID_SET_REQUEST:
				Pair<ProviderRequestUnbundled, WorkSource> obj = (Pair<ProviderRequestUnbundled, WorkSource>) msg.obj;
				handleSetRequest(obj.first, obj.second);
				break;
			default:
				handleSetMinTime();
				break;
			}
		}
	}

}
