package com.tsavo.apiomatic.model;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

public class ObjectType extends TypeDefinition {
	Map<String, TypeDefinition> properties = new HashMap<>();
	@JsonProperty("class")
	String clazz;

	public Map<String, TypeDefinition> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, TypeDefinition> properties) {
		this.properties = properties;
	}

	public void addProperty(String aKey, TypeDefinition aTypeDef) {
		properties.put(aKey, aTypeDef);
	}

	public ObjectType(Class<?> aClazz) {
		super(Type.OBJECT);
		clazz = aClazz.getName();
	}
}
