package com.github.tsavo.apiomatic.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;


public class AbstractServiceDescriptionController implements ApplicationContextAware {
	@Autowired
	ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext aContext) throws BeansException {
		context = aContext;
	}

	@RequestMapping(method = { RequestMethod.GET, RequestMethod.OPTIONS })
	@Path("/")
	@GET
	@OPTIONS
	public @ResponseBody
	List<ServiceDescription> getServiceList() {
		List<ServiceDescription> results = new ArrayList<>();
		for (Entry<String, Object> e : context.getBeansWithAnnotation(Path.class).entrySet()) {
			results.add(new ServiceDescription(e.getKey(), ClassUtils.getUserClass(e.getValue().getClass())));
		}
		return results;
	}

	@RequestMapping(value = "/{name}", method = { RequestMethod.GET, RequestMethod.OPTIONS })
	@Path("/{name}")
	@GET
	@OPTIONS
	public @ResponseBody
	ControllerDescription getRestControllerForClass(@PathVariable("name") @PathParam("name") String aName) {
		return new ControllerDescription(ClassUtils.getUserClass(context.getBean(aName).getClass()));
	}

}
