package com.sunnysuperman.mountain.lang.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import com.sunnysuperman.mountain.lang.exception.UnexpectedException;
import com.sunnysuperman.mountain.lang.model.SameComparator;

public final class Colls {

	protected Colls() {
	}

	public static boolean isEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	public static boolean isNotEmpty(Collection<?> collection) {
		return collection != null && !collection.isEmpty();
	}

	public static boolean isEmpty(Map<?, ?> map) {
		return map == null || map.isEmpty();
	}

	public static boolean isNotEmpty(Map<?, ?> map) {
		return map != null && !map.isEmpty();
	}

	public static <T> List<T> emptyToNull(List<T> list) {
		List<T> empty = null;
		if (list != null && list.isEmpty()) {
			return empty;
		}
		return list;
	}

	public static <T> List<T> nullToEmpty(List<T> list) {
		if (list == null) {
			return Collections.emptyList();
		}
		return list;
	}

	public static <T> Set<T> emptyToNull(Set<T> set) {
		Set<T> empty = null;
		if (set != null && set.isEmpty()) {
			return empty;
		}
		return set;
	}

	public static <T> Set<T> nullToEmpty(Set<T> set) {
		if (set == null) {
			return Collections.emptySet();
		}
		return set;
	}

	public static <T> List<T> concat(List<T> list1, List<T> list2) {
		if (list1 == null) {
			list1 = Collections.emptyList();
		}
		if (list2 == null) {
			list2 = Collections.emptyList();
		}
		List<T> list = new ArrayList<>(list1.size() + list2.size());
		list.addAll(list1);
		list.addAll(list2);
		return list;
	}

	public static <T> Set<T> concat(Set<T> set1, Set<T> set2) {
		if (set1 == null) {
			set1 = Collections.emptySet();
		}
		if (set2 == null) {
			set2 = Collections.emptySet();
		}
		Set<T> set = new HashSet<>(set1.size() + set2.size());
		set.addAll(set1);
		set.addAll(set2);
		return set;
	}

	public static <K, T> Map<K, T> concat(Map<K, T> map1, Map<K, T> map2) {
		if (map1 == null) {
			map1 = Collections.emptyMap();
		}
		if (map2 == null) {
			map2 = Collections.emptyMap();
		}
		Map<K, T> map = newMap(map1.size() + map2.size());
		map.putAll(map1);
		map.putAll(map2);
		return map;
	}

	public static <T> T safeGet(List<T> list, int index) {
		if (index < 0 || index >= list.size()) {
			return null;
		}
		return list.get(index);
	}

	public static boolean containsAny(Collection<?> source, Collection<?> candidates) {
		if (isEmpty(source) || isEmpty(candidates)) {
			return false;
		}
		for (Object candidate : candidates) {
			if (source.contains(candidate)) {
				return true;
			}
		}
		return false;
	}

	public static boolean containsAll(Collection<?> source, Collection<?> candidates) {
		if (isEmpty(source) || isEmpty(candidates)) {
			return false;
		}
		for (Object candidate : candidates) {
			if (!source.contains(candidate)) {
				return false;
			}
		}
		return true;
	}

	public static <T> List<T> removeDuplicate(List<T> list) {
		if (isEmpty(list)) {
			return list;
		}
		ArrayList<T> newList = new ArrayList<>(list.size());
		for (T element : list) {
			if (!newList.contains(element)) {
				newList.add(element);
			}
		}
		return newList;
	}

	public static <T> List<T> reverse(List<T> list) {
		if (list == null || list.size() <= 1) {
			return list;
		}
		List<T> list2 = new ArrayList<>(list);
		Collections.reverse(list2);
		return list2;
	}

	public static class CollectionChange<T> {
		private List<T> adds;
		private List<T> keeps;
		private List<T> removes;

		public CollectionChange(List<T> adds, List<T> keeps, List<T> removes) {
			super();
			this.adds = adds;
			this.keeps = keeps;
			this.removes = removes;
		}

		public boolean isEmpty() {
			return adds.isEmpty() && keeps.isEmpty() && removes.isEmpty();
		}

		public List<T> getAdds() {
			return adds;
		}

		public List<T> getKeeps() {
			return keeps;
		}

		public List<T> getRemoves() {
			return removes;
		}
	}

