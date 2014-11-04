package com.tx.example.nlp;

import java.util.List;

import android.content.Context;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.test.AndroidTestCase;

public class BaseTestCase extends AndroidTestCase {
	public Location createLocation() {
		Location loc = new Location("gps");
		loc.setAccuracy(10);
		loc.setAltitude(20);
		loc.setBearing(40);
		loc.setLatitude(38.812345678);
		loc.setLongitude(114.987654321);
		loc.setSpeed(15);
		loc.setTime(System.currentTimeMillis());
		return loc;
	}

	public List<ScanResult> getScanResults() {
		WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		return wifiManager.getScanResults();
	}
}
