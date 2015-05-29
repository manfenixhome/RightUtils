package com.rightutils.rightutils.db;

import android.test.AndroidTestCase;

import com.rightutils.rightutils.collections.RightList;
import com.rightutils.rightutils.entities.Company;
import com.rightutils.rightutils.entities.Worker;

/**
 * Created by Anton Maniskevich on 2/23/15.
 */
public class DbTest extends AndroidTestCase {

	private DBUtilsNew dbUtils;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dbUtils = DBUtilsNew.newInstance(getContext(), "example_test.sqlite", 1);
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

	public void testAddEntityWithNullDate() throws Exception {
		Company company = new Company(100, "Company name", null);

		dbUtils.add(company);
		RightList<Company> dbCompanies = dbUtils.getAll(Company.class);

		assertEquals(1, dbCompanies.size());
		assertEquals(company, dbCompanies.getFirst());
	}

	public void testAddEntityAutoInc() throws Exception {
		Company company = new Company("Company name");

		dbUtils.add(company);
		dbUtils.add(company);
		RightList<Company> dbCompanies = dbUtils.getAll(Company.class);

		assertEquals(2, dbCompanies.size());
		assertEquals(new Company(1, "Company name"), dbCompanies.get(0));
		assertEquals(new Company(2, "Company name"), dbCompanies.get(1));
	}

	public void testAddList() throws Exception {
		RightList<Company> companies = RightList.asRightList(new Company(100, "Company name 1"), new Company(101, "Company name 2"));

		dbUtils.add(companies);
		RightList<Company> dbCompanies = dbUtils.getAll(Company.class);

		assertEquals(2, dbCompanies.size());
		assertTrue(companies.containsAll(dbCompanies));
	}

	public void testGetAllWhere() throws Exception {
		Company company = new Company(101, "Company name 2");
		RightList<Company> companies = RightList.asRightList(new Company(100, "Company name 1"), company);
		dbUtils.add(companies);

		RightList<Company> dbCompanies = dbUtils.getAllWhere("id = 101", Company.class);
		assertEquals(1, dbCompanies.size());
		assertEquals(company, dbCompanies.getFirst());
	}

	public void testDeleteAll() throws Exception {
		RightList<Company> companies = RightList.asRightList(new Company(100, "Company name 1"), new Company(101, "Company name 2"));

		dbUtils.add(companies);
		dbUtils.deleteAll(Company.class);
		RightList<Company> dbCompanies = dbUtils.getAll(Company.class);

		assertTrue(dbCompanies.isEmpty());
	}

	public void testDaoSimple() throws Exception {
		Worker worker = new Worker(1, 25, "Joel", true, new Company(100, "Company name 1"));
		dbUtils.add(worker);
		RightList<Worker> dbWorkers = dbUtils.getAll(Worker.class);

		assertEquals(1, dbWorkers.size());
		assertEquals(worker, dbWorkers.getFirst());
	}
}
