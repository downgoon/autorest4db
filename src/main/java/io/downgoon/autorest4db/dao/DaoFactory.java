package io.downgoon.autorest4db.dao;

import javax.sql.DataSource;

import com.github.downgoon.jresty.data.orm.dao.CRUDDaoSupport;

public class DaoFactory {

	private DataSourceFactory dataSourceFactory = new DataSourceFactory();
	
	public CRUDDaoSupport<Object> getDao(String databaseName) {
		
		DataSource dataSource = dataSourceFactory.getDataSource(databaseName);
		
		// CRUDDaoSupport<Object> dao = new CRUDDaoSupport<Object>(); // light object
		CRUDDaoSupport<Object> dao = new Sqlite3Dao<Object>(); // light object
		dao.setDataSource(dataSource);
		
		return dao;
		
	}
}
