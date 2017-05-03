package io.downgoon.autorest4db.mode;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.github.downgoon.jresty.data.orm.annotation.ORMField;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

public class DynamicPojo {

	private CtClass ctClass;

	private Class<?> pojoClass;

	private String className;

	private Set<String> primaryKeys;

	private boolean autoIncr;

	public DynamicPojo(String className, Map<String, Class<?>> properties) {
		this(className, properties, null, false);
	}

	/**
	 * 依据属性和主键（ Dao Model 时）生成POJO
	 */
	public DynamicPojo(String className, Map<String, Class<?>> properties, Set<String> primaryKeys,
			boolean isAutoIncr) {

		try {
			this.className = className;
			this.primaryKeys = primaryKeys;
			this.autoIncr = isAutoIncr;

			this.ctClass = generateCtClass(className, properties);

			for (String priKey : primaryKeys) {
				ORMFieldMate as = new ORMFieldMate(priKey);
				as.isKey = true;
				if (isAutoIncr) {
					as.isAutoIncrement = isAutoIncr;
				}
				// attribute name of POJO equal to filed name in Table
				addAnnotation(priKey, as);
			}

			this.pojoClass = ctClass.toClass();

		} catch (NotFoundException | CannotCompileException e) {
			throw new IllegalStateException("DynamicPojo generate exception on: " + className, e);
		}

	}

	public Set<String> getPrimaryKeys() {
		return this.primaryKeys;
	}

	/**
	 * indicating the primary key is 'IS_AUTOINCREMENT' or not
	 */
	public boolean isAutoIncr() {
		return autoIncr;
	}

	/**
	 * add annotation on getter method of POJO if necessary
	 * 
	 * @param attributeName
	 *            attribute name of POJO in Java, rather than filed name in
	 *            Database
	 * @param annotationSetting
	 *            ORMField annotation setting
	 * @throws NotFoundException
	 *             getter method for attributeName Not found on POJO
	 */
	protected void addAnnotation(String attributeName, ORMFieldMate annotationSetting) throws NotFoundException {
		final String getterMethodName = getterSetterName(true, attributeName);
		CtMethod method = ctClass.getDeclaredMethod(getterMethodName);

		MethodInfo methodInfoGetEid = method.getMethodInfo();
		ConstPool cp = methodInfoGetEid.getConstPool();

		Annotation annotationNew = new Annotation(ORMField.class.getName(), cp);

		// set annotation values
		annotationNew.addMemberValue("name", new StringMemberValue(annotationSetting.name, cp));
		if (annotationSetting.isSkip != null) {
			annotationNew.addMemberValue("isSkip", new BooleanMemberValue(annotationSetting.isSkip, cp));
		}
		if (annotationSetting.isKey != null) {
			annotationNew.addMemberValue("isKey", new BooleanMemberValue(annotationSetting.isKey, cp));
		}
		if (annotationSetting.isAutoIncrement != null) {
			annotationNew.addMemberValue("isAutoIncrement",
					new BooleanMemberValue(annotationSetting.isAutoIncrement, cp));
		}

		AnnotationsAttribute attributeNew = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);
		attributeNew.setAnnotation(annotationNew);

