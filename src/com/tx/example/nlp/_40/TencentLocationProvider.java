package com.tx.example.nlp._40;

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
import android.os.WorkSource;
import android.provider.Settings;

import com.tx.example.nlp.AlertActivity;
import com.tx.example.nlp.Dbg;

public class TencentLocationProvider extends BaseTencentLocationProvider {
	private static final String TAG = TencentLocationProvider.class
			.getSimpleName();

	private static TencentLocationProvider sInstance;
	private final Context mContext;
	private final ProviderHandler mHandler;

	public TencentLocationProvider(Context context) {
		super();
		mContext = context;
		mHandler = new ProviderHandler();
		sInstance = this; // trick
	}

	private void userConfirm0(boolean enabled) {
		Settings.Secure.setLocationProviderEnabled(
				mContext.getContentResolver(),
				LocationManager.NETWORK_PROVIDER, enabled);
		if (enabled) {
			// setUserConfirmedPreference(true);
			// TODO
		}
	}

	public static void userConfirm(boolean enabled) {
		if (sInstance != null) {
			sInstance.userConfirm0(enabled);
		}
	}

	@Override
	public void onAddListener(int arg0, WorkSource arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisable() {
		Dbg.i(TAG, "on disable", true);

		Binder.clearCallingIdentity();
		mHandler.obtainMessage(ProviderHandler.MSG_ID_DISABLE).sendToTarget();
	}

	@Override
	public void onEnable() {
		// TODO Auto-generated method stub
		Dbg.i(TAG, "on enable", true);

		Binder.clearCallingIdentity();
		// maybe called from a non-main thread
		mHandler.obtainMessage(ProviderHandler.MSG_ID_ENABLE).sendToTarget();
	}

	@Override
	public void onEnableLocationTracking(boolean arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public String onGetInternalState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onGetStatus(Bundle arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long onGetStatusUpdateTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onRemoveListener(int arg0, WorkSource arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetMinTime(long arg0, WorkSource arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpdateLocation(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpdateNetworkState(int arg0, NetworkInfo arg1) {
		// TODO Auto-generated method stub

	}

	private void handleEnable() {
		// TODO 兼容 google 应用
		Dbg.i(TAG, "handle enable");

		Intent intent = new Intent(mContext, AlertActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
	}

	private void handleDisable() {
		Dbg.i(TAG, "handle disable");

		userConfirm0(false);
	}

	private void handleSetMinTime() {

	}

	@SuppressLint("HandlerLeak")
	private final class ProviderHandler extends Handler {
		private static final int MSG_ID_DISABLE = 2;
		private static final int MSG_ID_ENABLE = 1;

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
