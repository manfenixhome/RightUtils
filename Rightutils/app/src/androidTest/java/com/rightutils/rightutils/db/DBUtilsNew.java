package com.rightutils.rightutils.db;

import android.content.Context;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * eKreative
 * Created by Fenix-mobile on 12.03.2015.
 */
public class DBUtilsNew extends RightDBUtils {
	private ObjectMapper mapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	public static DBUtilsNew newInstance(Context context, String name, int version) {
		DBUtilsNew dbUtils = new DBUtilsNew();
		dbUtils.setDBContext(context, name, version);
		return dbUtils;
	}

	@Override
	protected String serializeObject(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected <T> T deserializeObject(String string, Class<T> tClass, final Type genericType) {
		try {
			return mapper.readValue(string, new TypeReference<Object>() {
				@Override public Type getType() {
					return genericType;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
