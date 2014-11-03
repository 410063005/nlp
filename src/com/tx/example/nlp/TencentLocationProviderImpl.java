package com.tx.example.nlp;

import java.util.HashSet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.WorkSource;
import android.util.Pair;

import com.android.location.provider.ProviderRequestUnbundled;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.map.geolocation.internal.TencentExtraKeys;
import com.tx.example.nlp.util.Debug;
import com.tx.example.nlp.util.LegacyWrapper;
import com.tx.example.nlp.util.Utils;

public class TencentLocationProviderImpl implements TencentLocationListener {
	private static final String TAG = "TencentLocationProviderProxy";

	private static TencentLocationProviderImpl sInstance;

	private final LocationReporter mLocationReporter;

	private final Context mContext;
	private final ProviderHandler mHandler;
	private final TencentLocationManager mLocationManager;
	private final TencentLocationRequest mLocationRequest;
	private final HashSet<Integer> mListenerIds;

	private final Object mLock = new Object();
	// private boolean mStarted;
	private int mMinTimeSeconds = 24 * 3600;//2147483647;
	private int mNetworkState;
	private int mStatus = 2;
	private long mStatusUpdateTime = 0L;

	private boolean mSystemNlpEnabled;

	public TencentLocationProviderImpl(Context context, LocationReporter reporter) {
		super();

		// init final fields
		mLocationReporter = reporter;
		mContext = context;
		mHandler = new ProviderHandler();
		mLocationManager = TencentLocationManager.getInstance(context);
		mLocationRequest = TencentLocationRequest.create().setRequestLevel(
				TencentLocationRequest.REQUEST_LEVEL_GEO)
				.setInterval(3000);
		TencentExtraKeys.setAllowGps(mLocationRequest, false);
		mListenerIds = new HashSet<Integer>();

		// TODO 哪个坐标系??
		mLocationManager.setCoordinateType(TencentLocationManager.COORDINATE_TYPE_WGS84);

		mSystemNlpEnabled = LegacyWrapper.isNetworkLocationProviderEnabled(context);
		sInstance = this; // trick
		Debug.i(TAG, "locaton provider proxy created");
	}

	// =================== public api
	public void enableSystemNlp(boolean enabled) {
		mSystemNlpEnabled = enabled;
		LegacyWrapper.setNetworkLocationProviderEnabled(mContext, enabled);
		if (enabled) {
			// setUserConfirmedPreference(true);
			// TODO
		}
	}
	// =================== public api

	// =================== callback method from LocationProvider

	// common for 4.0 and 4.2 & status 相关
	public void onDisable() {
		Debug.i(TAG, "onDisable", true);

		Binder.clearCallingIdentity();
		mHandler.obtainMessage(ProviderHandler.MSG_ID_DISABLE).sendToTarget();
	}

	public void onEnable() {
		Debug.i(TAG, "onEnable", true);

		Binder.clearCallingIdentity();
		// maybe called from a non-main thread
		mHandler.obtainMessage(ProviderHandler.MSG_ID_ENABLE).sendToTarget();
	}

	public int onGetStatus(Bundle arg0) {
		Binder.clearCallingIdentity();
		synchronized (mLock) {
			Debug.i(TAG, "onGetStatus: mStatus=" + mStatus, true);
			return mStatus;
		}
	}

	// status 相关
	public long onGetStatusUpdateTime() {
		Binder.clearCallingIdentity();

		synchronized (mLock) {
			Debug.i(TAG, "onGetStatusUpdateTime: mStatusUpdateTime=" + mStatusUpdateTime, true);
			return mStatusUpdateTime;
		}
	}

	public void onUpdateNetworkState(int state, NetworkInfo info) {
		Binder.clearCallingIdentity();

		synchronized (mLock) {
			Debug.i(TAG, "onUpdateNetworkState: mNetworkState changed from " + mNetworkState + " to " + state, true);
			mNetworkState = state;
			updateStatusLocked(mNetworkState);
		}
	}

	public String onGetInternalState() {
		// TODO Auto-generated method stub
		return null;
	}

