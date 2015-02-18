package com.rightutils.rightutils.collections;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class RightList<T> extends ArrayList<T> implements Serializable {

	private static final long serialVersionUID = 5093130242320904496L;

	public static <T> RightList<T> asRightList(T... array) {
		return asRightList(Arrays.asList(array));
	}

	public static <T> RightList<T> asRightList(Collection<T> list) {
		RightList<T> newList = new RightList<T>();
		newList.addAll(list);
		return newList;
	}

	public RightList<T> filter(Predicate<T> predicate) {
		RightList<T> result = new RightList<T>();
		for (T element : this) {
			if (predicate.apply(element)) {
				result.add(element);
			}
		}
		return result;
	}

	public <E> RightList<E> map(Mapper<E, T> mapper) {
		RightList<E> result = new RightList<E>();
		for(T element: this) {
			result.add(mapper.apply(element));
		}
		return result;
	}

	public RightList<T> sort(Comparator<T> compare) {
		RightList<T> result = new RightList<T>();
		result.addAll(this);
		Collections.sort(result, compare);
		return result;
	}

	public T findBy(Predicate<T> predicate) {
		for (T element : this) {
			if (predicate.apply(element)) {
				return element;
			}
		}
		return null;
	}

	public <E> RightList<Pair<E,RightList<T>>> groupBy(Mapper<E, T> mapper) {
		Map<E, RightList<T>> map = new HashMap<E, RightList<T>>();
		for (T element: this) {
			E groupKey = mapper.apply(element);
			if (map.get(groupKey) == null) {
				map.put(groupKey, new RightList<T>());
			}
			map.get(groupKey).add(element);
		}
		RightList<Pair<E, RightList<T>>> groups = new RightList<Pair<E,RightList<T>>>();
		for (Entry<E,RightList<T>> item : map.entrySet()) {
			groups.add(Pair.of(item.getKey(), item.getValue()));
		}
		return groups;
	}

	public <E> E reduce(E initValue, Function<E,T> function) {
		E result = initValue;
		for(T element: this) {
			result = function.apply(result, element);
		}
		return result;
	}

	public RightList<T> addList(RightList<T> list) {
		this.addAll(list);
		return this;
	}

	public void foreach(Operation<T> operation) {
		for (T element : this) {
			operation.execute(element);
		}
	}

	@SuppressWarnings("unchecked")
	public String convertToString() {
		if (this.isEmpty()) {
			return "[]";
		}
		return Arrays.toString(this.toArray((T[]) Array.newInstance(this.get(0).getClass(), 0)));
	}

	public T getLast() {
		if (this.isEmpty()) {
			return null;
		}
		return this.get(this.size()-1);
	}

	public T getFirst() {
		if (this.isEmpty()) {
			return null;
		}
		return this.get(0);
	}
}
