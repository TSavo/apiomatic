package com.tsavo.apiomatic.model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang.WordUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
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
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class JsonBeanGenerator {

	JCodeModel codeModel = new JCodeModel();
	
	public static void main(String[] args0) throws Exception {
		JsonBeanGenerator g = new JsonBeanGenerator();
		g.test();
	}

	public void test() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		makeClassFromJson(objectMapper.readValue(System.in, JsonNode.class), new File("c:\\tmp"));
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

	public JDefinedClass makeClass(String aClassName, JsonNode aNode) throws JClassAlreadyExistsException {
		Iterator<Entry<String, JsonNode>> i = aNode.fields();
		JDefinedClass newClass = codeModel._class(WordUtils.capitalize(aClassName));
		while (i.hasNext()) {
			Entry<String, JsonNode> entry = i.next();
			addBeanField(newClass, entry.getKey(), determineType(entry.getValue(), entry.getKey(), WordUtils.capitalize(aClassName)));
		}
		return newClass;
	}

	public JClass determineType(JsonNode aNode, String aName) throws JClassAlreadyExistsException {
		return determineType(aNode, aName, "");
	}

	public JClass determineType(JsonNode aNode, String aName, String aParentName) throws JClassAlreadyExistsException {
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
		if (aNode.isNumber()) {
			return codeModel.ref(Long.class);
		}
		if (aNode.isArray()) {
			JClass list = codeModel.ref(List.class);
			if (aNode.elements().hasNext()) {
				JsonNode node = aNode.elements().next();
				list = list.narrow(determineType(node, aParentName + UUID.randomUUID().toString()));
			}
			return list;
		}
		if (aNode.isObject()) {
			String name = aParentName + WordUtils.capitalize(sanitizeName(aName));
			if(aNode.has("__class__")){
				name = aNode.get("__class__").textValue();
			}
			return makeClass(name, aNode);
		}
		throw new RuntimeException("Couldn't determine the type of " + aNode);
	}

	public static String sanitizeName(String aString) {
		return WordUtils.uncapitalize(aString.replaceAll("\\_", "").replaceAll("class", "clazz"));
	}

	public void addBeanField(JDefinedClass aClass, String aName, Class<?> aType) {
		addBeanField(aClass, aName, codeModel.ref(aType));
	}

	public static void addBeanField(JDefinedClass aClass, String aName, JClass aType) {
		JFieldVar field = aClass.field(JMod.PRIVATE, aType, sanitizeName(aName));
		JAnnotationUse property = field.annotate(JsonProperty.class);
		property.param("value", aName);
		JMethod m = aClass.method(JMod.PUBLIC, aType, "get" + WordUtils.capitalize(sanitizeName(aName)));
		m.body()._return(field);
		JMethod setter = aClass.method(JMod.PUBLIC, void.class, "set" + WordUtils.capitalize(sanitizeName(aName)));
		JVar var = setter.param(aType, "a" + WordUtils.capitalize(sanitizeName(aName)));
		setter.body().assign(JExpr._this().ref(field), var);
	}
}
