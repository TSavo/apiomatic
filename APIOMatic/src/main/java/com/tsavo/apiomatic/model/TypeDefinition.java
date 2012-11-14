package com.tsavo.apiomatic.model;

import java.io.Serializable;

public abstract class TypeDefinition implements Serializable {
	public String type;
	public boolean optional = false;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	public TypeDefinition(Type aType) {
		type = aType.toString();
	}

	public enum Type {
		OBJECT {
			@Override
			public String toString() {
				return "object";
			}
		},
		ARRAY {
			@Override
			public String toString() {
				return "array";
			}
		},
		NUMBER {
			@Override
			public String toString() {
				return "number";
			}
		},
		INTEGER {
			@Override
			public String toString() {
				return "int";
			}
		},
		STRING {
			@Override
			public String toString() {
				return "string";
			}
		},
		NULL {
			@Override
			public String toString() {
				return "null";
			}
		}
	}

	
}
