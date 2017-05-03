package io.downgoon.autorest4db.dao;

import java.util.List;

import com.github.downgoon.jresty.commons.utils.concurrent.ConcurrentResourceContainer;
import com.github.downgoon.jresty.commons.utils.concurrent.ResourceLifecycle;

import io.downgoon.autorest4db.mode.TableSchema;

public class TableSchemaStorage {

	private TableSchemaFetcher tableSchemaFetcher = new TableSchemaFetcher();

	public TableSchema getTableSchema(String dbName, String tableName) {
		List<TableSchema> schemas;
		try {
			schemas = container.getResource(dbName);
			
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		if (schemas == null) {
			return null;
		}
		for (TableSchema tableSchema : schemas) {
			if (tableSchema.getClassName().equalsIgnoreCase(tableName)) {
				return tableSchema;
			}
		}
		return null;
	}

	
	
	/**
	 * table schema cache container
	 * **/
	private ConcurrentResourceContainer<List<TableSchema>> container = new ConcurrentResourceContainer<>(

	new ResourceLifecycle<List<TableSchema>>() {

		@Override
		public List<TableSchema> buildResource(String dbName) throws Exception {
			// no cache if null
			return tableSchemaFetcher.getTableSchemas(dbName); 
		}

		@Override
		public void destoryResource(String name, List<TableSchema> resource) throws Exception {

		}

	});

}
