package com.tx.example.nlp.util;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.exp.AppContext;
import com.tencent.map.geolocation.info.TxCellInfo;
import com.tencent.map.geolocation.util.SosoLocUtils;

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

	@SuppressLint("NewApi")
	public static boolean isScannable(WifiManager wifiManager) {
		if (wifiManager == null) {
			return false;
		}
		if (Build.VERSION.SDK_INT >= 18) {
			return wifiManager.isScanAlwaysAvailable();
		} else {
			return wifiManager.isWifiEnabled();
		}
	}

	public static String createMappingData(Context context, Location location, List<ScanResult> aps, TxCellInfo cell) {
		final String imei = AppContext.getInstance(context).getAppStatus().getDeviceId();
		final String sep = ",";
		final String itemSep = ";";
		String gpsInfoJson = SosoLocUtils.getGpsInfoWithJoiner(location, sep);
		String wifiInfoJson = SosoLocUtils.getWifiInfoWithJoiner(aps, sep, itemSep);
		String cellInfoJson = SosoLocUtils.getCellInfoWithJoiner(cell, sep, itemSep);

		StringBuilder sb = new StringBuilder();
		sb.append(imei).append("|").append(gpsInfoJson).append("|")
				.append(wifiInfoJson).append("|").append(cellInfoJson)
				.append("\n");
		return sb.toString();
	}
}
