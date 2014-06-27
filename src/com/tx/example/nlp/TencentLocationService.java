package com.tx.example.nlp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.tx.example.nlp._40.TencentGeocodeProvider;
import com.tx.example.nlp._40.TencentLocationProvider;

public class TencentLocationService extends Service {

	private static final String ACTION_GEOCODE = "com.google.android.location.GeocodeProvider";
	private static final String ACTION_LOCATION = "com.google.android.location.NetworkLocationProvider";

	private static final String ACTION_GEOCODE_V2 = "com.google.android.location.v2.GeocodeProvider";
	private static final String ACTION_LOCATION_V2 = "com.google.android.location.v2.NetworkLocationProvider";

	private static final String TAG = TencentLocationService.class
			.getSimpleName();

	@Override
	public IBinder onBind(Intent intent) {
		if (intent == null) {
			return null;
		}
		String action = intent.getAction();
		Dbg.i(TAG, "on bind: " + action);
		if (ACTION_LOCATION.equals(action)) {
			return new TencentLocationProvider().getBinder();
		} else if (ACTION_GEOCODE.equals(action)) {
			return new TencentGeocodeProvider().getBinder();
		} else if (ACTION_LOCATION_V2.equals(action)) {
			return new com.tx.example.nlp._42.TencentLocationProvider("", null).getBinder();
		} else if (ACTION_GEOCODE_V2.equals(action)) {
			return new com.tx.example.nlp._42.TencentGeocodeProvider().getBinder();
		}

		return null;
	}

}
