package com.cpn.apiomatic.documentation.model;


public class TypeRefType extends TypeDefinition {
	public TypeRefType() {
		super(Type.TYPE_REF);
	}

	public TypeRefType(Class<?> aClass) {
		super(Type.TYPE_REF);
		typeRefs.add(aClass);
		setType(aClass.getName());
	}

	public TypeRefType(Class<?> aClass, String aName) {
		super(Type.TYPE_REF);
		typeRefs.add(aClass);
		setType(aClass.getName());
		setName(aName);
	}


}
