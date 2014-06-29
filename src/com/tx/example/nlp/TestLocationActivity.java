package com.tx.example.nlp;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.location.R;

public class TestLocationActivity extends Activity implements LocationListener {

	private LocationManager mLocationManager;
	private TextView mStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_location);
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		mStatus = (TextView) findViewById(R.id.status);

		mStatus.append("nlp enabled: "
				+ mLocationManager.isProviderEnabled("network") + "\n");

		if (!mLocationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			Toast.makeText(this, "网络定位没打开", Toast.LENGTH_SHORT).show();

			try {
				startActivity(new Intent(
						Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			} catch (Exception e) {

			}
		}

		mLocationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 5000, 0, this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mLocationManager.removeUpdates(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onLocationChanged(Location location) {
		if (location == null) {
			mStatus.append("error\n");
			return;
		}
		mStatus.append(location.getProvider() + ": (" + location.getLatitude()
				+ "," + location.getLongitude() + "," + location.getAccuracy()
				+ ")\n");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		mStatus.append(provider + " enabled\n");
	}

	@Override
	public void onProviderDisabled(String provider) {
		mStatus.append(provider + " disabled\n");
	}

}
