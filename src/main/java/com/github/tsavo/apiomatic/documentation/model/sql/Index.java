package com.github.tsavo.apiomatic.documentation.model.sql;

import java.io.IOException;
import java.io.Writer;

import com.sun.codemodel.JType;

public class Index {
	Table table;
	Column column;
	JType ref;

	public Index(Table aTable, Column aColumn, JType jType) {
		super();
		this.table = aTable;
		this.column = aColumn;
		this.ref = jType;
	}

	public void write(Writer writer) throws IOException {
		writer.write("alter table " + table.name + " add index " + table.name + "_" + column.name + "_index (" + column.name + "), add constraint " + table.name + "_" + column.name + "_fk foreign key (" + column.name + ") references " + ref.name()
				+ " (id);\n");
	}

}