	public static <T> CollectionChange<T> compare(Collection<T> oldCollections, Collection<T> collections) {
		oldCollections = Objs.or(oldCollections, Collections.emptyList());
		collections = Objs.or(collections, Collections.emptyList());
		if (oldCollections.isEmpty()) {
			return new CollectionChange<>(new ArrayList<>(collections), Collections.emptyList(),
					Collections.emptyList());
		}
		List<T> adds = new ArrayList<>();
		List<T> keeps = new ArrayList<>();
		List<T> removes = new ArrayList<>();
		for (T item : oldCollections) {
			if (!collections.contains(item)) {
				removes.add(item);
			}
		}
		for (T item : collections) {
			if (oldCollections.contains(item)) {
				keeps.add(item);
			} else {
				adds.add(item);
			}
		}
		return new CollectionChange<>(adds, keeps, removes);
	}

	public static <T> CollectionChange<T> compare(Collection<T> oldCollections, Collection<T> collections,
			SameComparator<T> comparator) {
		oldCollections = Objs.or(oldCollections, Collections.emptyList());
		collections = Objs.or(collections, Collections.emptyList());
		if (oldCollections.isEmpty()) {
			return new CollectionChange<>(new ArrayList<>(collections), Collections.emptyList(),
					Collections.emptyList());
		}
		List<T> adds = new ArrayList<>();
		List<T> keeps = new ArrayList<>();
		List<T> removes = new ArrayList<>();
		for (T oldItem : oldCollections) {
			if (!find(oldItem, collections, comparator)) {
				removes.add(oldItem);
			}
		}
		for (T newItem : collections) {
			if (find(newItem, oldCollections, comparator)) {
				keeps.add(newItem);
			} else {
				adds.add(newItem);
			}
		}
		return new CollectionChange<>(adds, keeps, removes);
	}

	public static <T> boolean find(T item, Collection<T> collections, SameComparator<T> comparator) {
		for (T i : collections) {
			if (comparator.sameAs(i, item)) {
				return true;
			}
		}
		return false;
	}

	public static <T> boolean isSame(Collection<T> oldCollections, Collection<T> collections) {
		if (oldCollections == null) {
			oldCollections = Collections.emptyList();
		}
		if (collections == null) {
			collections = Collections.emptyList();
		}
		if (oldCollections.size() != collections.size()) {
			return false;
		}
		for (T item : oldCollections) {
			if (!collections.contains(item)) {
				return false;
			}
		}
		return true;
	}

	public static <T> Set<T> unionSet(Set<T> set1, Set<T> set2) {
		if (set1 == null || set1.isEmpty()) {
			return set2;
		}
		if (set2 == null || set2.isEmpty()) {
			return set1;
		}
		int size = set1.size() + set2.size();
		Set<T> union = new HashSet<>(size);
		union.addAll(set1);
		union.addAll(set2);
		return union;
	}

	public static <T> List<T> unionList(List<T> list1, List<T> list2) {
		if (list1 == null || list1.isEmpty()) {
			return list2;
		}
		if (list2 == null || list2.isEmpty()) {
			return list1;
		}
		int size = list1.size() + list2.size();
		List<T> union = new ArrayList<>(size);
		union.addAll(list1);
		union.addAll(list2);
		return union;
	}

	public static <T> List<T> subListByOffsetAndSize(List<T> list, int offset, int size) {
		if (offset >= list.size()) {
			return Collections.emptyList();
		}
		return list.subList(offset, Math.min(offset + size, list.size()));
	}

	public static <T> int indexOf(List<T> findIn, Predicate<? super T> pred) {
		for (int i = 0; i < findIn.size(); i++) {
			if (pred.test(findIn.get(i))) {
				return i;
			}
		}
		return -1;
	}

	public static Map<String, Object> arrayAsMap(Object... t) {
		if (t == null || t.length <= 0) {
			return Collections.emptyMap();
		}
		if (t.length % 2 != 0) {
			throw new UnexpectedException("illegal args count");
		}
		Map<String, Object> params = new HashMap<>(t.length);
		for (int i = 0; i < t.length; i += 2) {
			if (t[i] == null || !t[i].getClass().equals(String.class)) {
				throw new UnexpectedException("illegal arg: " + t[i] + "at " + i);
			}
			String key = t[i].toString();
			Object value = t[i + 1];
			params.put(key, value);
		}
		return params;
	}

	public static <K, V> Map<K, V> newMap(int capacity) {
		return new HashMap<>(capacity, 1f);
	}

	public static Map<String, Object> singletonMap(String key, Object value) {
		return Collections.singletonMap(key, value);
	}

	public static <T> Map<String, T> copyMap(Map<String, T> map, String[] keys) {
		Map<String, T> copy = newMap(keys.length);
		for (String key : keys) {
			T val = map.get(key);
			if (val != null) {
				copy.put(key, val);
			}
		}
		return copy;
	}

}
