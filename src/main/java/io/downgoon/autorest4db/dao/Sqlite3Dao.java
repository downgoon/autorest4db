package io.downgoon.autorest4db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.StatementCallback;

import com.github.downgoon.jresty.data.orm.dao.CRUDDaoSupport;
import com.github.downgoon.jresty.data.orm.dao.sql.ORFieldMapping;
import com.github.downgoon.jresty.data.orm.dao.sql.ORMBuilderFacade;
import com.github.downgoon.jresty.data.orm.dao.sql.SQLGenerator;
import com.github.downgoon.jresty.data.orm.dao.util.PojoOperatorFactory;

public class Sqlite3Dao<T> extends CRUDDaoSupport<T> {

	private SQLGenerator sqlGenerator = new SQLGenerator();

	@Override
	public int saveObject(T t) {
		final String sqlInsert = sqlGenerator.genSQLInsert(t);
        
        ORFieldMapping fieldMapping = new ORMBuilderFacade(t, false).buildFieldMapping(); // TODO Cached
        
        String autoIncrAttri = fieldMapping.getAutoIncrementAttri();
        // autoIncrAttri = "id";
        if (autoIncrAttri == null || PojoOperatorFactory.getPojoOperator().doGetter(t, autoIncrAttri) != null) {
        	return getJdbcTemplate().update(sqlInsert);
        }
        
        // return auto_increment id
        final AtomicInteger rowsAffect = new AtomicInteger(); // output argument 
        Long autoIncrValue = getJdbcTemplate().execute(new StatementCallback<Long>() {
        	@Override
			public Long doInStatement(Statement stmt) throws SQLException, DataAccessException {
        		int rows = stmt.executeUpdate(sqlInsert);
        		rowsAffect.set(rows);
				
        		ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()");  // LAST_INSERT_ID() for mysql
				if (rs.next()) { // hasNext
					return rs.getLong(1); // auto_increment value
				} else {
					throw new IllegalArgumentException(String.format("AUTO_INCREMENT not supported in Table %s", 
							t.getClass().getSimpleName()));
				}
			}
        });
        
        // set auto_increment id from db to java bean attribute
        String autoIncrColum = fieldMapping.getAutoIncrementColum();
        Class<?> autoIncrType = fieldMapping.getDbFieldJavaType().get(autoIncrColum);
        
        // autoIncrType = Integer.class;
        
        if (Long.class.equals(autoIncrType)) {
        	PojoOperatorFactory.getPojoOperator().doSetter(t, autoIncrAttri, autoIncrValue.longValue());
        } else if (Integer.class.equals(autoIncrType)) {
        	PojoOperatorFactory.getPojoOperator().doSetter(t, autoIncrAttri, autoIncrValue.intValue());
        } else if (Short.class.equals(autoIncrType)) {
        	PojoOperatorFactory.getPojoOperator().doSetter(t, autoIncrAttri, autoIncrValue.shortValue());
        } else {
        	throw new IllegalArgumentException(String.format("AUTO_INCREMENT type %s not supported in Table %s", 
					autoIncrType.getSimpleName(), t.getClass().getSimpleName()));
        }
        
        return rowsAffect.get();
	}

	
}
