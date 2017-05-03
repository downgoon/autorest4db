package io.downgoon.autorest4db.dao;

import java.sql.Connection;
import java.sql.DriverManager;

public abstract class FlyingConn {

	protected Connection getConnection(String dbName) throws Exception {
		Class.forName("org.sqlite.JDBC");
		String url = String.format("jdbc:sqlite:%s.db", dbName);
		Connection conn = DriverManager.getConnection(url);
		return conn;
	}
	
}
