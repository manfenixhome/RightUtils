package com.rightutils.rightutils.entities;

import com.rightutils.rightutils.db.ColumnDAO;

/**
 * Created by Anton Maniskevich on 2/23/15.
 */
public class Worker {

	private long id;
	private int age;
	private String name;
	private boolean gender;
	@ColumnDAO
	private Company company;

	public Worker() {
	}

	public Worker(long id, int age, String name, boolean gender, Company company) {
		this.id = id;
		this.age = age;
		this.name = name;
		this.gender = gender;
		this.company = company;
	}

	@Override
	public String toString() {
		return "Worker{" +
				"id=" + id +
				", age=" + age +
				", name='" + name + '\'' +
				", gender=" + gender +
				", company=" + company +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Worker worker = (Worker) o;

		if (age != worker.age) return false;
		if (gender != worker.gender) return false;
		if (id != worker.id) return false;
		if (company != null ? !company.equals(worker.company) : worker.company != null)
			return false;
		if (name != null ? !name.equals(worker.name) : worker.name != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = (int) (id ^ (id >>> 32));
		result = 31 * result + age;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (gender ? 1 : 0);
		result = 31 * result + (company != null ? company.hashCode() : 0);
		return result;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isGender() {
		return gender;
	}

	public void setGender(boolean gender) {
		this.gender = gender;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
}
