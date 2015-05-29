package com.rightutils.rightutils.entities;

import com.rightutils.rightutils.db.ColumnAutoInc;
import com.rightutils.rightutils.db.TableName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Anton Maniskevich on 8/18/14.
 */
@TableName("company")
public class Company implements Serializable {

	@ColumnAutoInc
	private long id;
	private String name;
	private Date date;

	public Company() {
	}

	public Company(String name) {
		this.name = name;
	}

	public Company(long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Company(long id, String name, Date date) {
		this.id = id;
		this.name = name;
		this.date = date;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Company company = (Company) o;

		if (id != company.id) return false;
		if (name != null ? !name.equals(company.name) : company.name != null) return false;
		return !(date != null ? !date.equals(company.date) : company.date != null);

	}

	@Override
	public int hashCode() {
		int result = (int) (id ^ (id >>> 32));
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (date != null ? date.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Company{" +
				"id=" + id +
				", name='" + name + '\'' +
				", date=" + date +
				'}';
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
