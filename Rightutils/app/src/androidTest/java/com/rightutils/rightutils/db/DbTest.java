package com.rightutils.rightutils.db;

import android.test.AndroidTestCase;

import com.rightutils.rightutils.collections.RightList;
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

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		dbUtils.getDbHandler().deleteDataBase();
	}

	public void testAddEntity() throws Exception {
		Company company = new Company(100, "Company name");

		dbUtils.add(company);
		RightList<Company> dbCompanies = dbUtils.getAll(Company.class);

		assertEquals(1, dbCompanies.size());
		assertEquals(company, dbCompanies.getFirst());
	}

	public void testAddList() throws Exception {
		RightList companies = RightList.asRightList(new Company(100, "Company name 1"), new Company(101, "Company name 2"));

		dbUtils.add(companies);
		RightList<Company> dbCompanies = dbUtils.getAll(Company.class);

		assertEquals(2, dbCompanies.size());
		assertTrue(companies.containsAll(dbCompanies));
	}

	public void testGetAllWhere() throws Exception {
		Company company = new Company(101, "Company name 2");
		RightList companies = RightList.asRightList(new Company(100, "Company name 1"), company);
		dbUtils.add(companies);

		RightList<Company> dbCompanies = dbUtils.getAllWhere("id = 101", Company.class);
		assertEquals(1, dbCompanies.size());
		assertEquals(company, dbCompanies.getFirst());
	}

	public void testDeleteAll() throws Exception {
		RightList companies = RightList.asRightList(new Company(100, "Company name 1"), new Company(101, "Company name 2"));

		dbUtils.add(companies);
		dbUtils.deleteAll(Company.class);
		RightList<Company> dbCompanies = dbUtils.getAll(Company.class);

		assertTrue(dbCompanies.isEmpty());
	}
}
