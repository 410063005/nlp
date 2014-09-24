package com.tx.example.nlp.ui;

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

public class TestGeocodeActivity extends Activity {

	private EditText mEtName;
	private TextView mTvStatus;
	private Geocoder mGeocoder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_geocode);

		mGeocoder = new Geocoder(this);
		mEtName = (EditText) findViewById(R.id.lat);
		mTvStatus = (TextView) findViewById(R.id.status);
	}

	public void onClick(View view) {
		String name = mEtName.getText().toString();
		if (TextUtils.isEmpty(name)) {
			mEtName.setError("不能为空");
			return;
		}

		mTvStatus.setText("");
		new GeoTask().execute(name);
	}

	class GeoTask extends AsyncTask<String, Void, List<Address>> {

		@Override
		protected List<Address> doInBackground(String... params) {
			try {
				return mGeocoder.getFromLocationName(params[0], 3);
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
