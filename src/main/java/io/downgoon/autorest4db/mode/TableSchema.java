package io.downgoon.autorest4db.mode;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class TableSchema {

	private String className;
	
	private Map<String, Class<?>> propertyTypes = new LinkedHashMap<String, Class<?>>();
	
	// KEY IMPORT: MUST Ordered Set
	private Set<String> primaryKeys = new LinkedHashSet<String>();
	
	/**
	 * indicating the primary key is 'IS_AUTOINCREMENT' or not
	 * */
	private boolean autoIncr = false;
	

	public TableSchema(String className) {
		super();
		this.className = className;
	}
	 
	public void addProperty(String name, Class<?> type) {
		propertyTypes.put(name, type);
	}
	

	public Map<String, Class<?>> getPropertyTypes() {
		return propertyTypes;
	} 
	
	
	public boolean isPrimaryKey(String name) {
		return primaryKeys.contains(name);
	}
	
	public void addPrimaryKey(String name) {
		primaryKeys.add(name);
	}
	
	/**
	 * Ordered Set (according to primary key order)
	 * */
	public Set<String> getPrimaryKeys() {
		return primaryKeys;
	}

	public String getClassName() {
		return className;
	}
	

	/**
	 * indicating the primary key is 'IS_AUTOINCREMENT' or not
	 * */
	public boolean isAutoIncr() {
		return autoIncr;
	}
	
	/**
	 * set 'IS_AUTOINCREMENT' flag
	 * */
	public void autoIncr() {
		this.autoIncr = true;
	}

	@Override
	public String toString() {
		return "TableSchema [className=" + className + ", propertyTypes=" + propertyTypes + ", primaryKeys="
				+ primaryKeys + ", autoIncr=" + autoIncr + "]";
	}
	
	
}
