package com.tx.example.nlp;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.tencent.map.geolocation.util.Cells;
import com.tencent.map.geolocation.util.Wifis;
import com.tx.example.nlp.util.Utils;

/**
 * 用于上报 GPS 信息. 流程如下:
 *
 * <p>
 * <ol>
 * <li>TencentGpsReporter 挂到 PASSIVE_PROVIDER, 监听是否有 GPS
 * <li>onLocationChanged回调时, 1. 记录GPS Location 2.记录基站 3.记录Wifi热点(可选)
 * <li>组合上述3种信息并加入缓存
 * <li>收到ACTION_UPLOAD广播后, 上传缓存数据
 * </ol>
 *
 * @author kingcmchen
 *
 */
public class TencentGpsReporter implements LocationListener {
	private static final String TAG = "TencentGpsReporter";
	private static final boolean DEBUG = false;
	private static final int MSG_ID_ADD_TO_CACHE = 1;

	private final class WifiReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (null == intent) {
				return;
			}

			String action = intent.getAction();
			if (DEBUG) Log.i(TAG, "Wifi scan finish: " + action);
			if (check(action) && mLastInfo.location != null) {
				// 记录Wifi热点(可选)
				mLastInfo.wifis = Wifis.getScanResultsQuietly(mWifiManager);

				Message msg = mHandler.obtainMessage(MSG_ID_ADD_TO_CACHE);
				msg.obj = new HybridInfo(mLastInfo);
				msg.sendToTarget();
				mLastInfo.location = null;
			}
		}

		private boolean check(String action) {
			return WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action);
		}
	}

	private static final String ACTION_UPLOAD = "com.tencent.location.ACTION_REPORT";

	private final Context mContext;
	private final WifiManager mWifiManager;
	private final TelephonyManager mTelManager;

	private final HandlerThread mWorker;
	private final Handler mHandler;
	private final BroadcastReceiver mWifiReceiver;
	private final HybridInfo mLastInfo;

	private final TencentGpsCache mCache;
	
	private boolean mShutdown = false;

	public TencentGpsReporter(Context context) {
		mContext = context;
		mWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		mTelManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		mWorker = new HandlerThread("worker",
				Process.THREAD_PRIORITY_BACKGROUND);
		mWorker.start();
		mHandler = new Handler(mWorker.getLooper()) {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what == MSG_ID_ADD_TO_CACHE) {

					HybridInfo info = (HybridInfo) msg.obj;
					mCache.add(info.location, info.wifis, info.cells.cell,
							info.cells.neighbours);
					
					if (DEBUG) Log.i(TAG, "Added to cache");
				}
			}
		};
		mWifiReceiver = new WifiReceiver();
		mLastInfo = new HybridInfo();

		mCache = new TencentGpsCache(context);

		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		filter.addAction(ACTION_UPLOAD);
		context.registerReceiver(mWifiReceiver, filter, null, mHandler);
	}

	public void shutdown() {
		if (!mShutdown) {
			mContext.unregisterReceiver(mWifiReceiver);
			mHandler.removeCallbacksAndMessages(null);
			mWorker.quit();
			mShutdown = true;
		}
	}
	
	TencentGpsCache myCache() {
		return mCache;
	}

	@Override
	public void onLocationChanged(Location location) {
		if (location != null
				&& LocationManager.GPS_PROVIDER.equals(location.getProvider())) {
			if (DEBUG) Log.i(TAG, "Gps got " + location);
			HybridInfo info = mLastInfo;
			// 记录GPS Location
			info.location = location;
			// 记录基站
			info.cells = getCurrentCellInfo();

			if (Utils.isScannable(mWifiManager)) {
				// 扫描wifi并等待结果
				Wifis.startScanQuietly(mWifiManager);
				if (DEBUG) Log.i(TAG, "Start wifi scan");
			} else {
				info.wifis = null;

				Message msg = mHandler.obtainMessage(MSG_ID_ADD_TO_CACHE);
				msg.obj = new HybridInfo(info);
				msg.sendToTarget();
			}
		}
	}

	private CellInfo getCurrentCellInfo() {
		CellInfo cellInfo = new CellInfo();
		cellInfo.cell = Cells.getCellLocationQuietly(mTelManager);
		cellInfo.neighbours = Cells.getNeighboringCellInfoQuietly(mTelManager);
		return cellInfo;
	}

	private void doUpload() {
		// TODO wake lock
	}

	private void doUploadInBg() {
		// TODO thread
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

	static class CellInfo {
		CellLocation cell;
		List<NeighboringCellInfo> neighbours;
	}

	static class HybridInfo {
		Location location;
		CellInfo cells;
		List<ScanResult> wifis;

		public HybridInfo() {
			super();
		}

		public HybridInfo(Location location, CellInfo cells,
				List<ScanResult> wifis) {
			super();
			this.location = location;
			this.cells = cells;
			if (wifis != null) {
				this.wifis = new ArrayList<ScanResult>(wifis);
			} else {
				this.wifis = null;
			}
		}

		public HybridInfo(HybridInfo info) {
			this(info.location, info.cells, info.wifis);
		}

	}
}
