package com.tsavo.apiomatic.model.sql;

import java.io.IOException;
import java.io.Writer;

import com.sun.codemodel.JClass;

public class Index {
	Table table;
	Column column;
	JClass ref;

	public Index(Table aTable, Column aColumn, JClass aRef) {
		super();
		this.table = aTable;
		this.column = aColumn;
		this.ref = aRef;
	}
	
	public void write(Writer writer) throws IOException{
		writer.write("alter table " + table.name + " add index " + table.name + "_" + column.name + "_index (" + column.name + "), add constraint " + table.name + "_" +column.name + "_fk foreign key (" + column.name + ") references " + ref.name() + " (id)\n");
	}

}
