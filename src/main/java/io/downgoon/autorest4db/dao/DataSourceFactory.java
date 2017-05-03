package io.downgoon.autorest4db.dao;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import com.github.downgoon.jresty.commons.utils.concurrent.ConcurrentResourceContainer;
import com.github.downgoon.jresty.commons.utils.concurrent.ResourceLifecycle;

public class DataSourceFactory {
	
	private ConcurrentResourceContainer<DataSource> container = new ConcurrentResourceContainer<>(
			
			new ResourceLifecycle<DataSource>() {

				@Override
				public DataSource buildResource(String databaseName) throws Exception {
					SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
					dataSource.setDriverClass(org.sqlite.JDBC.class);
					
					dataSource.setUrl(String.format("jdbc:sqlite:%s.db", databaseName));
					return dataSource;
				}

				@Override
				public void destoryResource(String databaseName, DataSource resource) throws Exception {
					
				}
				
				
			});
	
	
	public DataSource getDataSource(String databaseName) {
		DataSource ds = null;
		try {
			ds = container.getResource(databaseName);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		if (ds == null) {
			throw new IllegalStateException("database not found: " + databaseName);
		}
		return ds;
	}

}
