package io.downgoon.autorest4db.dao;

public class TableSchemaUpdatorDemo {

	public static void main(String[] args) throws Exception {
		TableSchemaUpdator updator = new TableSchemaUpdator();
		
		
		String sql = "CREATE TABLE `new_table` (" + "\r\n" +
						"`id`    INTEGER," + "\r\n" + 
						"`name`    TEXT NOT NULL," + "\r\n" + 
						"`age`    INTEGER," + "\r\n" + 
						"PRIMARY KEY(`id`)" + "\r\n" + 
					");";

		updator.alterTableSchemas("default", sql);
		
	}

}
