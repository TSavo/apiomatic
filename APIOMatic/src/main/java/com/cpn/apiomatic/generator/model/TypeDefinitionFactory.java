package com.cpn.apiomatic.generator.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.Temporal;

import org.reflections.Reflections;
import org.springframework.util.ClassUtils;

import com.cpn.apiomatic.annotation.Null;
import com.cpn.apiomatic.annotation.Optional;
import com.cpn.apiomatic.type.TypeResolver;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

public class TypeDefinitionFactory {

	private static Set<Class<?>> stopClasses = new HashSet<>();

	static {
		stopClasses.add(String.class);
		stopClasses.add(Integer.class);
		stopClasses.add(Long.class);
		stopClasses.add(Short.class);
		stopClasses.add(int.class);
		stopClasses.add(long.class);
		stopClasses.add(short.class);
		stopClasses.add(Float.class);
		stopClasses.add(Double.class);
		stopClasses.add(float.class);
		stopClasses.add(double.class);
	}

	public static TypeDefinition getTypeDefinitionWithReference(final Class<?> clazz, final Type aType) {
		Class<?> baseClazz = ClassUtils.getUserClass(clazz);
		if (baseClazz.equals(void.class)) {
			return null;
		}
		if (baseClazz.equals(String.class)) {
			return new StringType();
		}
		if (baseClazz.equals(Integer.class) || baseClazz.equals(Long.class) || baseClazz.equals(Short.class) || baseClazz.equals(int.class) || baseClazz.equals(long.class) || baseClazz.equals(short.class)) {
			return new IntegerType();
		}
		if (baseClazz.equals(Float.class) || baseClazz.equals(Double.class) || baseClazz.equals(float.class) || baseClazz.equals(double.class)) {
			return new NumberType();
		}
		if (baseClazz.isAssignableFrom(List.class) || baseClazz.isAssignableFrom(Set.class)) {
			Type typeInfo = TypeResolver.resolveGenericType(aType, baseClazz);
			if (typeInfo instanceof ParameterizedType) {
				ParameterizedType pType = (ParameterizedType) typeInfo;
				Type gType = pType.getActualTypeArguments()[0];
				return new ArrayType(TypeDefinitionFactory.getTypeDefinitionWithReference((Class<?>) gType, aType));
			}
		}
		if (baseClazz.isArray()) {
			return new ArrayType(TypeDefinitionFactory.getTypeDefinitionWithReference(baseClazz.getComponentType(), baseClazz.getComponentType().getGenericSuperclass()));
		}
		TypeRefType typeRef = new TypeRefType(baseClazz);
		outer: for (final Field f : baseClazz.getDeclaredFields()) {
			if (f.toString().contains("static")) {
				continue;
			}
			for (final Annotation a : f.getAnnotations()) {
				if (a instanceof JsonIgnore) {
					continue outer;
				}
			}
			if (stopClasses.contains(f.getType())){
				continue;
			}
			typeRef.typeRefs.add(f.getType());
		}
		return typeRef;

	}

	public static TypeDefinition getTypeDefinition(final Class<?> clazz, final Annotation[] someAnnotations, final Type aType) {
		TypeDefinition type;
		Class<?> baseClazz = ClassUtils.getUserClass(clazz);
		if (baseClazz.equals(void.class)) {
			return null;
		}
		if (baseClazz.equals(String.class)) {
			return new StringType();
		}
		if (baseClazz.equals(Integer.class) || baseClazz.equals(Long.class) || baseClazz.equals(Short.class) || baseClazz.equals(int.class) || baseClazz.equals(long.class) || baseClazz.equals(short.class)) {
			return new IntegerType();
		}
		if (baseClazz.equals(Float.class) || baseClazz.equals(Double.class) || baseClazz.equals(float.class) || baseClazz.equals(double.class)) {
			return new NumberType();
		}
		if (baseClazz.isAssignableFrom(List.class) || baseClazz.isAssignableFrom(Set.class)) {
			Type typeInfo = TypeResolver.resolveGenericType(aType, baseClazz);
			if (typeInfo instanceof ParameterizedType) {
				ParameterizedType pType = (ParameterizedType) typeInfo;
				Type gType = pType.getActualTypeArguments()[0];
				return new ArrayType(TypeDefinitionFactory.getTypeDefinitionWithReference((Class<?>) gType, aType));
			}
		}
		if (baseClazz.isArray()) {
			return new ArrayType(TypeDefinitionFactory.getTypeDefinitionWithReference(baseClazz.getComponentType(), baseClazz.getComponentType().getGenericSuperclass()));
		}

		ObjectType oType = new ObjectType(baseClazz);
		type = oType;
		if ((baseClazz.getModifiers() & Modifier.ABSTRACT) == Modifier.ABSTRACT) {
			oType.setAbstractClass(true);
		}
		outer: for (final Field f : baseClazz.getDeclaredFields()) {
			if (f.toString().contains("static")) {
				continue;
			}

			String name = f.getName();
			for (final Annotation a : f.getAnnotations()) {
				if (a instanceof JsonIgnore) {
					continue outer;
				}
			}
			for (final Annotation a : f.getAnnotations()) {
				if (a instanceof JsonProperty) {
					name = ((JsonProperty) a).value();
				}
				if (a instanceof Temporal) {
					TypeDefinition i = new StringType();
					i.setName(name);
					oType.addProperty(i);
					continue outer;
				}
			}
			final TypeDefinition innerType = getTypeDefinitionWithReference(f.getType(), f.getGenericType());
			innerType.setName(name);
			oType.addProperty(innerType);
			oType.typeRefs.addAll(innerType.typeRefs);
		}
		if (baseClazz.getPackage() != null) {
			Reflections reflections = new Reflections(baseClazz.getPackage().getName());
			Set<?> subTypes = reflections.getSubTypesOf(baseClazz);
			Iterator<?> i = subTypes.iterator();
			while (i.hasNext()) {
				Class<?> c = (Class<?>) i.next();
				oType.getSubclasses().add(getTypeDefinitionWithReference(c, c));
				oType.typeRefs.add(c);
			}
		}
		for (final Annotation annotation : baseClazz.getAnnotations()) {
			if (annotation instanceof JsonTypeInfo) {
				JsonTypeInfo typeInfo = (JsonTypeInfo) annotation;
				StringType stringType = new StringType();
				stringType.setOptional(false);
				stringType.setRequiredValue(baseClazz.getCanonicalName());
				stringType.setName(typeInfo.property());
				oType.addProperty(stringType);
			}
		}
		for (final Annotation annotation : someAnnotations) {
			if (annotation instanceof Null) {
				type = new NullType();
			}
		}
		for (final Annotation annotation : someAnnotations) {
			if (annotation instanceof Optional) {
				type.setOptional(((Optional) annotation).optional());
			}
		}
		return type;
	}
}
