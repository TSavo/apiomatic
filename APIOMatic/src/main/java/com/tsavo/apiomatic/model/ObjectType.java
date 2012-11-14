package com.tsavo.apiomatic.model;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

public class ObjectType extends TypeDefinition {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2471656517997763393L;
	@JsonProperty("class")
	String clazz;
	Map<String, TypeDefinition> properties = new HashMap<>();

	public ObjectType(final Class<?> aClazz) {
		super(Type.OBJECT);
		clazz = aClazz.getName();
	}

	public void addProperty(final String aKey, final TypeDefinition aTypeDef) {
		properties.put(aKey, aTypeDef);
	}

	public Map<String, TypeDefinition> getProperties() {
		return properties;
	}

	public void setProperties(final Map<String, TypeDefinition> properties) {
		this.properties = properties;
	}
}
