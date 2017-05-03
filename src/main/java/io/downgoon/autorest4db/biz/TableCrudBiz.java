package io.downgoon.autorest4db.biz;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.github.downgoon.jresty.data.orm.dao.CRUDDaoSupport;

import io.downgoon.autorest4db.dao.DaoFactory;
import io.downgoon.autorest4db.dao.TableClassFactory;
import io.downgoon.autorest4db.mode.AutoRestException;
import io.downgoon.autorest4db.mode.DynamicBean;
import io.downgoon.autorest4db.mode.DynamicPojo;

public class TableCrudBiz {

	
	private TableClassFactory tableClassFactory = new TableClassFactory();
	
	private DaoFactory daoFactory = new DaoFactory();
	
	public List<Object> getList(String dbName, String tableName) {
		DynamicPojo tableClass = tableClassFactory.getTableClass(tableName);
		DynamicBean tableInstance = tableClass.newInstance();
		CRUDDaoSupport<Object> dao = daoFactory.getDao(dbName);
		return dao.findObjects(tableInstance.getBean());
	}
	
	public List<Object> getList(String dbName, String tableName, Map<String, Object> params) {
		DynamicPojo tableClass = tableClassFactory.getTableClass(tableName);
		DynamicBean tableInstance = tableClass.newInstance();
		CRUDDaoSupport<Object> dao = daoFactory.getDao(dbName);
		
		// params condition
		Set<String> props = tableInstance.getProperties();
		for (String prop : props) {
			if (params.get(prop) == null) {
				continue;
			}
			if (params.get(prop) instanceof String) {
				tableInstance.setPropertyString(prop, (String) params.get(prop) );
			} else {
				tableInstance.setProperty(prop, params.get(prop));
			}
			
		}
		
		return dao.findObjects(tableInstance.getBean());
	}
	
	public Object getDetail(String dbName, String tableName, String id) {
		DynamicPojo tableClass = tableClassFactory.getTableClass(tableName);
		DynamicBean tableInstance = tableClass.newInstance();
		
		fillPrimaryKeys(id, tableClass.getPrimaryKeys(), tableInstance);
	
		CRUDDaoSupport<Object> dao = daoFactory.getDao(dbName);
		
		return dao.findObject(tableInstance.getBean());
	}
	
	public int remove(String dbName, String tableName, String id) {
		DynamicPojo tableClass = tableClassFactory.getTableClass(tableName);
		DynamicBean tableInstance = tableClass.newInstance();
		
		fillPrimaryKeys(id, tableClass.getPrimaryKeys(), tableInstance);
	
		CRUDDaoSupport<Object> dao = daoFactory.getDao(dbName);
		int rows = dao.removeObjects(tableInstance.getBean());
		return rows;
	}
	
	public Object create(String dbName, String tableName, Map<String, Object> record) {
		
		DynamicPojo tableClass = tableClassFactory.getTableClass(tableName);
		DynamicBean tableInstance = tableClass.newInstance();

		// fill object
		fillDyamicBean(record, tableInstance);
		
		// exec dao
		CRUDDaoSupport<Object> dao = daoFactory.getDao(dbName);
		dao.saveObject(tableInstance.getBean());
		return tableInstance.getBean();
	}
	
	
	public int update(String dbName, String tableName, String id, Map<String, Object> record) {
		DynamicPojo tableClass = tableClassFactory.getTableClass(tableName);
		DynamicBean tableInstance = tableClass.newInstance();
		
		// fill primary key
		fillPrimaryKeys(id, tableClass.getPrimaryKeys(), tableInstance);
		
		// fill object
		fillDyamicBean(record, tableInstance);
	
		// exec dao
		CRUDDaoSupport<Object> dao = daoFactory.getDao(dbName);
		int rows = dao.updateObject(tableInstance.getBean());
		return rows;
	}
	
	
	
	/**
	 * fill dynamic object with record
	 * 
	 * @param	record	
	 * 				input argument
	 * @param	tableInstance	
	 * 				output argument
	 * */
	protected void fillDyamicBean(Map<String, Object> record, DynamicBean tableInstance) {
		Iterator<Entry<String, Object>> fields = record.entrySet().iterator();
		while (fields.hasNext()) {
			Entry<String, Object> f = fields.next();
			tableInstance.setProperty(f.getKey(), f.getValue());
		}
	}
	
	
	/**
	 * fill primary keys with id
	 * */
	protected void fillPrimaryKeys(String id, Set<String> keys, DynamicBean tableInstance) {
		if (keys == null || keys.size() == 0) {
			return ;
		}
		if (keys.size() == 1) { 
			String keyName = keys.iterator().next();
			tableInstance.setPropertyString(keyName, id);
			return ;
		}
		
		// 约定：联合主键的ID取值用短横线连接
		String[] keyValues = id.split("-");  
		if (keyValues == null || keyValues.length != keys.size()) {
			throw new AutoRestException("invalid keys value for " + tableInstance.getClass().getSimpleName());
		}
		
		int i = 0;
		for (String keyName : keys) {
			tableInstance.setPropertyString(keyName, keyValues[i]);
			i ++;
		}
		
	}
	
	
}
