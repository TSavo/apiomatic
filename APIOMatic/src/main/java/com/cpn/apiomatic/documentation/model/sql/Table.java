package com.cpn.apiomatic.documentation.model.sql;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.WordUtils;

import com.sun.codemodel.JFieldVar;

public class Table {

	public String name;

	List<Column> columns = new ArrayList<>();

	public Table(String aName) {
		name = aName;
	}

	public Column addColumn(JFieldVar field) {
		if (field.type().name().equals("int")) {
			Column c = new Column(field.name(), "integer");
			columns.add(c);
			return c;
		}
		if (field.type().name().equals("String")) {
			Column c = new Column(field.name(), "varchar(255)");
			columns.add(c);
			if (field.name().equals("id")) {
				c.notNull = "not null";
			}
			return c;
		}
		Column column = new Column(WordUtils.uncapitalize(field.type().name()) + "_id", "varchar(255)", "", "default NULL");
		columns.add(column);
		return column;
	}

	public void write(Writer aWriter) throws IOException {
		aWriter.write("create table " + name + " (");
		for (Column column : columns) {
			aWriter.write(column.name + " " + column.type);
			if (column.notNull != null) {
				aWriter.write(" " + column.notNull);
			}
			if (column.defaultValue != null) {
				aWriter.write(" " + column.defaultValue);
			}
			aWriter.write(", ");
		}
		aWriter.write("primary key (id)) ENGINE=InnoDB;\n");
	}
}
