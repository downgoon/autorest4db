package io.downgoon.autorest4db.biz;

import java.util.HashMap;
import java.util.Map;

public class TableCrudDemo {

	public static void main(String[] args) {
		TableCrudBiz crud = new TableCrudBiz();
		
		Map<String, Object> record = new HashMap<String, Object>();
		record.put("id", 4);
		record.put("name", "赵四");
		record.put("age", 42);
		
		crud.create("dafault", "student", record);
		
	}

}
