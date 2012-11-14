package com.tsavo.apiomatic.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class ArrayType extends TypeDefinition {
	@JsonProperty("nestedType")
	TypeDefinition nestedType;

	public <T> ArrayType(TypeDefinition aTypeDef) {
		super(Type.ARRAY);
		nestedType = aTypeDef;
	}
}
