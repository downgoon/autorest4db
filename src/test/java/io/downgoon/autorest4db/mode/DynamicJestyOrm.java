package io.downgoon.autorest4db.mode;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import com.github.downgoon.jresty.data.orm.dao.CRUDDaoSupport;

import io.downgoon.autorest4db.mode.DynamicBean;
import io.downgoon.autorest4db.mode.DynamicClass;

public class DynamicJestyOrm {

	public static void main(String[] args) throws Exception {

		// datasource
		DataSource datasource = getDataSourceSimple();
		
		// dao
		CRUDDaoSupport<Object> dao = new CRUDDaoSupport<Object>();
		dao.setDataSource(datasource);

		/*
		CREATE TABLE `student` (
			`id`	INTEGER,
			`name`	TEXT NOT NULL,
			`age`	NUMERIC
			PRIMARY KEY(`id`)
		);
		 * */
		
		
		// dynamic bean
		DynamicClass studentClass = new DynamicClass("io.downgoon.dynamic.Student", 
				new String[] {"id", "name", "age"}, new Class<?>[] {Integer.class, String.class, Integer.class});
		
		DynamicBean studentInstance = studentClass.newInstance(new Object[] {2, "陈六子", 45});
		
		// save object
		dao.saveObject(studentInstance.getBean());  // get real object
		
		
		DynamicBean studentCondi = new DynamicBean(studentClass);
		studentCondi.setProperty("name", "陈六子");
		Object studentFound = dao.findObject(studentCondi.getBean());
		
		DynamicBean studentBean = new DynamicBean(studentFound);
		System.out.println("age: " + studentBean.getProperty("age"));

	}

	static DataSource getDataSourceSimple() {
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		dataSource.setDriverClass(org.sqlite.JDBC.class);
		dataSource.setUrl("jdbc:sqlite:student.db");
		return dataSource;

	}
}
