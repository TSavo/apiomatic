package com.github.tsavo.apiomatic.controller;

import java.io.Serializable;

import org.springframework.web.bind.annotation.RequestMapping;

import com.github.tsavo.apiomatic.annotation.Documentation;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.ws.rs.Path;

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
		Path path = aClazz.getAnnotation(Path.class);
		if (requestMapping != null && requestMapping.value().length > 0) {
			baseUrl = requestMapping.value()[0];
		}else if(path != null && path.value().length() > 0){
			baseUrl = path.value();
		}
		Documentation documentationAnnotation = aClazz.getAnnotation(Documentation.class);
		if (documentationAnnotation != null) {
			documentation = documentationAnnotation.value();
		}
	}

}
