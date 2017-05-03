package io.downgoon.autorest4db.mode;

import java.util.LinkedHashMap;
import java.util.Map;

import io.downgoon.autorest4db.mode.DynamicBean;
import io.downgoon.autorest4db.mode.DynamicPojo;

public class DynamicPojoDemo {

	public static void main(String[] args) throws Exception {
		
		String className = "io.downgoon.dynamic.Employee";
		Map<String, Class<?>> props = new LinkedHashMap<String, Class<?>>();
		props.put("age", Integer.class);
		props.put("name", String.class);
		
		DynamicPojo dynamicPojo = new DynamicPojo(className, props);
		DynamicBean dynamicBean = dynamicPojo.newInstance();
		
		dynamicBean.setProperty("age", 28);
		dynamicBean.setProperty("name", "张三");
		
		System.out.println(dynamicBean.getBean());
		
//		dynamicPojo.printClass();
	}

}
