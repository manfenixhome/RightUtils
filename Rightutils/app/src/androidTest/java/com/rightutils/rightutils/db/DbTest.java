package com.rightutils.rightutils.db;

import android.test.AndroidTestCase;

import com.rightutils.rightutils.entities.Company;

/**
 * Created by Anton Maniskevich on 2/23/15.
 */
public class DbTest extends AndroidTestCase {

	private DBUtils dbUtils;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dbUtils = DBUtils.newInstance(getContext(), "example_test.sqlite", 1);
		assertNotNull(dbUtils.db);
	}

	public void testAddEntity() throws Exception {
		long companyId = 100;
		Company company = new Company(companyId, "Google");
		assertEquals(dbUtils.add(company), companyId);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		dbUtils.getDbHandler().deleteDataBase();
	}
}
