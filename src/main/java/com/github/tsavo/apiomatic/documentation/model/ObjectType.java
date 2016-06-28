package com.github.tsavo.apiomatic.documentation.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ObjectType extends TypeDefinition {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2471656517997763393L;

	List<TypeDefinition> properties = new ArrayList<>();
	public Set<TypeDefinition> subclasses = new HashSet<>();
	public Boolean abstractClass = null;

	public Boolean isAbstractClass() {
		return abstractClass;
	}

	public void setAbstractClass(boolean abstractClass) {
		this.abstractClass = abstractClass;
	}

	public ObjectType(final Class<?> aClazz) {
		super(Type.OBJECT);
		setType(aClazz.getName());
	}

	public void addProperty(final TypeDefinition aTypeDef) {
		properties.add(aTypeDef);
	}

	public List<TypeDefinition> getProperties() {
		return properties;
	}

	public void setProperties(final List<TypeDefinition> properties) {
		this.properties = properties;
	}

	public Set<TypeDefinition> getSubclasses() {
		return subclasses;
	}

	public void setSubclasses(Set<TypeDefinition> someSubclasses) {
		subclasses = someSubclasses;
	}

}
