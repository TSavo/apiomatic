package com.cpn.apiomatic.documentation;

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr._this;
import static com.sun.codemodel.JExpr.lit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cpn.apiomatic.documentation.model.sql.Column;
import com.cpn.apiomatic.documentation.model.sql.Index;
import com.cpn.apiomatic.documentation.model.sql.Table;
import com.cpn.apiomatic.rest.AbstractDAO;
import com.cpn.apiomatic.rest.AbstractRestController;
import com.cpn.apiomatic.rest.DataTransferObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JVar;

public class JsonBeanGenerator {

	private JCodeModel codeModel = new JCodeModel();
	private Map<String, Table> tables = new HashMap<>();
	private Map<String, Index> indexes = new HashMap<>();

	public static void main(String[] args0) throws Exception {
		JsonBeanGenerator g = new JsonBeanGenerator();
		g.test();
	}

	public void test() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		makeClassFromJson(objectMapper.readValue(System.in, JsonNode.class), new File("c:\\temp"));
	}

	public JDefinedClass makeClassFromJson(JsonNode aNode, File aDestination) throws JClassAlreadyExistsException, IOException {
		return makeClassFromJson(aNode.get("__class__").textValue(), aNode, aDestination);
	}

	public JavaRepresentation makeClassDescription(JsonNode aNode) throws Exception {
		final JavaRepresentation rep = new JavaRepresentation();
		rep.originalJson = aNode;
		String name;
		if(aNode.hasNonNull("__class__")){
			name = aNode.get("__class__").asText();
		}else{
			name = "JsonModel" + RandomUtils.nextInt();
		}
		JDefinedClass myClass = makeClass(name, aNode);

		if (aNode.has("__persistent__") && aNode.get("__persistent__").asBoolean()) {
			JClass idType = null;
			for (Entry<String, JFieldVar> f : myClass.fields().entrySet()) {
				if (f.getKey().toLowerCase().equals("id")) {
					idType = f.getValue().type().boxify();
					break;
				}
			}
			if (idType != null) {
				makeDAO(myClass, idType);
				makeController(myClass, idType);
			}
			for (Table t : tables.values()) {
				StringWriter writer = new StringWriter();
				t.write(writer);
				rep.migrations.add(writer.toString());
			}
			for (Index i : indexes.values()) {
				StringWriter writer = new StringWriter();
				i.write(writer);
				rep.migrations.add(writer.toString());
			}
		}

		codeModel.build(new CodeWriter() {
			Map<String, Object> streams = new HashMap<>();

			@Override
			public OutputStream openBinary(JPackage pkg, String fileName) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				streams.put(fileName, baos);
				return baos;
			}

			@Override
			public Writer openSource(JPackage pkg, String fileName) {
				StringWriter writer = new StringWriter();
				streams.put(fileName, writer);
				return writer;
			}

			@Override
			public void close() {
				for (Entry<String, Object> s : streams.entrySet()) {
					JavaClassDescription jcd = new JavaClassDescription();
					jcd.name = s.getKey();
					jcd.contents = s.getValue().toString();
					if (jcd.name.endsWith("Controller")) {
						rep.controllerClass = jcd;
					} else if (jcd.name.endsWith("DAO")) {
						rep.daoClass = jcd;
					} else {
						rep.classes.add(jcd);
					}
				}
			}
		});
		return rep;
	}

	public JDefinedClass makeClassFromJson(String aClassName, JsonNode aNode, File aDestination) throws JClassAlreadyExistsException, IOException {
		JDefinedClass myClass = makeClass(aClassName, aNode);

		if (aNode.has("__persistent__") && aNode.get("__persistent__").asBoolean()) {
			JClass idType = null;
			for (Entry<String, JFieldVar> f : myClass.fields().entrySet()) {
				if (f.getKey().toLowerCase().equals("id")) {
					idType = f.getValue().type().boxify();
					break;
				}
			}
			if (idType != null) {
				makeDAO(myClass, idType);
				makeController(myClass, idType);
			}
			FileWriter writer = new FileWriter(new File("c:\\temp\\migration.sql"));
			for (Table t : tables.values()) {
				t.write(writer);
			}
			for (Index i : indexes.values()) {
				i.write(writer);
			}
			writer.close();
			System.out.println("migration.sql");
		}

		codeModel.build(aDestination);
		return myClass;
	}

	public JDefinedClass makeClassFromJson(String aClassName, String aJsonString, File aDestination) throws JsonParseException, JsonMappingException, IOException, JClassAlreadyExistsException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode node = objectMapper.readValue(aJsonString, JsonNode.class);
		return makeClassFromJson(aClassName, node, aDestination);
	}

	private JDefinedClass makeDAO(JDefinedClass aClass, JClass aIdType) throws JClassAlreadyExistsException {
		JDefinedClass dao = codeModel._class(aClass.name() + "DAO")._extends(codeModel.ref(AbstractDAO.class).narrow(aIdType, aClass));
		dao.annotate(Service.class);
		return dao;
	}

	private JDefinedClass makeController(JDefinedClass aClass, JClass aIdType) throws JClassAlreadyExistsException {
		JDefinedClass controller = codeModel._class(aClass.name() + "Controller")._extends(codeModel.ref(AbstractRestController.class).narrow(aIdType, aClass));
		controller.annotate(Controller.class);
		controller.annotate(RequestMapping.class);
		return controller;
	}

	private JDefinedClass makeClass(String aClassName, JsonNode aNode) throws JClassAlreadyExistsException {
		JDefinedClass newClass;
		try {
			newClass = codeModel._class(capitalize(aClassName));
		} catch (JClassAlreadyExistsException e) {
			return codeModel._getClass((capitalize(aClassName)));
		}
		boolean persistent = false;
		if (aNode.has("__persistent__")) {
			persistent = aNode.get("__persistent__").booleanValue();
		}
		newClass._implements(Serializable.class);
		if (aNode.has("abstract") && aNode.get("abstract").asBoolean()) {
			JAnnotationUse typeInfo = newClass.annotate(JsonTypeInfo.class);
			typeInfo.param("use", JsonTypeInfo.Id.CLASS);
			typeInfo.param("include", JsonTypeInfo.As.PROPERTY);
			typeInfo.param("property", "__class__");
		}
		Table table = new Table(newClass.name());
		Iterator<Entry<String, JsonNode>> i = aNode.fields();
		JClass idType = null;
		while (i.hasNext()) {
			Entry<String, JsonNode> entry = i.next();
			if (entry.getKey().startsWith("__")) {
				continue;
			}
			JClass clazz = determineType(entry.getValue(), entry.getKey(), capitalize(aClassName));
			JFieldVar field = addBeanField(newClass, entry.getKey(), clazz, persistent);
			Column c = table.addColumn(field);
			if (persistent && entry.getValue().isObject()) {
				indexes.put(table.name + "_" + field.name(), new Index(table, c, clazz));
			}
			if (field.name().toLowerCase().equals("id")) {
				idType = clazz;
			}
		}
		if (persistent) {
			newClass.annotate(Entity.class);
			newClass = newClass._implements(codeModel.ref(DataTransferObject.class).narrow(idType));
		}
		newClass.field(JMod.PRIVATE | JMod.STATIC | JMod.FINAL, long.class, "serialVersionUID").init(lit(RandomUtils.nextLong()));
		newClass.constructor(JMod.PUBLIC);
		newClass.javadoc().append("Autogenerated by APIOMatic.");
		addToString(newClass);

		if (persistent && !tables.containsKey(newClass.name())) {
			tables.put(table.name, table);
		}
		return newClass;
	}

	private void addToString(JDefinedClass newClass) {
		JMethod toStringMethod = newClass.method(JMod.PUBLIC, String.class, "toString");
		toStringMethod.annotate(Override.class);
		JInvocation append = _new(codeModel.ref(ToStringBuilder.class)).arg(_this().invoke("getClass"));
		for (Entry<String, JFieldVar> ref : newClass.fields().entrySet()) {
			if (ref.getKey().equals("serialVersionUID")) {
				continue;
			}
			append = append.invoke("append");
			append.arg(ref.getKey());
			append.arg(ref.getValue());
		}
		toStringMethod.body()._return(append.invoke("toString"));
	}

	private JClass determineType(JsonNode aNode, String aName, String aParentName) throws JClassAlreadyExistsException {
		if (aNode.isInt()) {
			return codeModel.ref(int.class);
		}
		if (aNode.isTextual()) {
			return codeModel.ref(String.class);
		}
		if (aNode.isBoolean()) {
			return codeModel.ref(boolean.class);
		}
		if (aNode.isLong()) {
			return codeModel.ref(long.class);
		}
		if (aNode.isFloatingPointNumber()) {
			return codeModel.ref(float.class);
		}
		if (aNode.isNumber()) {
			return codeModel.ref(long.class);
		}
		if (aNode.isArray()) {
			JClass list = codeModel.ref(List.class);
			if (aNode.elements().hasNext()) {
				JsonNode node = aNode.elements().next();
				list = list.narrow(determineType(node, aParentName + UUID.randomUUID().toString(), ""));
			}
			return list;
		}
		if (aNode.isObject()) {
			String name = aParentName + capitalize(sanitizeName(aName));
			if (aNode.has("__class__")) {
				name = aNode.get("__class__").textValue();
			}
			return makeClass(name, aNode);
		}
		throw new RuntimeException("Couldn't determine the type of " + aNode);
	}

	private JFieldVar addBeanField(JDefinedClass aClass, String aName, JClass aType, boolean aPersistent) {
		if (sanitizeName(aName).toLowerCase().equals("clazz")) {
			return null;
		}
		if (sanitizeName(aName).toLowerCase().equals("persistent")) {
			return null;
		}

		JFieldVar field = aClass.field(JMod.PRIVATE, aType, sanitizeName(aName));
		if (!aType.isPrimitive()) {
			field.annotate(OneToOne.class);
		}
		if (sanitizeName(aName).toLowerCase().equals("id") && aPersistent) {
			field.annotate(Id.class);
			field.init(codeModel.ref(UUID.class).staticInvoke("randomUUID").invoke("toString"));
		}
		if (!sanitizeName(aName).equals(aName)) {
			JAnnotationUse property = field.annotate(JsonProperty.class);
			property.param("value", aName);
		}
		JMethod getter = aClass.method(JMod.PUBLIC, aType, "get" + capitalize(sanitizeName(aName)));
		getter.body()._return(field);
		JMethod setter = aClass.method(JMod.PUBLIC, void.class, "set" + capitalize(sanitizeName(aName)));
		JVar var = setter.param(JMod.FINAL, aType, generateParameterName(aName, aType));
		setter.body().assign(_this().ref(field), var);
		return field;

	}

	private static String generateParameterName(String aParamName, JClass aType) {
		String name = capitalize(sanitizeName(aParamName));
		if (aType.name().startsWith("List") || aType.name().startsWith("Set")) {
			return "some" + name;
		}
		if (name.startsWith("A") || name.startsWith("E") || name.startsWith("I") || name.startsWith("O") || name.startsWith("U")) {
			return "an" + name;
		}
		return "a" + name;
	}

	private static String sanitizeName(String aString) {
		String name = WordUtils.uncapitalize(aString.replaceAll("class", "clazz"));
		String[] names = name.split("\\_");
		StringBuffer out = new StringBuffer();
		for (int x = 0; x < names.length; ++x) {
			if (x > 0) {
				out.append(capitalize(names[x]));
			} else {
				out.append(names[x]);
			}
		}
		String[] words = out.toString().split(":");
		return words[words.length - 1];
	}

	private static String capitalize(String aString) {
		String[] array = aString.split("\\.");
		StringBuffer out = new StringBuffer();
		for (int x = 0; x < array.length; ++x) {
			if (x == array.length - 1) {
				out.append(WordUtils.capitalize(array[x]));
			} else {
				out.append(array[x].toLowerCase());
				out.append(".");
			}
		}
		return out.toString();
	}
}
