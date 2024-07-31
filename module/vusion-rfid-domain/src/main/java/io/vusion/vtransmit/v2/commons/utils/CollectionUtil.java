package io.vusion.vtransmit.v2.commons.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.vusion.gson.utils.GsonHelper;

/**
 * Utils for collections
 *
 * @author elanoiselee
 * @date 26 mai 2010
 * @version 1.0
 */
public class CollectionUtil {

	public static <K> void addToCountMap(final Map<K, Integer> countMap, final K key,
			final int increment) {
		Integer currentValue = countMap.get(key);
		if (currentValue == null) {
			currentValue = 0;
			countMap.put(key, 0);
		}
		countMap.put(key, currentValue + increment);
	}

	/**
	 * Add a Set of values in a Map of Set.<br>
	 * If no set is mapped on the given key, a new set associated to the key is created
	 *
	 * @param <K>
	 *            key type
	 * @param <E>
	 *            set value type
	 * @param map
	 * @param key
	 * @param valueSet
	 */
	public static <K, E> void addToSetMap(final Map<K, Set<E>> map, final K key, final Collection<E> valueSet) {
		Set<E> set = map.get(key);
		if (set == null) {
			set = new LinkedHashSet<>();
			map.put(key, set);
		}
		set.addAll(valueSet);
	}

	/**
	 * Add a value in a Map of Map.<br>
	 * If no set is mapped on the given key, a new set associated to the key is created
	 *
	 * @param <K>
	 *            key type
	 * @param <E>
	 *            set value type
	 * @param map
	 * @param key
	 * @param value
	 */
	public static <K, E, V> void addToMapOfMap(final Map<K, Map<E, V>> map, final K key, final E secondMapKey,
			final V value) {
		Map<E, V> subMap = map.get(key);
		if (subMap == null) {
			subMap = new HashMap<>();
			map.put(key, subMap);
		}
		subMap.put(secondMapKey, value);
	}

	public static <K, E, V> void addToMapOfListMap(final Map<K, Map<E, List<V>>> map, final K key,
			final E secondMapKey,
			final V value) {
		Map<E, List<V>> subMap = map.get(key);
		if (subMap == null) {
			subMap = new HashMap<>();
			map.put(key, subMap);
		}
		addToListMap(subMap, secondMapKey, value);
	}

	public static <K, E, V> void addToMapOfListMap(final Map<K, Map<E, List<V>>> map, final K key,
			final E secondMapKey,
			final List<V> values) {

		final AtomicReference<Map<E, List<V>>> atomicSubMap = new AtomicReference<>();
		Map<E, List<V>> subMap = map.get(key);
		if (subMap == null) {
			subMap = new HashMap<>();
			map.put(key, subMap);
		}
		atomicSubMap.set(subMap);

		if (!atomicSubMap.get().containsKey(secondMapKey)) {
			atomicSubMap.get().put(secondMapKey, new ArrayList<>());
		}
		values.forEach(value -> addToListMap(atomicSubMap.get(), secondMapKey, value));
	}

	/**
	 * Add a value in a Map of Set.<br>
	 * If no set is mapped on the given key, a new set associated to the key is created
	 *
	 * @param <K>
	 *            key type
	 * @param <E>
	 *            set value type
	 * @param map
	 * @param key
	 * @param value
	 */
	public static <K, E> void addToSetMap(final Map<K, Set<E>> map, final K key, final E value) {
		Set<E> set = map.get(key);
		if (set == null) {
			set = new LinkedHashSet<>();
			map.put(key, set);
		}
		set.add(value);
	}

	/**
	 * Add a value in a Map of List.<br>
	 * If no List is mapped on the given key, a new List associated to the key is created
	 *
	 * @param <K>
	 *            key type
	 * @param <E>
	 *            list value type
	 * @param map
	 * @param key
	 * @param value
	 */
	public static <K, E> void addToListMap(final Map<K, List<E>> map, final K key, final E value) {
		List<E> list = map.get(key);
		if (list == null) {
			list = new ArrayList<>();
			map.put(key, list);
		}
		list.add(value);
	}

	/**
	 * Add a value in a Map of List.<br>
	 * If no List is mapped on the given key, a new List associated to the key is created
	 *
	 * @param <K>
	 *            key type
	 * @param <E>
	 *            list value type
	 * @param map
	 * @param key
	 * @param value
	 */
	public static <K, E> void addToListMap(final ConcurrentHashMap<K, List<E>> map, final K key, final E value) {
		List<E> list = map.get(key);
		if (list == null) {
			list = new CopyOnWriteArrayList<>();
			map.put(key, list);
		}
		list.add(value);
	}

	/**
	 * Add a List of values in a Map of List.<br>
	 * If no List is mapped on the given key, a new List associated to the key is created
	 *
	 * @param <K>
	 *            key type
	 * @param <E>
	 *            set value type
	 * @param map
	 * @param key
	 * @param valueCollection
	 */
	public static <K, E> void addToListMap(final Map<K, List<E>> map, final K key,
			final Collection<E> valueCollection) {
		List<E> list = map.get(key);
		if (list == null) {
			list = new ArrayList<>();
			map.put(key, list);
		}
		list.addAll(valueCollection);
	}

