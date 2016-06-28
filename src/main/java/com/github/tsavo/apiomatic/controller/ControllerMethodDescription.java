package com.github.tsavo.apiomatic.controller;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.tsavo.apiomatic.annotation.Documentation;
import com.github.tsavo.apiomatic.documentation.model.TypeDefinition;
import com.github.tsavo.apiomatic.documentation.model.TypeDefinitionFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Request;

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
	List<String> headers = new ArrayList<>();
	List<RequestMethod> supportedMethods = new ArrayList<>();
	List<String> urlParams = new ArrayList<>();
    List<String> cookies = new ArrayList<>();
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
			if(annotation instanceof Path){
				final Path path = (Path) annotation;
				urls = Arrays.asList(path.value());
			}
			if(annotation instanceof Consumes){
				final Consumes consumesAnnotation = (Consumes) annotation;
				consumes = Arrays.asList(consumesAnnotation.value());
			}
			if(annotation instanceof Produces){
				final Produces producesAnnotation = (Produces) annotation;
				produces = Arrays.asList(producesAnnotation.value());
			}
			if(annotation instanceof GET){
				supportedMethods.add(RequestMethod.GET);
			}
			if (annotation instanceof PUT) {
				supportedMethods.add(RequestMethod.PUT);
			}
			if(annotation instanceof POST){
				supportedMethods.add(RequestMethod.POST);
			}
			if(annotation instanceof DELETE){
				supportedMethods.add(RequestMethod.DELETE);
			}
			if(annotation instanceof OPTIONS) {
				supportedMethods.add(RequestMethod.OPTIONS);
			}
			if(annotation instanceof HEAD){
				supportedMethods.add(RequestMethod.HEAD);
			}
			if(annotation instanceof HttpMethod){
				final HttpMethod httpMethod = (HttpMethod) annotation;
				supportedMethods.add(RequestMethod.valueOf(httpMethod.value()));
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
				if (annotation instanceof RequestBody || annotation instanceof Request) {
					requestBody = TypeDefinitionFactory.getTypeDefinitionWithReference(clazz, types[x]);
				}
                if(annotation instanceof PathParam){
                    final PathParam pathParam = (PathParam) annotation;
                    urlParams.add(pathParam.value());
                }
                if(annotation instanceof HeaderParam){
                    final HeaderParam headerParam = (HeaderParam) annotation;
                    headers.add(headerParam.value());
                }
                if(annotation instanceof CookieParam){
                    final CookieParam cookieParam = (CookieParam) annotation;
                    cookies.add(cookieParam.value());
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
