package com.tsavo.apiomatic.model.sql;

import java.util.ArrayList;
import java.util.List;

import com.sun.codemodel.JClass;

public class Table {

	String name;

	List<Column> columns = new ArrayList<>();
	List<Index> indexes = new ArrayList<>();

	public Table(String aName) {
		name = aName;
	}

	public void varchar(String aName) {
		columns.add(new Column(aName, "varchar(255)"));
	}
	
	public void objectRef(JClass anObjectRef){
		Column column = new Column(anObjectRef.name().toLowerCase() + "_id", "varchar(255)", "", "default NULL");
		columns.add(column);
		indexes.add(new Index(this, column, anObjectRef));
	}

}