	/**
	 * Add a List of values in a Map of List.<br>
	 * If no List is mapped on the given key, a new List associated to the key is created
	 *
	 * @param <K>
	 *            key type
	 * @param <E>
	 *            set value type
	 * @param map
	 * @param key
	 * @param valueCollection
	 */
	public static <K, E> void addToListMap(final Map<K, List<E>> map, final K key,
			final Stream<E> valueCollection) {
		List<E> list = map.get(key);
		if (list == null) {
			list = new ArrayList<>();
			map.put(key, list);
		}
		list.addAll(valueCollection.collect(Collectors.toList()));
	}

	/**
	 * Add a value in a Map of List.<br>
	 * If no List is mapped on the given key, a new List associated to the key is created
	 *
	 * @param <K>
	 *            key type
	 * @param <E>
	 *            list value type
	 * @param map
	 * @param key
	 * @param value
	 */
	public static <K, E> void addToListMap(final Map<K, List<E>> map, final Map<K, List<E>> mapToAdd) {
		mapToAdd.entrySet().forEach(entry -> addToListMap(map, entry.getKey(), entry.getValue()));
	}

	/**
	 * Retrieves, but does not remove, the last element of this list, or returns null if this list is empty
	 *
	 * @param list
	 * @return the last element of this list, or null if this list is empty
	 */
	public static <E> E peekLast(final List<E> list) {
		E last = null;
		final int size = list.size();
		if (size != 0) {
			last = list.get(size - 1);
		}
		return last;
	}

	public static <K, V> boolean contains(final Map<K, V> container, final Map<K, V> contained) {

		return GsonHelper.contains(container, contained);
	}

	public static boolean isEmpty(final Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	public static <T> Collection<List<T>> partition(final Collection<T> list, final int size) {
		return partition(list.stream(), size);
	}

	public static <T> Collection<List<T>> partition(final Stream<T> list, final int size) {
		final AtomicInteger counter = new AtomicInteger(0);
		return list
				.collect(Collectors.groupingBy(it -> counter.getAndIncrement() / size))
				.values();
	}

	public static <T> List<List<T>> partitionByMaxContentSize(final Stream<T> list, final int maxContentSize,
			final SizeComputer<T> sizeComputer) {
		return partitionByMaxContentSize(list.collect(Collectors.toList()), maxContentSize, sizeComputer);
	}

	public static <T> List<List<T>> partitionByMaxContentSize(final List<T> list, final int maxContentSize,
			final SizeComputer<T> sizeComputer) {

		final List<List<T>> result = new ArrayList<>();
		if (list.isEmpty()) {
			return result;
		}

		int currentSubListSize = 0;
		List<T> currentSubList = new ArrayList<>();
		result.add(currentSubList);

		for (final T current : list) {
			final int currentSize = sizeComputer.sizeOf(current);

			if (currentSize > maxContentSize) {
				throw new RuntimeException("Size of " + current + " (" + currentSize + ") is superior to max size: "
						+ maxContentSize);
			}

			if (currentSubListSize + currentSize > maxContentSize) {
				currentSubList = new ArrayList<>();
				result.add(currentSubList);
				currentSubListSize = 0;
			}
			currentSubList.add(current);
			currentSubListSize += currentSize;
		}

		return result;
	}

	public static interface SizeComputer<T> {
		int sizeOf(T object);
	}

	public static <K, V> List<Map<K, V>> partition(final Map<K, V> mapToPartition, final int size) {
		final List<Map<K, V>> result = new ArrayList<>();

		CollectionUtil.partition(mapToPartition.entrySet(), size)
				.forEach(currentEntries -> {
					result.add(currentEntries.stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
				});

		return result;
	}

	public static <T> boolean hasChange(final Collection<T> first, final Collection<T> second) {
		return !first.containsAll(second) || !second.containsAll(first);
	}

	public static <T> boolean hasChangeNoOrderCheck(final List<T> first, final List<T> second) {
		return !first.containsAll(second) || !second.containsAll(first);
	}

	public static <K, V> boolean hasChange(final Map<K, V> first, final Map<K, V> second) {
		return hasChange(first.values(), second.values());
	}

	public static <T> Stream<T> asStream(final Iterator<T> sourceIterator) {
		final Iterable<T> iterable = () -> sourceIterator;
		return StreamSupport.stream(iterable.spliterator(), false);
	}

	public static <K,T> Map<K, List<T>> groupBy(final Collection<T> collection, final Function<? super T, ? extends K> classifier) {
		return collection
                .stream()
				 .collect(Collectors.groupingBy(classifier, TreeMap::new, Collectors.toList()));
	}

	public static <K,T> Map<K, Long> groupCountBy(final Collection<T> collection, final Function<? super T, ? extends K> classifier) {
		return collection
                .stream()
				 .collect(Collectors.groupingBy(classifier, Collectors.counting()));
	}
}
