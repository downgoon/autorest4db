package io.downgoon.autorest4db.mode;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.core.Predicate;

public class DynamicClass {

	private BeanGenerator beanGenerator;

	private String className;

	private Map<String, Class<?>> propertyTypes; // MUST Incoming Order

	private Set<String> primaryKeys; // May be NULL

	public DynamicClass(final String className, String[] propertyNames, Class<?>[] propertyTypes) {

		if (propertyNames.length != propertyTypes.length) {
			throw new IllegalArgumentException("property names and types length not equal");
		}
		this.className = className;
		beanGenerator = new BeanGenerator();

		// Class Name
		beanGenerator.setNamingPolicy(new NamingPolicy() {
			@Override
			public String getClassName(final String prefix, final String source, final Object key,
					final Predicate names) {
				return className;
			}
		});

		// MUST Incoming Order
		this.propertyTypes = new LinkedHashMap<String, Class<?>>();

		// Class Attribute
		for (int i = 0; i < propertyNames.length; i++) {
			beanGenerator.addProperty(propertyNames[i], propertyTypes[i]);
			this.propertyTypes.put(propertyNames[i], propertyTypes[i]);

		}

	}

	public DynamicClass(final String className, Map<String, Class<?>> propertyTypes) {
		beanGenerator = new BeanGenerator();

		// Class Name
		beanGenerator.setNamingPolicy(new NamingPolicy() {
			@Override
			public String getClassName(final String prefix, final String source, final Object key,
					final Predicate names) {
				return className;
			}
		});

		// Class Attribute
		// beanGenerator.addProperty("name", String.class);

		this.className = className;
		this.propertyTypes = propertyTypes;
		propertyTypes.entrySet().parallelStream()
				.forEach(entry -> beanGenerator.addProperty(entry.getKey(), entry.getValue()));

	}

	public DynamicClass(final String className, Map<String, Class<?>> propertyTypes, Set<String> primaryKeys) {
		this(className, propertyTypes);
		this.primaryKeys = primaryKeys;
	}

	Object createObject() {
		return beanGenerator.create();
	}

	public DynamicBean newInstance() {
		return new DynamicBean(this.createObject());
	}

	public DynamicBean newInstance(Object[] propertyValues) {
		if (propertyValues != null && propertyValues.length != propertyTypes.size()) {
			throw new IllegalArgumentException("property values length not equal to its names");
		}
		DynamicBean bean = new DynamicBean(this.createObject());
		Iterator<Entry<String, Class<?>>> props = propertyTypes.entrySet().iterator();
		int i = 0;
		while (props.hasNext()) {
			Entry<String, Class<?>> es = props.next();
			bean.setProperty(es.getKey(), propertyValues[i]);
			i++;
		}
		return bean;
	}

	public Class<?> getPropertyType(String propertyName) {
		return propertyTypes.get(propertyName);
	}

	public boolean hasProperty(String name) {
		return propertyTypes.containsKey(name);
	}

	public Set<String> getProperties() {
		return propertyTypes.keySet();
	}

	public Set<String> getPrimaryKeys() {
		return primaryKeys;
	}
	
	
	@Override
	public String toString() {
		return "DynamicClass [className=" + className + ", propertyTypes=" + propertyTypes + ", primaryKeys="
				+ primaryKeys + "]";
	}
	
}
