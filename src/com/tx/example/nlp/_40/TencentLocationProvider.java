package com.tx.example.nlp._40;

import android.location.Criteria;
import android.location.Location;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.WorkSource;

import com.android.location.provider.LocationProvider;
import com.tx.example.nlp.Dbg;


public class TencentLocationProvider extends LocationProvider {
	private static final String TAG = TencentLocationProvider.class.getSimpleName();

	@Override
	public void onAddListener(int arg0, WorkSource arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisable() {
		Dbg.i(TAG, "on disable");
	}

	@Override
	public void onEnable() {
		// TODO Auto-generated method stub
		Dbg.i(TAG, "on enable");
	}

	@Override
	public void onEnableLocationTracking(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int onGetAccuracy() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String onGetInternalState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onGetPowerRequirement() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int onGetStatus(Bundle arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long onGetStatusUpdateTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean onHasMonetaryCost() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onMeetsCriteria(Criteria arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onRemoveListener(int arg0, WorkSource arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onRequiresCell() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onRequiresNetwork() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onRequiresSatellite() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onSendExtraCommand(String arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onSetMinTime(long arg0, WorkSource arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSupportsAltitude() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onSupportsBearing() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onSupportsSpeed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onUpdateLocation(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdateNetworkState(int arg0, NetworkInfo arg1) {
		// TODO Auto-generated method stub
		
	}


}
