package com.sunnysuperman.mountain.lang.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.mountain.lang.exception.UnexpectedException;

public final class Types {
	private static final Logger LOG = LoggerFactory.getLogger(Types.class);

	private Types() {
	}

	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<?> type) {
		try {
			return (T) type.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new UnexpectedException(e);
		}
	}

	public static ParameterizedType findParameterizedType(Class<?> clazz, Class<?> parameterizedClass) {
		while (true) {
			Type superType = clazz.getGenericSuperclass();
			if (superType == null || superType == Object.class) {
				return null;
			}
			if (superType instanceof ParameterizedType) {
				ParameterizedType ptype = (ParameterizedType) superType;
				if (ptype.getRawType() == parameterizedClass) {
					return ptype;
				}
				clazz = (Class<?>) ptype.getRawType();
			} else {
				clazz = (Class<?>) superType;
			}
		}
	}

	public static boolean isByteArray(Class<?> type) {
		return type.isArray() && type.getComponentType().equals(byte.class);
	}

	public static Set<Class<?>> findTypesAnnotatedWith(String[] packages, boolean inherited, Class<?>... annotations) {
		validatePackages(packages);
		if (LOG.isInfoEnabled()) {
			LOG.info("findTypesAnnotatedWith: {} in packages: {}", Str.join(annotations), Str.join(packages));
		}
		Set<String> packageSet = Stream.of(packages).collect(Collectors.toSet());
		Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages(packages));
		Set<Class<?>> allClasses = Collections.emptySet();
		for (Class<?> a : annotations) {
			@SuppressWarnings("unchecked")
			Class<? extends Annotation> annotation = (Class<? extends Annotation>) a;
			Set<Class<?>> classes = reflections.getTypesAnnotatedWith(annotation, inherited).stream()
					.filter(new PackageFilter(packageSet))
					.filter(i -> inherited || i.getDeclaredAnnotation(annotation) != null).collect(Collectors.toSet());
			if (classes.isEmpty()) {
				continue;
			}
			if (annotations.length == 1) {
				return classes;
			}
			if (allClasses.isEmpty()) {
				allClasses = classes;
			} else {
				allClasses.addAll(classes);
			}
		}
		return allClasses;
	}

	public static Set<Class<?>> findTypesAnnotatedWith(String[] packages, Class<?>... annotations) {
		return findTypesAnnotatedWith(packages, true, annotations);
	}

	public static Set<Class<?>> findSubTypesOf(String[] packages, Class<?> type) {
		validatePackages(packages);
		if (LOG.isInfoEnabled()) {
			LOG.info("findSubTypesOf: {} in packages: {}", type.getCanonicalName(), Str.join(packages));
		}
		Set<String> packageSet = Stream.of(packages).collect(Collectors.toSet());
		Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages(packages));
		return reflections.getSubTypesOf(type).stream().filter(new PackageFilter(packageSet))
				.collect(Collectors.toSet());
	}

	/** Reflections框架有点问题关于包过滤有点问题，所以我们需要再二次过滤 **/
	private static class PackageFilter implements Predicate<Class<?>> {
		Set<String> packageSet;
		Set<String> parentPackageSet;

		public PackageFilter(Set<String> packageSet) {
			super();
			this.packageSet = packageSet;
			this.parentPackageSet = packageSet.stream().map(i -> i + ".").collect(Collectors.toSet());
		}

		@Override
		public boolean test(Class<?> i) {
			String pkgName = i.getPackage().getName();
			if (packageSet.contains(pkgName)) {
				return true;
			}
			for (String parentPackage : parentPackageSet) {
				// a.bc.d starts with a.bc.
				// a.bc.d not starts with a.b.
				if (pkgName.startsWith(parentPackage)) {
					return true;
				}
			}
			if (LOG.isInfoEnabled()) {
				LOG.info(">>>exclude class: {} from packages: {} ", i, Str.join(packageSet));
			}
			return false;
		}

	}

	private static void validatePackages(String[] packages) {
		if (packages == null || packages.length == 0) {
			throw new IllegalArgumentException("Require packages");
		}
	}

}
