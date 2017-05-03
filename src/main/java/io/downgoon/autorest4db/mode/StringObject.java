package io.downgoon.autorest4db.mode;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Java字符串文本反序列化成Java对象
 * 		Object有toString()方法，String得有个toObject方法。
 * */
public class StringObject {

	/**
	 * convert filed value of pojo from String format to Object format
	 * @param	fvalue
	 * 		pojo field value in String format
	 * @param	ftype
	 * 		pojo field type (java class)
	 * @param 	fnanme
	 * 		pojo field name
	 * 	
	 * */
	public static Object toObject(String fvalue, Class<?> ftype, String fname)
			throws IllegalStateException, IllegalArgumentException {
		try {
			if (String.class.equals(ftype)) {
				return fvalue;
			} else if (Integer.class.equals(ftype)) {
				return Integer.parseInt(fvalue);
			} else if (Long.class.equals(ftype)) {
				return Long.parseLong(fvalue);
			} else if (Short.class.equals(ftype)) {
				return Short.parseShort(fvalue);
			} else if (Date.class.equals(ftype)) {
				if (fvalue.length() == "yyyy-MM-dd HH:mm:ss".length()) {
					return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fvalue);
				} else if (fvalue.length() == "yyyyMMddHHmmss".length()) {
					return new SimpleDateFormat("yyyyMMddHHmmss").parse(fvalue);
				} else if (fvalue.length() == "yyyy-MM-dd".length()) {
					return new SimpleDateFormat("yyyyy-MM-dd").parse(fvalue);
				} else if (fvalue.length() == "yyyy-MM-dd".length()) {
					return new SimpleDateFormat("yyyy-MM-dd").parse(fvalue);
				} else { // JAVA TimeStamp
					return new Date(Long.parseLong(fvalue));
				}
			} else if (Boolean.class.equals(ftype)) {
				return Boolean.parseBoolean(fvalue);
			} else {
				throw new IllegalStateException("Pojo filed type Not supported: " + ftype);
			}

		} catch (Exception e) {
			throw new IllegalArgumentException(
					"field value error: name=" + fname + "; type required: " + ftype.getSimpleName());
		}
	}
	
}
