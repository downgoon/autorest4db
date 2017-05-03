package io.downgoon.autorest4db;

import com.github.downgoon.jresty.data.orm.annotation.ORMField;

public class Employee {

	/*
	
	CREATE TABLE `employee` (
	`eid`	INTEGER PRIMARY KEY AUTOINCREMENT,
	`name`	TEXT NOT NULL,
	`age`	INTEGER,
	`credit` NUMERIC
	);
	
	 * */
	
	private Integer eid;
	
	private String name;
	
	private Integer age;
	
	private Float credit;

	
//	@ORMField(name = "eid", isKey = true, isAutoIncrement = false)
	@ORMField(name = "eid", isKey = true, isAutoIncrement = true)
	public Integer getEid() {
		return eid;
	}

	public void setEid(Integer eid) {
		this.eid = eid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	
	
	public Float getCredit() {
		return credit;
	}

	public void setCredit(Float credit) {
		this.credit = credit;
	}

	@Override
	public String toString() {
		return "Employee [" + 
						"eid=" + eid + ", name=" + name + ", age=" + age + ", credit=" + credit + 
				"]";
	}

	
}
