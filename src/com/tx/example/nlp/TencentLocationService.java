package com.tx.example.nlp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.tx.example.nlp._40.TencentGeocodeProvider;
import com.tx.example.nlp._40.TencentLocationProvider;
import com.tx.example.nlp.util.Debug;

public class TencentLocationService extends Service {

	private static final String TAG = "TencentLocationService";

	@Override
	public synchronized IBinder onBind(Intent intent) {
		if (intent == null) {
			return null;
		}
		String action = intent.getAction();
		Debug.i(TAG, "onBind: action=" + action);

		if (Actions.V.LOCATION.equals(action)
				|| Actions.V.LOCATION2.equals(action)) {
			return new TencentLocationProvider(this).getBinder();
		} else if (Actions.V.GEOCODE.equals(action)
				|| Actions.V.GEOCODE2.equals(action)) {
			return new TencentGeocodeProvider(this).getBinder();

		} else if (Actions.V2.LOCATION.equals(action)
				|| Actions.V3.LOCATION.equals(action)) {
			return new com.tx.example.nlp._42.TencentLocationProvider(this).getBinder();

		} else if (Actions.V.GEOCODE.equals(action)) {
			return new com.tx.example.nlp._42.TencentGeocodeProvider().getBinder();

		} else {
			Debug.e(TAG, "onBind: unknow action " + action);
		}

		return null;
	}

}
