package com.tx.example.nlp._42;

import android.os.Bundle;
import android.os.WorkSource;

import com.android.location.provider.LocationProviderBase;
import com.android.location.provider.ProviderPropertiesUnbundled;
import com.android.location.provider.ProviderRequestUnbundled;
import com.tx.example.nlp.util.Debug;

public class TencentLocationProvider extends LocationProviderBase {
	private static final String TAG = TencentLocationProvider.class.getSimpleName();

	public TencentLocationProvider(String arg0, ProviderPropertiesUnbundled arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		Debug.i(TAG, "on disable");
	}

	@Override
	public void onEnable() {
		// TODO Auto-generated method stub
		Debug.i(TAG, "on enable");
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
	public void onSetRequest(ProviderRequestUnbundled arg0, WorkSource arg1) {
		// TODO Auto-generated method stub

		
	}

}
