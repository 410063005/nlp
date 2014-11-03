package com.tx.example.nlp;

import android.location.Location;

import com.android.location.provider.LocationProvider;
import com.android.location.provider.LocationProviderBase;

/**
 * Trick.
 * <p>
 * 这个接口存在的意义在于让 {@linkplain TencentLocationProviderImpl} 可以调用
 * {@link LocationProvider} 或 {@link LocationProviderBase} 的同名方法.
 *
 * @see com.tx.example.nlp._40.TencentLocationProvider
 * @see com.tx.example.nlp._42.TencentLocationProvider
 */
public interface LocationReporter {

	void reportLocation(Location location);
}
