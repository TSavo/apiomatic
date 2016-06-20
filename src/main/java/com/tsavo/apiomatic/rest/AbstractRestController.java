package com.tsavo.apiomatic.rest;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

public abstract class AbstractRestController<IdType, DTOType extends DataTransferObject<IdType>> {

	@PersistenceContext
	protected EntityManager entityManager;

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public @ResponseBody
	DTOType add(@RequestBody final DTOType aT) {
		entityManager.persist(aT);
		return aT;
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	@Transactional
	public @ResponseBody
	void delete(@PathVariable final IdType id) {
		entityManager.remove(entityManager.find(getPersistenceClass(), id));
	}

	@SuppressWarnings("unchecked")
	public final Class<DTOType> getPersistenceClass() {
		return (Class<DTOType>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	/*@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.GET)
	@Transactional
	public @ResponseBody
	DTOType[] list() {
		return (DTOType[]) entityManager.createQuery("from " + getPersistenceClass().getName(), getPersistenceClass()).getResultList().toArray();
	}*/

	@SuppressWarnings("unchecked")
	public final Class<DTOType> getDTOClass() {
		return (Class<DTOType>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
	}

	@RequestMapping(method = RequestMethod.GET)
	@Transactional
	public @ResponseBody
	List<DTOType> list() {
		return entityManager.createQuery("from " + getDTOClass().getName(), getDTOClass()).getResultList();
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@Transactional
	public @ResponseBody
	DTOType show(@PathVariable final IdType id) {
		return entityManager.find(getPersistenceClass(), id);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@Transactional
	public @ResponseBody
	DTOType update(@RequestBody final DTOType aT) {
		return entityManager.merge(aT);
	}

}
