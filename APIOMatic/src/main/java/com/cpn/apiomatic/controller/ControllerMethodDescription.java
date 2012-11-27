package com.cpn.apiomatic.controller;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cpn.apiomatic.annotation.Documentation;
import com.cpn.apiomatic.documentation.model.TypeDefinition;
import com.cpn.apiomatic.documentation.model.TypeDefinitionFactory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ControllerMethodDescription {
	public TypeDefinition getBody() {
		return requestBody;
	}

	public void setBody(TypeDefinition body) {
		this.requestBody = body;
	}

	public List<String> getConsumes() {
		return consumes;
	}

	public void setConsumes(List<String> consumes) {
		this.consumes = consumes;
	}

	public List<String> getHeaders() {
		return headers;
	}

	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}

	public List<RequestMethod> getMethods() {
		return supportedMethods;
	}

	public void setMethods(List<RequestMethod> methods) {
		this.supportedMethods = methods;
	}

	public List<String> getParams() {
		return urlParams;
	}

	public void setParams(List<String> params) {
		this.urlParams = params;
	}

	public List<String> getProduces() {
		return produces;
	}

	public void setProduces(List<String> produces) {
		this.produces = produces;
	}

	public TypeDefinition getResponse() {
		return responseBody;
	}

	public void setResponse(TypeDefinition response) {
		this.responseBody = response;
	}

	public List<String> getUrls() {
		return urls;
	}

	public void setUrls(List<String> urls) {
		this.urls = urls;
	}

	TypeDefinition requestBody;
	List<String> consumes;
	List<String> headers;
	List<RequestMethod> supportedMethods;
	List<String> urlParams;
	List<String> produces;
	TypeDefinition responseBody;
	List<String> urls;
	String documentation;

	@JsonIgnore
	Set<Class<?>> typeRefs = new HashSet<Class<?>>();

	public String getDocumentation() {
		return documentation;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	public ControllerMethodDescription(final Method aMethod) {
		final Annotation[] methodAnnotations = aMethod.getAnnotations();
		for (final Annotation annotation : methodAnnotations) {
			if (annotation instanceof RequestMapping) {
				final RequestMapping requestMapping = (RequestMapping) annotation;
				urls = Arrays.asList(requestMapping.value());
				supportedMethods = Arrays.asList(requestMapping.method());
				produces = Arrays.asList(requestMapping.produces());
				consumes = Arrays.asList(requestMapping.consumes());
				headers = Arrays.asList(requestMapping.headers());
				urlParams = Arrays.asList(requestMapping.params());
				continue;
			}
			if (annotation instanceof Documentation) {
				documentation = ((Documentation) annotation).value();
			}
		}
		final Annotation[][] parameterAnnotations = aMethod.getParameterAnnotations();
		final Class<?>[] classes = aMethod.getParameterTypes();
		final Type[] types = aMethod.getGenericParameterTypes();
		for (int x = 0; x < classes.length; ++x) {
			final Class<?> clazz = classes[x];
			final Annotation[] annotations = parameterAnnotations[x];
			for (final Annotation annotation : annotations) {
				if (annotation instanceof RequestBody) {
					requestBody = TypeDefinitionFactory.getTypeDefinitionWithReference(clazz, types[x]);
				}
			}
		}
		responseBody = TypeDefinitionFactory.getTypeDefinitionWithReference(aMethod.getReturnType(), aMethod.getGenericReturnType());
		
		if(requestBody != null){
			typeRefs.addAll(requestBody.typeRefs);
		}
		if(responseBody != null){
			typeRefs.addAll(responseBody.typeRefs);
		}
	}
}
