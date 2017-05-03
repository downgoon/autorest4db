import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class SqliteJdbcAutoIncr {

	public static void main(String[] args) throws Exception {
		Class.forName("org.sqlite.JDBC");
		// Connection conn = DriverManager.getConnection("jdbc:sqlite:auto_incr.db");
		Connection conn = DriverManager.getConnection("jdbc:sqlite:default.db");

		DatabaseMetaData dbmeta = conn.getMetaData();

		// Listing Tables
		String catalog = null;
		String schemaPattern = null;
		String tableNamePattern = null;
		String[] types = null;

		ResultSet tables = dbmeta.getTables(catalog, schemaPattern, tableNamePattern, types);
		while (tables.next()) { // for each table

			final String tableName = tables.getString(3);

			// Listing Columns in a Table
			String columnNamePattern = null;
			ResultSet columns = dbmeta.getColumns(catalog, schemaPattern, tableName, columnNamePattern);

			System.out.println("table: " + tableName);
			System.out.println("----------");
			
			while (columns.next()) {
				String columnName = columns.getString(4);
				// int columnType = columns.getInt(5);

				boolean isAutoIncrement = columns.getBoolean("IS_AUTOINCREMENT");
				String autoIncString = columns.getString("IS_AUTOINCREMENT");

				System.out.println(
						String.format("\tcol: %s isAutoIncrement: %s %s", columnName, isAutoIncrement, autoIncString));
			}

			System.out.println();
			
			String schema = null;
			ResultSet keys = dbmeta.getPrimaryKeys(catalog, schema, tableName);
			while (keys.next()) {
				String keyName = keys.getString(4); // COLUMN_NAME
				int keySeq = keys.getInt("KEY_SEQ");
				
				System.out.println("primary key: " + keyName + ", " + keySeq);
			}


		}

	}

}
