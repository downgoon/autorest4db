package io.downgoon.autorest4db.dao;

import java.util.List;

import io.downgoon.autorest4db.mode.TableSchema;

public class TableSchemaFetcherDemo {

	public static void main(String[] args) throws Exception {
		TableSchemaFetcher schemaFetcher = new TableSchemaFetcher();
		// List<TableSchema> schemas = schemaFetcher.getTableSchemas("student");
		List<TableSchema> schemas = schemaFetcher.getTableSchemas("default");
		for (TableSchema tableSchema : schemas) {
			System.out.println(tableSchema);
		}
		
	}

}
