package com.tsavo.apiomatic.model;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.math.RandomUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class JsonBeanGenerator {

	private JCodeModel codeModel = new JCodeModel();

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

	public JDefinedClass makeClassFromJson(String aClassName, JsonNode aNode, File aDestination) throws JClassAlreadyExistsException, IOException {
		JDefinedClass myClass = makeClass(aClassName, aNode);
		codeModel.build(aDestination);
		return myClass;
	}

	public JDefinedClass makeClassFromJson(String aClassName, String aJsonString, File aDestination) throws JsonParseException, JsonMappingException, IOException, JClassAlreadyExistsException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode node = objectMapper.readValue(aJsonString, JsonNode.class);
		return makeClassFromJson(aClassName, node, aDestination);
	}

	private JDefinedClass makeClass(String aClassName, JsonNode aNode) throws JClassAlreadyExistsException {
		Iterator<Entry<String, JsonNode>> i = aNode.fields();
		JDefinedClass newClass;
		try {
			newClass = codeModel._class(capitalize(aClassName));
		} catch (JClassAlreadyExistsException e) {
			return codeModel._getClass((capitalize(aClassName)));
		}
		newClass._implements(Serializable.class);
		if (aNode.has("abstract") && aNode.get("abstract").asBoolean()) {
			JAnnotationUse typeInfo = newClass.annotate(JsonTypeInfo.class);
			typeInfo.param("use", JsonTypeInfo.Id.CLASS);
			typeInfo.param("include", JsonTypeInfo.As.PROPERTY);
			typeInfo.param("property", "__class__");
		}
		while (i.hasNext()) {
			Entry<String, JsonNode> entry = i.next();
			addBeanField(newClass, entry.getKey(), determineType(entry.getValue(), entry.getKey(), capitalize(aClassName)));
		}
		newClass.field(JMod.PRIVATE | JMod.STATIC | JMod.FINAL, long.class, "serialVersionUID").init(JExpr.lit(RandomUtils.nextLong()));
		newClass.constructor(JMod.PUBLIC);
		newClass.javadoc().append("Autogenerated by APIOMatic.");
		addToString(newClass);

		return newClass;
	}

	private void addToString(JDefinedClass newClass) {
		JMethod toStringMethod = newClass.method(JMod.PUBLIC, String.class, "toString");
		toStringMethod.annotate(Override.class);
		JInvocation append = JExpr._new(codeModel.ref(ToStringBuilder.class)).arg(JExpr._this().invoke("getClass"));
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
			return codeModel.ref(Integer.class);
		}
		if (aNode.isTextual()) {
			return codeModel.ref(String.class);
		}
		if (aNode.isBoolean()) {
			return codeModel.ref(Boolean.class);
		}
		if (aNode.isLong()) {
			return codeModel.ref(Long.class);
		}
		if (aNode.isFloatingPointNumber()) {
			return codeModel.ref(Float.class);
		}
		if (aNode.isNumber()) {
			return codeModel.ref(Long.class);
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

	private static void addBeanField(JDefinedClass aClass, String aName, JClass aType) {
		if (sanitizeName(aName).toLowerCase().equals("clazz")) {
			return;
		}
		JFieldVar field = aClass.field(JMod.PRIVATE, aType, sanitizeName(aName));
		if (!sanitizeName(aName).equals(aName)) {
			JAnnotationUse property = field.annotate(JsonProperty.class);
			property.param("value", aName);
		}
		JMethod getter = aClass.method(JMod.PUBLIC, aType, "get" + capitalize(sanitizeName(aName)));
		getter.body()._return(field);
		JMethod setter = aClass.method(JMod.PUBLIC, void.class, "set" + capitalize(sanitizeName(aName)));
		JVar var = setter.param(aType, generateParameterName(aName, aType));
		setter.body().assign(JExpr._this().ref(field), var);
	}

	private static String generateParameterName(String aParamName, JClass aType) {
		String name = capitalize(sanitizeName(aParamName));
		if (aType.name().startsWith("List")) {
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
