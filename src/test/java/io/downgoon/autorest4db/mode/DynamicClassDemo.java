package io.downgoon.autorest4db.mode;

public class DynamicClassDemo {

	public static void main(String[] args) throws Exception {

		DynamicClass dynamicClass = new DynamicClass("io.downgoon.dynamic.Employee",
				new String[] { "name", "age", "scores" }, new Class<?>[] { String.class, Integer.class, int[].class });

		DynamicBean e1 = new DynamicBean(dynamicClass);
		e1.setProperty("name", "陈六子");
		e1.setProperty("age", 45);
		e1.setProperty("scores", new int[] { 92, 89, 96 });

		DynamicBean e2 = dynamicClass.newInstance(new Object[] { "张三", 30, new int[] { 59, 66, 78 } });

		System.out.println(e1);
		System.out.println(e2);
	}

}