		/*
		 * confused API : add 'AnnotationsAttribute' (NOT 'Annotation') on
		 * 'MethodInfo' (NOT 'Method')
		 */
		methodInfoGetEid.addAttribute(attributeNew);
	}

	/**
	 * mate of the annotation of {@link ORMField}
	 */
	private static class ORMFieldMate {
		String name; // NOT NULL
		Boolean isSkip;
		Boolean isKey;
		Boolean isAutoIncrement;

		ORMFieldMate(String name) {
			this.name = name;
		}
	}

	public DynamicBean newInstance() {
		try {
			return new DynamicBean(this.pojoClass.newInstance());
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

	public static Class<?> generate(String className, Map<String, Class<?>> properties)
			throws NotFoundException, CannotCompileException {
		return generateCtClass(className, properties).toClass();
	}

	private static CtClass generateCtClass(String className, Map<String, Class<?>> properties)
			throws NotFoundException, CannotCompileException {

		ClassPool pool = ClassPool.getDefault();
		CtClass dClass = pool.makeClass(className); // dynamic class

		// add this to define a super class to extend
		// cc.setSuperclass(resolveCtClass(MySuperClass.class));

		// add this to define an interface to implement
		dClass.addInterface(resolveCtClass(Serializable.class));

		for (Entry<String, Class<?>> field : properties.entrySet()) {

			// add field
			dClass.addField(new CtField(resolveCtClass(field.getValue()), field.getKey(), dClass));

			// add getter
			dClass.addMethod(generateGetter(dClass, field.getKey(), field.getValue()));

			// add setter
			dClass.addMethod(generateSetter(dClass, field.getKey(), field.getValue()));
		}

		// add toString
		dClass.addMethod(generateToString(dClass, properties));

		return dClass;
	}

	private static CtMethod generateGetter(CtClass beanClass, String fieldName, Class<?> fieldType)
			throws CannotCompileException {

		String getterName = getterSetterName(true, fieldName);

		StringBuffer methodSrcCode = new StringBuffer();
		methodSrcCode.append("public ").append(fieldType.getName()).append(" ").append(getterName).append("() {")
				.append("return this.").append(fieldName).append(";").append("}");

		return CtMethod.make(methodSrcCode.toString(), beanClass);
	}

	private static CtMethod generateSetter(CtClass beanClass, String fieldName, Class<?> fieldType)
			throws CannotCompileException {

		String setterName = getterSetterName(false, fieldName);

		StringBuffer methodSrcCode = new StringBuffer();
		methodSrcCode.append("public void ").append(setterName).append("(").append(fieldType.getName()).append(" ")
				.append(fieldName).append(")").append("{").append("this.").append(fieldName).append("=")
				.append(fieldName).append(";").append("}");

		return CtMethod.make(methodSrcCode.toString(), beanClass);
	}

	private static CtMethod generateToString(CtClass beanClass, Map<String, Class<?>> properties)
			throws CannotCompileException {

		StringBuffer methodSrcCode = new StringBuffer();
		methodSrcCode.append("public String toString() { ").append(" return \"").append(beanClass.getSimpleName())
				.append(" [\"");

		methodSrcCode.append("+");

		for (Entry<String, Class<?>> field : properties.entrySet()) {
			methodSrcCode.append("\"").append(field.getKey()).append("=\"").append("+")
					.append("this." + field.getKey());
			methodSrcCode.append("+");
			methodSrcCode.append("\",\"+");
		}
		if (properties.size() > 0) {
			// "\",\"+" length is 4
			methodSrcCode.delete(methodSrcCode.length() - 4, methodSrcCode.length());
		}

		methodSrcCode.append("\"]\";").append("}");

		return CtMethod.make(methodSrcCode.toString(), beanClass);
	}

	private static CtClass resolveCtClass(Class<?> clazz) throws NotFoundException {
		ClassPool pool = ClassPool.getDefault();
		return pool.get(clazz.getName());
	}

	private static String getterSetterName(boolean isGetter, String fieldName) {
		String prefix = (isGetter ? "get" : "set");
		if (fieldName.length() > 1) {
			return prefix + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		} else {
			return prefix + fieldName.toUpperCase();
		}
	}

	private volatile String sourceCode;

	/** not thread-safe (just for debugging ) */
	@Override
	public String toString() {
		if (sourceCode == null) {
			sourceCode = reflectClassSourceCode();
		}
		return sourceCode;
	}

	/**
	 * class source code for debugging
	 */
	protected String reflectClassSourceCode() {
		StringBuffer srcCode = new StringBuffer();
		try {
			srcCode.append(System.lineSeparator());
			srcCode.append("/** Dynamic Class Definition */");
			srcCode.append(System.lineSeparator());
			srcCode.append("public Class ").append(className).append(" {");
			srcCode.append(System.lineSeparator());

			srcCode.append(System.lineSeparator());
			srcCode.append("\t").append("/* Fields */");
			srcCode.append(System.lineSeparator());

			// Fields (only non-private fields)
			CtField[] fields = ctClass.getFields();
			for (CtField field : fields) {
				// field.getName()
				srcCode.append("\t").append(field.getFieldInfo2());
				srcCode.append(System.lineSeparator());
			}

			srcCode.append(System.lineSeparator());
			srcCode.append("\t").append("/* Methods */");
			srcCode.append(System.lineSeparator());

			// Methods
			CtMethod[] methods = ctClass.getDeclaredMethods();
			for (CtMethod method : methods) {
				ORMField annotationOnMethod = (ORMField) method.getAnnotation(ORMField.class);
				// Method Annotation
				if (annotationOnMethod != null) {
					srcCode.append("\t").append(annotationOnMethod);
				}
				srcCode.append(System.lineSeparator());

				// Method Declaration
				// available even if frozen (don't call getMethodInfo)
				srcCode.append("\t").append(method.getMethodInfo2());
				srcCode.append(System.lineSeparator());
			}

			srcCode.append("}");
			srcCode.append(System.lineSeparator());

			return srcCode.toString();

		} catch (Exception e) {
			throw new IllegalStateException("Source Code Refection Exception: " + pojoClass.getName(), e);
		}

	}
}
