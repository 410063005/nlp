package com.tx.example.nlp;

public class TencentGpsCacheTest extends BaseTestCase {

	private TencentGpsCache mCache;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mCache = new TencentGpsCache(mContext);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		mCache.delete();
	}

	public void testAdd() {
		mCache.add(createLocation(), getScanResults(), null, null);
		assertTrue(mCache.size() > 0);
	}

	public void testSize() {
		assertEquals(0, mCache.size());

		mCache.add(createLocation(), getScanResults(), null, null);
		long size = mCache.size();
		mCache.add(createLocation(), getScanResults(), null, null);
		assertTrue(mCache.size() > size);
	}

	public void testDelete() {
		assertFalse(mCache.delete());

		mCache.add(createLocation(), getScanResults(), null, null);
		assertTrue(mCache.delete());
	}

}
