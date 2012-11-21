package com.cpn.apiomatic.generator.model.sql;

public class Column {

	String name;
	String type;
	String notNull = null;
	String defaultValue = null;

	public Column(String aName, String aType) {
		name = aName;
		type = aType;
	}

	public Column(String aName, String aType, String aNotNull) {
		name = aName;
		type = aType;
		notNull = aNotNull;
	}

	public Column(String aName, String aType, String aNotNull, String aDefaultValue) {
		name = aName;
		type = aType;
		notNull = aNotNull;
		defaultValue = aDefaultValue;
	}
}
