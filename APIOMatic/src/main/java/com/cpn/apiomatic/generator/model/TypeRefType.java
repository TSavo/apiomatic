package com.cpn.apiomatic.generator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TypeRefType extends TypeDefinition {
	public TypeRefType() {
		super(Type.TYPE_REF);
	}

	public TypeRefType(Class<?> aClass) {
		super(Type.TYPE_REF);
		typeRefs.add(aClass);
		clazz = aClass.getName();
	}

	public TypeRefType(Class<?> aClass, String aName) {
		super(Type.TYPE_REF);
		typeRefs.add(aClass);
		clazz = aClass.getName();
		setName(aName);
	}

	@JsonProperty("class")
	String clazz;

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

}
