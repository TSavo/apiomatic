package com.tsavo.apiomatic.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

import com.cpn.type.TypeResolver;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.Optional;
import com.tsavo.apiomatic.annotation.Null;

public class TypeDefinitionFactory {
	public static TypeDefinition getTypeDefinition(final Class<?> clazz, final Annotation[] someAnnotations,
			final Type aType) {
		TypeDefinition type;
		if (clazz.equals(String.class)) {
			return new StringType();
		}
		if (clazz.equals(Integer.class) || clazz.equals(Long.class) || clazz.equals(Short.class)
				|| clazz.equals(BigInteger.class) || clazz.getName().equals("int") || clazz.getName().equals("short")
				|| clazz.getName().equals("long")) {
			return new IntegerType();
		}
		if (clazz.equals(Float.class) || clazz.equals(Double.class) || clazz.getName().equals("float")
				|| clazz.getName().equals("double")) {
			return new NumberType();
		}
		if (clazz.equals(List.class) || clazz.equals(Set.class)) {
			Type typeInfo = TypeResolver.resolveGenericType(aType, clazz);
			if(typeInfo instanceof ParameterizedType){
				ParameterizedType pType = (ParameterizedType) typeInfo;
				Type gType = pType.getActualTypeArguments()[0];
				return new ArrayType(TypeDefinitionFactory.getTypeDefinition((Class<?>) pType.getActualTypeArguments()[0], someAnnotations, aType));
			}
		}

		ObjectType oType = new ObjectType(clazz);
		type = oType;
		if ((clazz.getModifiers() & Modifier.ABSTRACT) == Modifier.ABSTRACT) {
			oType.setAbstractClass(true);
		}
		outer: for (final Field f : clazz.getDeclaredFields()) {
			if (f.toString().contains("static")) {
				continue;
			}

			String name = f.getName();
			for (final Annotation a : f.getAnnotations()) {
				if (a instanceof JsonProperty) {
					name = ((JsonProperty) a).value();
				}
				if (a instanceof JsonIgnore) {
					continue outer;
				}
			}
			final TypeDefinition innerType = TypeDefinitionFactory.getTypeDefinition(f.getType(), f.getAnnotations(),
					f.getGenericType());
			oType.addProperty(name, innerType);
		}
		if (clazz.getPackage() != null) {
			Reflections reflections = new Reflections(clazz.getPackage().getName());
			Set<?> subTypes = reflections.getSubTypesOf(clazz);
			Iterator<?> i = subTypes.iterator();
			while (i.hasNext()) {
				Class<?> c = (Class<?>) i.next();
				oType.getSubclasses().add(getTypeDefinition(c, c.getAnnotations(), c));
			}
		}
		for (final Annotation annotation : clazz.getAnnotations()) {
			if (annotation instanceof JsonTypeInfo) {
				JsonTypeInfo typeInfo = (JsonTypeInfo) annotation;
				StringType stringType = new StringType();
				stringType.setOptional(false);
				stringType.setRequiredValue(clazz.getCanonicalName());
				oType.addProperty(typeInfo.property(), stringType);
			}
		}
		for (final Annotation annotation : someAnnotations) {
			if (annotation instanceof Null) {
				type = new NullType();
			}
		}
		for (final Annotation annotation : someAnnotations) {
			if (annotation instanceof Optional) {
				type.setOptional(((com.tsavo.apiomatic.annotation.Optional) annotation).optional());
			}
		}
		return type;
	}
}