	public void onEnableLocationTracking(boolean enabled) {
		Binder.clearCallingIdentity();

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

	public void onSetMinTime(long minTime, WorkSource arg1) {
		Binder.clearCallingIdentity();

		Debug.i(TAG, "onSetMinTime: minTime is " + minTime + " ms");

		long l = minTime / 1000L;
		int i = (int) l;
		if (l != i) {
			throw new RuntimeException("onSetMinTime: minTime is too big " + l);
		}
		synchronized (this.mLock) {
			this.mMinTimeSeconds = Math.max(i, 1);
			Message.obtain(this.mHandler, ProviderHandler.MSG_ID_SET_MIN_TIME)
					.sendToTarget();
		}

	}

	// FIXME 如果我们的sdk足够灵活, 比如能支持"长时间暂停", 完全可以不必实现这两个方法
	public void onRemoveListener(int arg0, WorkSource arg1) {
		Binder.clearCallingIdentity();
		synchronized (mLock) {
			final boolean notEmpty = mListenerIds.remove(arg0);
			Debug.i(TAG, "onRemoveListener: " + arg0 + ", " + arg1.toString());

			if (notEmpty && mListenerIds.isEmpty()) {
				mLocationManager.removeUpdates(this); // 停止定位
				Debug.i(TAG, "onRemoveListener: stop location");
			}
		}
	}

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
				mLocationManager.requestLocationUpdates(mLocationRequest, this, mHandler.getLooper());
			}
		}
	}

	public void onSetRequest(ProviderRequestUnbundled req, WorkSource ws) {
		Binder.clearCallingIdentity();

		if (!mSystemNlpEnabled) {
			Debug.i(TAG, "onSetRequest: ignore location request cause nlp disabled");
			return;
		}

		mHandler.obtainMessage(
				ProviderHandler.MSG_ID_SET_REQUEST,
				Pair.create(req, ws))
		.sendToTarget();
	}

	public void onUpdateLocation(Location location) {
		// 系统将gps等其他模块的定位结果通过这个接口传入进来
		// 但从4.2系统开始, 这个接口不会被回调了, 所以干脆不实现
		// ignore
	}
	// =================== callback method from LocationProvider

	// =================== callback method from TencentLocationListener
	public void onLocationChanged(TencentLocation location, int error,
			String reason) {
		// tencent 定位sdk的结果
		Debug.i(TAG, "onLocationChanged: tencent location error = " + error);
		if (error == 0) {
			Location l = Utils.from(location);
			l.setTime(System.currentTimeMillis());
			updateStatusLocked(2);
			mLocationReporter.reportLocation(l); // 向系统汇报
		}
	}

	public void onStatusUpdate(String name, int status, String desc) {
		// ignore
	}

	// =================== callback method from TencentLocationListener

	// =================== handler method
	private void handleEnable() {
		if (!mSystemNlpEnabled) {
			if (App.CONFIG_DONT_SHOW_ALERT) {
				Debug.i(TAG, "handleEnable: skip AlertActivity");
				userConfirm(true);
			} else {
				// TODO 兼容 google 应用
				Debug.i(TAG, "handleEnable: start AlertActivity");
				AlertActivity.start(mContext);
			}
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

	private void handleSetRequest(ProviderRequestUnbundled req, WorkSource ws) {
		Debug.i(TAG, "handleSetRequest: interval=" + req.getInterval());
		mMinTimeSeconds = Math.max((int) (req.getInterval() / 1000), 1);
		handleSetMinTime();
		onEnableLocationTracking(req.getInterval() < 3600 * 1000);
	}
	// =================== handler method

	// =================== private method
	private void updateStatusLocked(int newStatus) {
		if (this.mStatus != newStatus) {
			this.mStatus = newStatus;
			this.mStatusUpdateTime = SystemClock.elapsedRealtime();
		}
	}

	private static void userConfirm(boolean enabled) {
		if (sInstance != null) {
			sInstance.enableSystemNlp(enabled);
		}
	}

	// =================== private method

	@SuppressLint("HandlerLeak")
	private final class ProviderHandler extends Handler {
		private static final int MSG_ID_DISABLE = 2;
		private static final int MSG_ID_ENABLE = 1;
		private static final int MSG_ID_SET_MIN_TIME = 3;
		private static final int MSG_ID_SET_REQUEST = 1999;

		private ProviderHandler() {
		}

		@SuppressWarnings("unchecked")
		public void handleMessage(Message paramMessage) {
			switch (paramMessage.what) {

			case MSG_ID_ENABLE:
				handleEnable();
				break;

			case MSG_ID_DISABLE:
				handleDisable();
				break;

			case MSG_ID_SET_REQUEST:
				Pair<ProviderRequestUnbundled, WorkSource> obj = (Pair<ProviderRequestUnbundled, WorkSource>) paramMessage.obj;
				handleSetRequest(obj.first, obj.second);
				break;

			case MSG_ID_SET_MIN_TIME:
				handleSetMinTime();
				break;

			default:
				Debug.i(TAG, "handleMessage: unknown msg.what " + paramMessage.what);
				break;
			}
		}
	}

}
