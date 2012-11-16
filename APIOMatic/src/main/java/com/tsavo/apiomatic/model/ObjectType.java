package com.tsavo.apiomatic.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class ObjectType extends TypeDefinition {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2471656517997763393L;
	@JsonProperty("class")
	String clazz;
	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	Map<String, TypeDefinition> properties = new HashMap<>();
	public Set<TypeDefinition> subclasses = new HashSet<>();
	public boolean abstractClass = false;
	
	public boolean isAbstractClass() {
		return abstractClass;
	}

	public void setAbstractClass(boolean abstractClass) {
		this.abstractClass = abstractClass;
	}

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
	public Set<TypeDefinition> getSubclasses() {
		return subclasses;
	}
	public void setSubclasses(Set<TypeDefinition> someSubclasses){
		subclasses = someSubclasses;
	}

}
