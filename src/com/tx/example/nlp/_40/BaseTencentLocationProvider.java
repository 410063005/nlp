package com.tx.example.nlp._40;

import android.location.Criteria;
import android.os.Bundle;

import com.android.location.provider.LocationProvider;

/**
 * 实现某些行为确定的回调方法，减少子类的复杂性
 *
 */
public abstract class BaseTencentLocationProvider extends LocationProvider {

	public BaseTencentLocationProvider() {
		super();
	}

	@Override
	public int onGetAccuracy() {
		return Criteria.ACCURACY_FINE; // 准确度较好
	}

	@Override
	public int onGetPowerRequirement() {
		return Criteria.POWER_LOW; // 低耗时
	}

	@Override
	public boolean onHasMonetaryCost() {
		return false; // trick, 网络定位一般会耗流量从而引起资费, 但认为资费相当小
	}

	@Override
	public boolean onMeetsCriteria(Criteria criteria) {
		return true; // trick, 认为我们可以满足任何条件
	}

	@Override
	public boolean onRequiresCell() {
		return true; // 需要 cell
	}

	@Override
	public boolean onRequiresNetwork() {
		return true; // 需要 network
	}

	@Override
	public boolean onRequiresSatellite() {
		return false; // 不需要卫星
	}

	@Override
	public boolean onSendExtraCommand(String arg0, Bundle arg1) {
		return false; // 不支持 extra command
	}

	@Override
	public boolean onSupportsAltitude() {
		return false; // 不支持海拔
	}

	@Override
	public boolean onSupportsBearing() {
		return false; // 不支持方向
	}

	@Override
	public boolean onSupportsSpeed() {
		return false; // 不支持速度
	}

}
