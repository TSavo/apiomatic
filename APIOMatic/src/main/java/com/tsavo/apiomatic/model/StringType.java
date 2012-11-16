package com.tsavo.apiomatic.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class StringType extends TypeDefinition {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7208449540890409090L;

	String requiredValue = null;
	
	public String getRequiredValue() {
		return requiredValue;
	}

	public void setRequiredValue(String requiredValue) {
		this.requiredValue = requiredValue;
	}

	public StringType() {
		super(Type.STRING);
	}
}
