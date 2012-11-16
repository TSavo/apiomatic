package com.tsavo.apiomatic.model;

import java.io.Serializable;

public abstract class TypeDefinition implements Serializable {
	public enum Type {
		ARRAY {
			@Override
			public String toString() {
				return "array";
			}
		},
		INTEGER {
			@Override
			public String toString() {
				return "int";
			}
		},
		NULL {
			@Override
			public String toString() {
				return "null";
			}
		},
		NUMBER {
			@Override
			public String toString() {
				return "number";
			}
		},
		OBJECT {
			@Override
			public String toString() {
				return "object";
			}
		},
		STRING {
			@Override
			public String toString() {
				return "string";
			}
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6447501882432068218L;
	public boolean optional = false;

	public String type;
	
	public TypeDefinition(final Type aType) {
		type = aType.toString();
	}

	public String getType() {
		return type;
	}

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(final boolean optional) {
		this.optional = optional;
	}

	public void setType(final String type) {
		this.type = type;
	}


}
