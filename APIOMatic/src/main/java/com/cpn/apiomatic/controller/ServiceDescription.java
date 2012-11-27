package com.cpn.apiomatic.controller;

import java.io.Serializable;

import org.springframework.web.bind.annotation.RequestMapping;

import com.cpn.apiomatic.annotation.Documentation;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceDescription implements Serializable {

	private static final long serialVersionUID = -8301521788816167944L;
	public String name;
	@JsonProperty("class")
	public String clazz;
	public String baseUrl;
	public String documentation;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getServiceDescriptionUrl() {
		return baseUrl;
	}

	public void setServiceDescriptionUrl(String serviceDescriptionUrl) {
		this.baseUrl = serviceDescriptionUrl;
	}

	public String getDocumentation() {
		return documentation;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	public ServiceDescription(String aName, Class<?> aClazz) {
		clazz = aClazz.getCanonicalName();
		name = aName;
		RequestMapping requestMapping = aClazz.getAnnotation(RequestMapping.class);
		if (requestMapping != null && requestMapping.value().length > 0) {
			baseUrl = requestMapping.value()[0];
		}
		Documentation documentationAnnotation = aClazz.getAnnotation(Documentation.class);
		if (documentationAnnotation != null) {
			documentation = documentationAnnotation.value();
		}
	}

}
