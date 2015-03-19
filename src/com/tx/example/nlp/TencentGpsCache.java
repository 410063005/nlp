package com.tx.example.nlp;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.util.Log;

import com.tencent.map.geolocation.exp.AppContext;
import com.tencent.map.geolocation.info.TxCellInfo;
import com.tencent.map.geolocation.util.Files;
import com.tencent.map.geolocation.util.SosoLocUtils;
import com.tx.example.nlp.util.Debug;
import com.tx.example.nlp.util.Utils;

public class TencentGpsCache {

	private static final String TAG = "TencentGpsCache";

	private final static String CACHE_FILE_NAME = "fedcba";

	private final Context mContext;
	private final File mCache;

	public TencentGpsCache(Context context) {
		super();
		this.mContext = context;
		File appDir = context.getFilesDir();
		mCache = new File(appDir, CACHE_FILE_NAME);
	}

	public void add(Location location, List<ScanResult> aps,
			CellLocation cell, List<NeighboringCellInfo> neighbors) {
		TxCellInfo cellInfo = TxCellInfo.newInstance(AppContext.getInstance(mContext));
		if (cellInfo != null) {
			cellInfo.setNeighboringCellInfo(neighbors);
		}

		String result = Utils.createMappingData(mContext, location, aps, cellInfo);
		Debug.i(TAG, "addToCache: " + result);
		byte[] encryted = SosoLocUtils.encryptBytes(result.getBytes());
		// encryted = result.getBytes();
		try {
			Files.append(encryted, mCache);
		} catch (IOException e) {
			Debug.e(TAG, Log.getStackTraceString(e));
		}
	}

	public long size() {
		return mCache.length();
	}

	public boolean delete() {
		return mCache.delete();
	}

	public File getFile() {
		return mCache;
	}
}
