package com.tsavo.apiomatic.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.tsavo.apiomatic.annotation.Documentation;

@JsonInclude(Include.NON_NULL)
public class Operation {
	public TypeDefinition getBody() {
		return body;
	}

	public void setBody(TypeDefinition body) {
		this.body = body;
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
		return methods;
	}

	public void setMethods(List<RequestMethod> methods) {
		this.methods = methods;
	}

	public List<String> getParams() {
		return params;
	}

	public void setParams(List<String> params) {
		this.params = params;
	}

	public List<String> getProduces() {
		return produces;
	}

	public void setProduces(List<String> produces) {
		this.produces = produces;
	}

	public TypeDefinition getResponse() {
		return response;
	}

	public void setResponse(TypeDefinition response) {
		this.response = response;
	}

	public List<String> getUrls() {
		return urls;
	}

	public void setUrls(List<String> urls) {
		this.urls = urls;
	}

	TypeDefinition body;
	List<String> consumes;
	List<String> headers;
	List<RequestMethod> methods;
	List<String> params;
	List<String> produces;
	TypeDefinition response;
	List<String> urls;
	String documentation;
	
	public String getDocumentation() {
		return documentation;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	public Operation(final Method aMethod) {
		final Annotation[] methodAnnotations = aMethod.getAnnotations();
		for (final Annotation annotation : methodAnnotations) {
			if (annotation instanceof RequestMapping) {
				final RequestMapping requestMapping = (RequestMapping) annotation;
				urls = Arrays.asList(requestMapping.value());
				methods = Arrays.asList(requestMapping.method());
				// produces = Arrays.asList(requestMapping.produces());
				// consumes = Arrays.asList(requestMapping.consumes());
				headers = Arrays.asList(requestMapping.headers());
				params = Arrays.asList(requestMapping.params());
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
					body = TypeDefinitionFactory.getTypeDefinition(clazz, annotations, types[x]);
				}
			}
		}
		response = TypeDefinitionFactory.getTypeDefinition(aMethod.getReturnType(), aMethod.getAnnotations(), aMethod.getGenericReturnType());
	}
}
