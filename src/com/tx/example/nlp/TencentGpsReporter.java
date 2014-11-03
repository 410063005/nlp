package com.tx.example.nlp;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class TencentGpsReporter implements LocationListener {

	@Override
	public void onLocationChanged(Location location) {
		if (location != null
				&& LocationManager.GPS_PROVIDER.equals(location.getProvider())) {
			addToCache(location);
		}
	}

	private void addToCache(Location location) {
		// TODO Auto-generated method stub

	}

	// //// ignore methods
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// ignore
	}

	@Override
	public void onProviderEnabled(String provider) {
		// ignore
	}

	@Override
	public void onProviderDisabled(String provider) {
		// ignore
	}
	// //// ignore methods
}
