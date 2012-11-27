package com.cpn.apiomatic.controller;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.RequestMapping;

import com.cpn.apiomatic.annotation.Documentation;
import com.cpn.apiomatic.documentation.model.TypeDefinition;
import com.cpn.apiomatic.documentation.model.TypeDefinitionFactory;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ControllerDescription {
	public List<String> urls;
	public String documentation;
	public String packageName;
	public List<ControllerMethodDescription> methods = new ArrayList<>();
	public List<TypeDefinition> typeDefinitions = new ArrayList<>();
	public List<String> getUrls() {
		return urls;
	}

	public void setUrls(List<String> urls) {
		this.urls = urls;
	}

	public String getDocumentation() {
		return documentation;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public List<ControllerMethodDescription> getMethods() {
		return methods;
	}

	public void setMethods(List<ControllerMethodDescription> methods) {
		this.methods = methods;
	}

	public List<TypeDefinition> getTypeDefinitions() {
		return typeDefinitions;
	}

	public void setTypeDefinitions(List<TypeDefinition> typeDefinitions) {
		this.typeDefinitions = typeDefinitions;
	}

	private Set<Class<?>> doneClasses = new HashSet<>();
	private Set<Class<?>> remainingClasses = new HashSet<>();

	public ControllerDescription(final Class<?> aClazz) {
		packageName = aClazz.getPackage().getName();
		final Annotation[] classAnnotations = aClazz.getAnnotations();
		for (final Annotation annotation : classAnnotations) {
			if (annotation instanceof RequestMapping) {
				final RequestMapping requestMapping = (RequestMapping) annotation;
				urls = Arrays.asList(requestMapping.value());
			}
			if (annotation instanceof Documentation) {
				documentation = ((Documentation) annotation).value();
			}
		}
		Method[] aMethods = aClazz.getMethods();
		for (final Method aMethod : aMethods) {
			for (final Annotation annotation : aMethod.getAnnotations()) {
				if (annotation instanceof RequestMapping && !aMethod.getReturnType().equals(getClass())) {
					methods.add(new ControllerMethodDescription(aMethod));
					break;
				}
			}
		}
		for (ControllerMethodDescription m : methods) {
			remainingClasses.addAll(m.typeRefs);
		}
		while (!remainingClasses.isEmpty()) {
			Class<?> c = remainingClasses.iterator().next();
			doneClasses.add(c);
			remainingClasses.remove(c);
			TypeDefinition t = TypeDefinitionFactory.getTypeDefinition(c, c.getAnnotations(), c);
			t.typeRefs.removeAll(doneClasses);
			remainingClasses.addAll(t.typeRefs);
			typeDefinitions.add(t);
		}
	}
}