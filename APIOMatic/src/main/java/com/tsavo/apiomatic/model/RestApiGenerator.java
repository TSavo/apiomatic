package com.tsavo.apiomatic.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;

import com.tsavo.apiomatic.annotation.Documentation;

public class RestApiGenerator {
	public List<String> urls;
	public String documentation;
	public String packageName;
	public List<Operation> apiList = new ArrayList();

	public RestApiGenerator(final Class aClazz) {
			Package aPackage = aClazz.getPackage();
			packageName = aPackage.getName();
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
				ANNOTATIONS: for (final Annotation annotation : aMethod.getAnnotations()) {
					if (annotation instanceof RequestMapping) {
						apiList.add(new Operation(aMethod));
						break ANNOTATIONS;
					}
				}
			}
	}
}