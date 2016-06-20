package com.tsavo.apiomatic.documentation.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ArrayType extends TypeDefinition {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6339975342235356854L;
	@JsonProperty("nestedType")
	TypeDefinition nestedType;

	public <T> ArrayType(final TypeDefinition aTypeDef) {
		super(Type.ARRAY);
		nestedType = aTypeDef;
		typeRefs.addAll(aTypeDef.typeRefs);
	}
}
