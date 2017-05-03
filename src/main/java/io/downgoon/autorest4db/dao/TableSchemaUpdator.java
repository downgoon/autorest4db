package io.downgoon.autorest4db.dao;

import java.sql.Connection;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * update/alter table schema in given database
 */
public class TableSchemaUpdator extends FlyingConn {

	private static final Logger LOG = LoggerFactory.getLogger(TableSchemaUpdator.class);

	/**
	 * update/alter table schema in given database
	 * 
	 * @param dbName
	 *            database name
	 * @param sql
	 *            SQL statement (e.g. CREATE TABLE ... )
	 */
	public void alterTableSchemas(String dbName, String sql) throws Exception {

		LOG.debug("updating schema on : {}, {}", dbName, sql);
		Connection conn = getConnection(dbName);
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);

		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		}

	}

	
}
