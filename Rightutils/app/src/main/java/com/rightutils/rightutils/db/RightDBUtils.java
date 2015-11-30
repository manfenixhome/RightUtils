package com.rightutils.rightutils.db;

import com.github.andreyrage.leftdb.LeftDBUtils;
import com.rightutils.rightutils.collections.Operation;
import com.rightutils.rightutils.collections.RightList;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * eKreative
 * Created by rage on 11/30/15.
 */
public abstract class RightDBUtils extends LeftDBUtils {
	private ObjectMapper mapper;

	@Override
	protected String serializeObject(Object object) {
		try {
			return getMapper().writeValueAsString(object);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected <T> T deserializeObject(String string, Class<T> tClass, final Type genericType) {
		try {
			return getMapper().readValue(string, new TypeReference<Object>() {
				@Override public Type getType() {
					return genericType;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private ObjectMapper getMapper() {
		if (mapper == null) {
			mapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		}
		return mapper;
	}

	public <T> RightList<T> executeQuery(String query, Class<T> type) {
		return RightList.asRightList(super.executeQuery(query, type));
	}

	public <T> RightList<T> getAll(Class<T> type) {
		return RightList.asRightList(super.getAll(type));
	}

	public <T> RightList<T> getAllLimited(Class<T> type, long limit) {
		return RightList.asRightList(super.getAllLimited(type, limit));
	}

	public <T> RightList<T> getAllWhere(String where, Class<T> type) {
		return RightList.asRightList(super.getAllWhere(where, type));
	}

	public <T> void add(RightList<T> elements) {
		elements.foreach(new Operation<T>() {
			@Override
			public void execute(T value) {
				add(value);
			}
		});
	}
}
