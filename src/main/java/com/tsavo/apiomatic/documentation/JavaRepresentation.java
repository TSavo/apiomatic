package com.tsavo.apiomatic.documentation;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class JavaRepresentation {

	public JsonNode originalJson;
	public List<JavaClassDescription> classes = new ArrayList<>();
	public List<String> migrations = new ArrayList<>();
	public JavaClassDescription daoClass;
	public JavaClassDescription controllerClass;
	public JsonNode getOriginalJson() {
		return originalJson;
	}
	public void setOriginalJson(JsonNode originalJson) {
		this.originalJson = originalJson;
	}
	public List<JavaClassDescription> getClasses() {
		return classes;
	}
	public void setClasses(List<JavaClassDescription> classes) {
		this.classes = classes;
	}
	public List<String> getMigrations() {
		return migrations;
	}
	public void setMigrations(List<String> migrations) {
		this.migrations = migrations;
	}
	public JavaClassDescription getDaoClass() {
		return daoClass;
	}
	public void setDaoClass(JavaClassDescription daoClass) {
		this.daoClass = daoClass;
	}
	public JavaClassDescription getControllerClass() {
		return controllerClass;
	}
	public void setControllerClass(JavaClassDescription controllerClass) {
		this.controllerClass = controllerClass;
	}
	
}
