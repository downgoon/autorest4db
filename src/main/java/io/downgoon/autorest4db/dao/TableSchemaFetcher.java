package io.downgoon.autorest4db.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.downgoon.autorest4db.mode.SqlTypeClass;
import io.downgoon.autorest4db.mode.TableSchema;

/**
 * create a connection and pull metadata from database (no cache)
 */
public class TableSchemaFetcher extends FlyingConn {

	private static final Logger LOG = LoggerFactory.getLogger(TableSchemaFetcher.class);

	public List<TableSchema> getTableSchemas(String dbName) throws Exception {

		List<TableSchema> schemas = new ArrayList<TableSchema>();

		LOG.debug("table schema loading: {}.*", dbName);
		Connection conn = getConnection(dbName);

		try {
			// http://tutorials.jenkov.com/jdbc/databasemetadata.html
			DatabaseMetaData dbmeta = conn.getMetaData();

			// Listing Tables
			String catalog = null;
			String schemaPattern = null;
			String tableNamePattern = null;
			String[] types = null;

			ResultSet tables = dbmeta.getTables(catalog, schemaPattern, tableNamePattern, types);
			while (tables.next()) { // for each table

				final String tableName = tables.getString(3);

				if ("sqlite_sequence".equalsIgnoreCase(tableName)) {
					LOG.debug("table schema skip: {}.sqlite_sequence", dbName);
					continue; // skip system table
				}

				final TableSchema tableSchema = new TableSchema(tableName);

				// Primary Key for Table
				List<PriKey> priKeyList = new ArrayList<PriKey>();
				String schema = null;
				ResultSet keys = dbmeta.getPrimaryKeys(catalog, schema, tableName);
				while (keys.next()) {
					// ISSUE#219
					// https://github.com/xerial/sqlite-jdbc/issues/219
					PriKey key = new PriKey(normName(keys.getString(4)), keys.getInt("KEY_SEQ"));
					priKeyList.add(key);
				}
				Collections.sort(priKeyList);

				for (PriKey priKey : priKeyList) {
					tableSchema.addPrimaryKey(priKey.keyName);
				}

				// Listing Columns in a Table
				String columnNamePattern = null;
				ResultSet columns = dbmeta.getColumns(catalog, schemaPattern, tableName, columnNamePattern);

				// ISSUE#218
				// https://github.com/xerial/sqlite-jdbc/issues/218
				while (columns.next()) {
					String columnName = normName(columns.getString(4));
					int columnType = columns.getInt(5);
					tableSchema.addProperty(columnName, SqlTypeClass.toClass(columnType));

					if ("YES".equalsIgnoreCase(columns.getString("IS_AUTOINCREMENT"))) {
						tableSchema.autoIncr(); // always executed 0 or 1 time
					} else {
						// Quick&Dirty: 约定只要名字为id，并且是整数类型，而且是主键，则当自增处理
						if ("id".equalsIgnoreCase(columnName)
								&& (columnType == Types.INTEGER || columnType == Types.BIGINT)
								&& tableSchema.getPrimaryKeys().contains(columnName)) {
							tableSchema.autoIncr();
						}
					}
				}

				schemas.add(tableSchema); // one table schema

				LOG.debug("table schema loaded: {}.{} = {}", dbName, tableName, tableSchema);
			}

		} finally {
			if (conn != null) {
				conn.close();
			}
		}

		return schemas;

	}

	private class PriKey implements Comparable<PriKey> {
		String keyName;
		int keySeq;

		PriKey(String keyName, int keySeq) {
			this.keyName = keyName;
			this.keySeq = keySeq;
		}

		@Override
		public int compareTo(PriKey o) {
			return keySeq - o.keySeq;
		}
	}

	private static String normName(String nameInDataSpace) {
		String nameInJava = nameInDataSpace.toLowerCase();
		if (nameInJava.startsWith("`") && nameInJava.endsWith("`")) {
			return nameInJava.substring(1, nameInJava.length() - 1);
		}
		return nameInJava;
	}

}
