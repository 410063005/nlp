package com.tx.example.nlp.util;

import com.tencent.map.geolocation.exp.AppContext;
import com.tencent.map.geolocation.info.TxCellInfo;
import com.tx.example.nlp.BaseTestCase;

public class UtilsTest extends BaseTestCase {

	public void testCreateMappingData() {
		TxCellInfo cellInfo = TxCellInfo.newInstance(AppContext.getInstance(mContext));
		String res = Utils.createMappingData(mContext, createLocation(), getScanResults(), cellInfo);
		System.out.println(res);
	}

}
