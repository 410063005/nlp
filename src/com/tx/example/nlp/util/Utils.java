package com.tx.example.nlp.util;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;

import com.tencent.map.geolocation.TencentLocation;

public class Utils {

	@SuppressLint("NewApi")
	public static Location from(TencentLocation location) {
		Location l = new Location(LocationManager.NETWORK_PROVIDER);
		l.setLatitude(location.getLatitude());
		l.setLongitude(location.getLongitude());
		l.setAltitude(location.getAltitude());
		l.setAccuracy(location.getAccuracy());
		l.setTime(location.getTime());
		if (Build.VERSION.SDK_INT >= 17) {
			try {
				l.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
			} catch (NoSuchMethodError e) {
				// ignore
			}
		}
		return l;
	}
}
