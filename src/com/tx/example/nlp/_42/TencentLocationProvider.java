package com.tx.example.nlp._42;

import android.content.Context;
import android.location.Criteria;
import android.os.Bundle;
import android.os.WorkSource;

import com.android.location.provider.LocationProviderBase;
import com.android.location.provider.ProviderPropertiesUnbundled;
import com.android.location.provider.ProviderRequestUnbundled;
import com.tx.example.nlp.LocationReporter;
import com.tx.example.nlp.TencentLocationProviderProxy;
import com.tx.example.nlp._40.BaseTencentLocationProvider;
import com.tx.example.nlp.util.Debug;

public class TencentLocationProvider extends LocationProviderBase implements
		LocationReporter {
	private static final String TAG = "TencentLocationProvider_42";

	/**
	 * provider 参数, 应跟 {@link BaseTencentLocationProvider} 回调方法保持一致.
	 */
	private static ProviderPropertiesUnbundled PROPERTIES = ProviderPropertiesUnbundled
			.create(true, // requiresNetwork
					false, // requiresSatellite
					true, // requiresCell
					false, // hasMonetaryCost
					false, // supportsAltitude
					false, // supportsSpeed
					false, // supportsBearing
					Criteria.POWER_LOW, // powerRequirement
					Criteria.ACCURACY_FINE // accuracy
			);

	private static TencentLocationProvider sInstance;
	private TencentLocationProviderProxy mProxy;

	public TencentLocationProvider(Context context) {
		super("NetworkLocationProvider", PROPERTIES);
		mProxy = new TencentLocationProviderProxy(context, this);
		sInstance = this; // trick
		Debug.i(TAG, "location provider 4.2 created");
	}

	@Override
	public void onDisable() {
		mProxy.onDisable();
	}

	@Override
	public void onEnable() {
		mProxy.onEnable();
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
	public void onSetRequest(ProviderRequestUnbundled req, WorkSource ws) {
		mProxy.onSetRequest(req, ws);
	}

	// =================== callback method from TencentLocationListener
	private void enableSystemNlp(boolean enabled) {
		mProxy.enableSystemNlp(enabled);
	}

	public static void userConfirm(boolean enabled) {
		if (sInstance != null) {
			sInstance.enableSystemNlp(enabled);
		}
	}
}
