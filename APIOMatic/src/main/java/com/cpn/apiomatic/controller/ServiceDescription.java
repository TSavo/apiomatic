package com.cpn.apiomatic.controller;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.springframework.web.bind.annotation.RequestMapping;

import com.cpn.apiomatic.annotation.Documentation;
import com.cpn.apiomatic.generator.RestController;

public class ServiceDescription implements Serializable {

	private static final long serialVersionUID = -8301521788816167944L;
	public String name;
	public String serviceDescriptionUrl;
	public String documentation;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getServiceDescriptionUrl() {
		return serviceDescriptionUrl;
	}

	public void setServiceDescriptionUrl(String serviceDescriptionUrl) {
		this.serviceDescriptionUrl = serviceDescriptionUrl;
	}

	public String getDocumentation() {
		return documentation;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	public ServiceDescription(Class<?> aClazz) {
		name = aClazz.getCanonicalName();
		RequestMapping requestMapping = aClazz.getAnnotation(RequestMapping.class);
		String baseUrl = "";
		String helpUrl = "";
		if (requestMapping != null && requestMapping.value().length > 0) {
			baseUrl = requestMapping.value()[0];
		}
		for (Method m : aClazz.getMethods()) {
			if (m.getReturnType().getClass().equals(RestController.class)) {
				RequestMapping methodMapping = m.getAnnotation(RequestMapping.class);
				if (methodMapping != null && methodMapping.value().length > 0)
					helpUrl = methodMapping.value()[0];
				break;
			}
		}
		serviceDescriptionUrl = baseUrl + helpUrl;
		Documentation documentationAnnotation = aClazz.getAnnotation(Documentation.class);
		if (documentationAnnotation != null) {
			documentation = documentationAnnotation.value();
		}
	}

}
