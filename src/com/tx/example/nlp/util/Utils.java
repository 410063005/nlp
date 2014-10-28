package com.tx.example.nlp.util;

import android.location.Location;
import android.location.LocationManager;

import com.tencent.map.geolocation.TencentLocation;

public class Utils {

	public static Location from(TencentLocation location) {
		Location l = new Location(LocationManager.NETWORK_PROVIDER);
		l.setLatitude(location.getLatitude());
		l.setLongitude(location.getLongitude());
		l.setAltitude(location.getAltitude());
		l.setAccuracy(location.getAccuracy());
		l.setTime(location.getTime());
		return l;
	}
}
