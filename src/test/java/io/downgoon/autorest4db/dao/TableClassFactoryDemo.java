package io.downgoon.autorest4db.dao;

import io.downgoon.autorest4db.mode.DynamicBean;
import io.downgoon.autorest4db.mode.DynamicPojo;

public class TableClassFactoryDemo {

	public static void main(String[] args) {
		TableClassFactory tableClassFactory = new TableClassFactory();
		DynamicPojo studentClass = tableClassFactory.getTableClass("student");
		
		DynamicBean studentBean = studentClass.newInstance();
		
		System.out.println(studentBean.getBean());
	}

}
