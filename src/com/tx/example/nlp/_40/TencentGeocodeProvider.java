package com.tx.example.nlp._40;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.GeocoderParams;

import com.tencent.tencentmap.mapsdk.map.GeoPoint;
import com.tencent.tencentmap.mapsdk.search.GeocoderSearch;
import com.tencent.tencentmap.mapsdk.search.PoiItem;
import com.tencent.tencentmap.mapsdk.search.PoiResults;
import com.tencent.tencentmap.mapsdk.search.PoiSearch;
import com.tencent.tencentmap.mapsdk.search.ReGeocoderResult;
import com.tencent.tencentmap.mapsdk.search.ReGeocoderResult.ReGeocoderAddress;
import com.tx.example.nlp.BaseTencentGeocodeProvider;
import com.tx.example.nlp.Debug;

public class TencentGeocodeProvider extends BaseTencentGeocodeProvider {

	private static final String TAG = TencentGeocodeProvider.class
			.getSimpleName();
	private final Context mContext;
	private GeocoderSearch mGeocoder;
	private PoiSearch mPoiSearch;

	public TencentGeocodeProvider(Context context) {
		super();
		mContext = context;
		Debug.i(TAG, "geocode provider created");
	}

	@Override
	public String onGetFromLocation(double lat, double lng, int maxResult,
			GeocoderParams arg3, List<Address> addresses) {
		// TODO 检查网络

		if (maxResult < 0) {
			return null;
		}
		Debug.i(TAG, "thead: " + Thread.currentThread().getName());
		Debug.i(TAG, "(" + lat + "," + lng + ")");
		Debug.i(TAG, "max results: " + maxResult);
		GeoPoint geoRegeocoder = new GeoPoint((int) (lat * 1e6),
				(int) (lng * 1e6));
		ReGeocoderResult result = null;
		try {
			result = getGeocoderSearch().searchFromLocation(geoRegeocoder);
		} catch (Exception e) {
			e.printStackTrace();
			result = null;
		}

		try {
			parseGeocodeResult(addresses, result);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null; // 不管成功或失败均返回 null, 保证系统回调时结果正常
		// return super.onGetFromLocation(arg0, arg1, arg2, arg3, arg4);
	}

	private GeocoderSearch getGeocoderSearch() {
		if (mGeocoder == null) {
			mGeocoder = new GeocoderSearch(mContext);
		}
		return mGeocoder;
	}

	private void parseGeocodeResult(List<Address> addresses,
			ReGeocoderResult result) {
		if (result == null) {
			return;
		}

		Debug.i(TAG, "geo result: " + result.addresslist);
		Debug.i(TAG, "geo result: " + result.poilist);

		if (result != null && result.addresslist != null
				&& result.addresslist.size() > 0) {
			ReGeocoderAddress addr = result.addresslist.get(0);
			PoiItem poi = result.poilist != null && result.poilist.size() > 0 ? result.poilist
					.get(0) : null;

			Address mAddress = new Address(Locale.getDefault());
			mAddress.setLatitude(addr.point.getLatitudeE6() / 1E6);
			mAddress.setLongitude(addr.point.getLongitudeE6() / 1E6);
			mAddress.setAddressLine(0, poi != null ? poi.address : addr.name);

			String countryName = null;
			String adminArea = null;
			String locality = null;
			String subLocality = null;

			String[] strArray = addr.name.split(",");
			if (strArray.length >= 3) {
				countryName = strArray[0];
				adminArea = strArray[1];
				locality = strArray[1];
				subLocality = strArray[2];
			} else if (strArray.length >= 4) {
				countryName = strArray[0];
				adminArea = strArray[1];
				locality = strArray[2];
				subLocality = strArray[3];
			}

			mAddress.setCountryName(countryName);
			mAddress.setAdminArea(adminArea);
			mAddress.setLocality(locality);
			mAddress.setSubLocality(subLocality);
			// mAddress.setThoroughfare(mGeoCode.mPoiDetail.mstrStreet);
			// mAddress.setCountryCode(mGeoCode.mPoiDetail.mstrCityCode);

			mAddress.setPostalCode(addr.adcode);

			if (result.poilist != null) {
				int i = 1;
				for (PoiItem p : result.poilist) {
					mAddress.setAddressLine(i, p.address);
					i++;
				}
			}

			addresses.add(mAddress);
		}
	}

	@Override
	public String onGetFromLocationName(String locationName,
			double lowerLeftLatitude, double lowerLeftLongitude,
			double upperRightLatitude, double upperRightLongitude,
			int maxResults, GeocoderParams params, List<Address> addresses) {
		if (maxResults < 0) {
			return null;
		}
		// FIXME 强制在中国大陆内搜索
		lowerLeftLatitude = 3;
		lowerLeftLongitude = 74;
		upperRightLatitude = 53;
		upperRightLongitude = 163;

		Debug.i(TAG, "thead: " + Thread.currentThread().getName());
		Debug.i(TAG, "max results: " + maxResults);
		Debug.i(TAG, "location name: " + locationName);
		Debug.i(TAG, "(" + lowerLeftLatitude + "," + lowerLeftLongitude + "), ("
				+ upperRightLatitude + "," + upperRightLongitude + ")");

		GeoPoint lowerLeft = new GeoPoint((int) (lowerLeftLatitude * 1E6),
				(int) (lowerLeftLongitude * 1E6));

		GeoPoint upperRight = new GeoPoint((int) (upperRightLatitude * 1E6),
				(int) (upperRightLongitude * 1E6));
		PoiResults result = null;
		try {
			result = getPoiSearch().searchPoiInBound(locationName, lowerLeft,
					upperRight);
		} catch (Exception e) {
			e.printStackTrace();
			result = null;
		}

		try {
			parsePoiResult(result, addresses);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private PoiSearch getPoiSearch() {
		if (mPoiSearch == null) {
			mPoiSearch = new PoiSearch(mContext);
		}
		return mPoiSearch;
	}

	private void parsePoiResult(PoiResults result, List<Address> addresses) {
		if (result == null) {
			return;
		}

		Debug.i(TAG, "poi result: " + result.getCurrentPagePoiItems());
		if (result != null && result.getCurrentPagePoiItems() != null) {
			List<PoiItem> items = result.getCurrentPagePoiItems();
			for (PoiItem item : items) {
				Address address = new Address(Locale.getDefault());
				address.setLatitude(item.point.getLatitudeE6() / 1E6);
				address.setLongitude(item.point.getLongitudeE6() / 1E6);
				address.setAddressLine(0, item.address);
				address.setFeatureName(item.classes);
				address.setPhone(item.phone);
				addresses.add(address);
			}
		}
	}
}
