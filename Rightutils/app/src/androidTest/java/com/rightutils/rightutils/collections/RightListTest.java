package com.rightutils.rightutils.collections;

import android.test.AndroidTestCase;

import com.rightutils.rightutils.entities.Company;

/**
 * Created by Anton Maniskevich on 8/18/14.
 */
public class RightListTest extends AndroidTestCase {

	public void testCreateEmptyRightList() throws Exception {
		RightList<Company> companies = new RightList<Company>();
		assertTrue(companies.isEmpty());
	}

	public void testCreateRightListWithElements() throws Exception {
		RightList<Company> companies = RightList.asRightList(new Company(1,"company 1"), new Company(2, "company 2"));
		assertFalse(companies.isEmpty());
		assertEquals(2, companies.size());
	}

	public void testFilterRightList() throws Exception {
		RightList<Company> companies = RightList.asRightList(new Company(1,"company 1"), new Company(2, "company 2"));
		RightList<Company> result = companies.filter(new Predicate<Company>() {
			@Override
			public boolean apply(Company value) {
				return value.getId() == 1;
			}
		});
		assertEquals(1, result.size());
		assertEquals(new Company(1, "company 1"), result.get(0));
	}

	public void testMapRightList() throws Exception {
		RightList<Company> companies = RightList.asRightList(new Company(1,"company 1"), new Company(2, "company 2"));

		RightList<Long> result = companies.map(new Mapper<Long, Company>() {
			@Override
			public Long apply(Company value) {
				return value.getId();
			}
		});

		RightList<Long> expectedResult = RightList.asRightList(1l, 2l);
		assertEquals(2, result.size());
		assertTrue(expectedResult.containsAll(result));
	}

	public void testFindBy() throws Exception {
		RightList<Company> companies = RightList.asRightList(
				new Company(1,"company 1"),
				new Company(2,"company 2"),
				new Company(3,"company 3")
		);
		Company company = companies.findBy(new Predicate<Company>() {
			@Override
			public boolean apply(Company value) {
				return "company 2".equals(value.getName());
			}
		});
		assertEquals(new Company(2, "company 2"), company);
	}

	public void testReduceSimple() throws Exception {
		RightList<Company> companies = RightList.asRightList(
				new Company(1,"company 1"),
				new Company(2,"company 2"),
				new Company(3,"company 3")
		);

		String result = companies.reduce("", new Function<String, Company>() {
			@Override
			public String apply(String prevResult, Company value) {
				if ("".equals(prevResult)) {
					return value.getName();
				}
				return prevResult + ", " + value.getName();
			}
		});
		assertEquals("company 1, company 2, company 3", result);
	}
}
