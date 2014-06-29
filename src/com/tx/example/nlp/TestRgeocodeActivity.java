package com.tx.example.nlp;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.location.R;

public class TestRgeocodeActivity extends Activity {

	private Geocoder mGeocoder;
	private EditText mEtLat;
	private EditText mEtLng;
	private TextView mTvStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_rgeocode);

		mEtLat = (EditText) findViewById(R.id.lat);
		mEtLng = (EditText) findViewById(R.id.lng);
		mTvStatus = (TextView) findViewById(R.id.status);

		mGeocoder = new Geocoder(this);
	}

	public void onClick(View view) {
		if (TextUtils.isEmpty(mEtLat.getText().toString())) {
			mEtLat.setError("不能为空");
			return;
		}

		if (TextUtils.isEmpty(mEtLng.getText().toString())) {
			mEtLng.setError("不能为空");
			return;
		}

		double lat = 0;
		double lng = 0;
		lat = Double.valueOf(mEtLat.getText().toString());
		lng = Double.valueOf(mEtLng.getText().toString());

		if (lat < -90 || lat > 90) {
			mEtLat.setError("非法纬度");
			return;
		}

		if (lng < -180 || lng > 180) {
			mEtLng.setError("非法经度");
			return;
		}

		mTvStatus.setText("");
		new RgeoTask().execute(lat, lng);
	}

	class RgeoTask extends AsyncTask<Double, Void, List<Address>> {

		@Override
		protected List<Address> doInBackground(Double... params) {
			try {
				return mGeocoder.getFromLocation(params[0], params[1], 3);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<Address> result) {
			super.onPostExecute(result);

			if (result == null) {
				mTvStatus.append("null\n");
			} else {
				for (Address address : result) {
					mTvStatus.append(address.getAddressLine(0) + "\n");
				}
			}
		}

	}
}
