package io.downgoon.autorest4db.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.downgoon.jresty.commons.utils.concurrent.ConcurrentResourceContainer;
import com.github.downgoon.jresty.commons.utils.concurrent.ResourceLifecycle;

import io.downgoon.autorest4db.mode.DynamicPojo;
import io.downgoon.autorest4db.mode.TableSchema;

public class TableClassFactory {

	private static Logger LOG = LoggerFactory.getLogger(TableClassFactory.class);

	private static final String packageName = "io.downgoon.dynamic";
	
	private static final String DEFAULT_DB_NAME = "default";

	private final TableSchemaStorage tableSchemaStorage = new TableSchemaStorage();

	private ConcurrentResourceContainer<DynamicPojo> container = new ConcurrentResourceContainer<>(
			new ResourceLifecycle<DynamicPojo>() {

				@Override
				public DynamicPojo buildResource(String tableName) throws Exception {
					TableSchema beanSchema = tableSchemaStorage.getTableSchema(DEFAULT_DB_NAME, tableName);
					
					String pojoClassName = packageName + "." + beanSchema.getClassName();
					DynamicPojo dynamicPojo = new DynamicPojo(pojoClassName, beanSchema.getPropertyTypes(),
							beanSchema.getPrimaryKeys(), beanSchema.isAutoIncr());

					if (LOG.isDebugEnabled()) {
						// show dynamic class definition
						LOG.debug("dynamic class for {}.{} {}", DEFAULT_DB_NAME, tableName, dynamicPojo);
					}
					return dynamicPojo;

				}

				@Override
				public void destoryResource(String tableName, DynamicPojo resource) throws Exception {

				}

			});

	/**
	 * @param tableName
	 *            database table name
	 * @return return NULL, if tableName not found
	 */
	public DynamicPojo getTableClass(String tableName) {
		DynamicPojo dc = null;
		try {
			dc = container.getResource(tableName);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		if (dc == null) {
			throw new IllegalStateException("table not found: " + tableName);
		}
		return dc;
	}

}
