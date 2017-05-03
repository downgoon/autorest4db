package io.downgoon.autorest4db;

import java.io.File;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.sqlite.SQLiteDataSource;

import com.github.downgoon.jresty.data.orm.dao.CRUDDao;

import io.downgoon.autorest4db.dao.Sqlite3Dao;

/**
 * Hello world!
 *
 */
public class JestyOrmHello {

	public static void main(String[] args) throws Exception {

		// datasource
		DataSource datasource = getDataSourceSimple();
		
		// DataSource datasource = getDataSourceSqlite();

		// dao
		// CRUDDaoSupport<Object> dao = new CRUDDaoSupport<Object>();
		
		Sqlite3Dao<Object> dao = new Sqlite3Dao<Object>(); 
		dao.setDataSource(datasource);

		// find
		Employee condi = new Employee();
		condi.setEid(3);
		Employee found = (Employee) dao.findObject(condi);
		System.out.println("found: " + found);

		// new object
		Employee e = new Employee();
		// e.setEid(63);
		e.setAge(45);
		e.setName("陈六子");

		// INSERT INTO employee( `name`,`age`) VALUES ('陈六子',45)
		// Caused by: java.sql.SQLException: not implemented by SQLite JDBC
		// driver
		// 无法返回自增 ID
		dao.saveObject(e);

	}

	static DataSource getDataSourceSimple() {

		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		dataSource.setDriverClass(org.sqlite.JDBC.class);

		String dbFile = System.getProperty("java.io.tmpdir") + File.separator + "hello.db";
		dataSource.setUrl("jdbc:sqlite:" + dbFile);
		dataSource.setUrl("jdbc:sqlite:default.db");
		return dataSource;

	}

    static DataSource getDataSourceSqlite() {
		SQLiteDataSource dataSource = new SQLiteDataSource();
		// String dbFile = System.getProperty("java.io.tmpdir") + File.separator
		// + "hello.db";
		// dataSource.setUrl("jdbc:sqlite:" + dbFile);
		dataSource.setUrl("jdbc:sqlite:default.db");
		return dataSource;

	}

}
