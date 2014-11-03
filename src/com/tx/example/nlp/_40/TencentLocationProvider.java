package com.tx.example.nlp._40;

import android.content.Context;
import android.location.Location;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.WorkSource;

import com.tx.example.nlp.LocationReporter;
import com.tx.example.nlp.TencentLocationProviderProxy;
import com.tx.example.nlp.util.Debug;

public class TencentLocationProvider extends BaseTencentLocationProvider implements LocationReporter {
	private static final String TAG = "TencentLocationProvider_40";

	private static TencentLocationProvider sInstance;

	private final TencentLocationProviderProxy mProxy;

	public TencentLocationProvider(Context context) {
		super();
		mProxy = new TencentLocationProviderProxy(context, this);
		sInstance = this; // trick
		Debug.i(TAG, "locaton provider 4.0 created");
	}

	// =================== callback method from LocationProvider
	@Override
	public void onDisable() {
		mProxy.onDisable();
	}

	@Override
	public void onEnable() {
		mProxy.onEnable();
	}

	@Override
	public void onEnableLocationTracking(boolean enabled) {
		mProxy.onEnableLocationTracking(enabled);
	}

	@Override
	public String onGetInternalState() {
		return mProxy.onGetInternalState();
	}

	@Override
	public int onGetStatus(Bundle arg0) {
		return mProxy.onGetStatus(arg0);
	}

	@Override
	public long onGetStatusUpdateTime() {
		return mProxy.onGetStatusUpdateTime();
	}

	@Override
	public void onSetMinTime(long minTime, WorkSource arg1) {
		mProxy.onSetMinTime(minTime, arg1);
	}

	@Override
	public void onUpdateLocation(Location location) {
		mProxy.onUpdateLocation(location);
	}

	// FIXME 如果我们的sdk足够灵活, 比如能支持"长时间暂停", 完全可以不必实现这两个方法
	@Override
	public void onRemoveListener(int arg0, WorkSource arg1) {
		mProxy.onRemoveListener(arg0, arg1);
	}

	@Override
	public void onAddListener(int arg0, WorkSource arg1) {
		mProxy.onAddListener(arg0, arg1);
	}

	@Override
	public void onUpdateNetworkState(int state, NetworkInfo info) {
		mProxy.onUpdateNetworkState(state, info);
	}

	private void enableSystemNlp(boolean enabled) {
		mProxy.enableSystemNlp(enabled);
	}

	public static void userConfirm(boolean enabled) {
		if (sInstance != null) {
			sInstance.enableSystemNlp(enabled);
		}
	}
}
