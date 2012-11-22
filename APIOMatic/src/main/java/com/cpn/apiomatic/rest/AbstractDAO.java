package com.cpn.apiomatic.rest;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public abstract class AbstractDAO<T extends DataTransferObject> {
	@PersistenceContext
	protected EntityManager entityManager;

	public T find(final Object o) {
		return entityManager.find(getDTOClass(), o);
	}
	
	public T find(final Object o, final int id){
		return entityManager.find(getDTOClass(), id);
	}

	@SuppressWarnings("unchecked")
	public final Class<T> getDTOClass() {
		return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public List<T> list() {
		return entityManager.createQuery("from " + getDTOClass().getName(), getDTOClass()).getResultList();
	}

	public T merge(final T aT) {
		return entityManager.merge(aT);
	}

	public void persist(final T aT) {
		entityManager.persist(aT);
	}

	public T persistOrMerge(final T aT) {
		if (entityManager.find(getDTOClass(), aT.getId()) != null) {
			return entityManager.merge(aT);
		}
		entityManager.persist(aT);
		return aT;
	}

	public void remove(final T aT) {
		entityManager.remove(aT);
	}
	
	public void removeAll(final Collection<T> aT){
		for (T t: aT){
			entityManager.remove(t);
		}
	}
	
	public T findById(final String id) {
		try {
			return entityManager.find(getDTOClass(), id);
		} catch (final Exception e) {
			return null;
		}
	}
}
