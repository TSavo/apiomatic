package com.cpn.apiomatic.rest;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cpn.apiomatic.generator.RestController;

public abstract class AbstractRestController<T extends DataTransferObject> {

	@PersistenceContext
	protected EntityManager entityManager;

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public @ResponseBody
	T add(@RequestBody final T aT) throws Exception {
		entityManager.persist(aT);
		return aT;
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	@Transactional
	public @ResponseBody
	void delete(@PathVariable final String id) {
		entityManager.remove(entityManager.find(getPersistenceClass(), id));
	}

	@SuppressWarnings("unchecked")
	public final Class<T> getPersistenceClass() {
		return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.GET)
	@Transactional
	public @ResponseBody
	T[] list() throws IOException {
		return (T[]) entityManager.createQuery("from " + getPersistenceClass().getName(), getPersistenceClass()).getResultList().toArray();

	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@Transactional
	public @ResponseBody
	T show(@PathVariable final String id) {
		return entityManager.find(getPersistenceClass(), id);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@Transactional
	public @ResponseBody
	T update(@RequestBody final T aT) {
		return entityManager.merge(aT);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/services")
	public @ResponseBody
	RestController servicesHelp() {
		return new RestController(this.getClass());
	}
}
