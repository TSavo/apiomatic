package com.cpn.apiomatic.generator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;

import com.cpn.apiomatic.annotation.Documentation;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class RestController {
	public List<String> urls;
	public String documentation;
	public String packageName;
	public List<RestControllerMethod> methods = new ArrayList<>();

	public RestController(final Class<?> aClazz) {
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
					methods.add(new RestControllerMethod(aMethod));
					break;
				}
			}
		}
	}
}