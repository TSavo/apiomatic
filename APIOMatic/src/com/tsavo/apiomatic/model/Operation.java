package com.tsavo.apiomatic.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class Operation {
	List<String> urls;
	List<RequestMethod> methods;
	List<String> produces;
	List<String> consumes;
	List<String> headers;
	List<String> params;
	TypeDefinition body;
	TypeDefinition response;

	public Operation(Method aMethod) {
		Annotation[] methodAnnotations = aMethod.getAnnotations();
		for (Annotation annotation : methodAnnotations) {
			if (annotation instanceof RequestMapping) {
				RequestMapping requestMapping = (RequestMapping) annotation;
				urls = Arrays.asList(requestMapping.value());
				methods = Arrays.asList(requestMapping.method());
				produces = Arrays.asList(requestMapping.produces());
				consumes = Arrays.asList(requestMapping.consumes());
				headers = Arrays.asList(requestMapping.headers());
				params = Arrays.asList(requestMapping.params());
				continue;
			}
		}
		Annotation[][] parameterAnnotations = aMethod.getParameterAnnotations();
		Class<?>[] classes = aMethod.getParameterTypes();
		Type[] types = aMethod.getGenericParameterTypes();
		for (int x = 0; x < classes.length; ++x) {
			Class<?> clazz = classes[x];
			Type type = types[x];
			Annotation[] annotations = parameterAnnotations[x];
			for (Annotation annotation : annotations) {
				if (annotation instanceof RequestBody) {
					body = TypeDefinitionFactory.getTypeDefinition(clazz, annotations, types[x]);
				}
			}
		}
	}
}
