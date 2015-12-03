package com.rightutils.example.db;

import android.content.Context;

import com.rightutils.rightutils.db.RightDBUtils;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Created by Anton Maniskevich on 1/20/15.
 */
public class DBUtils extends RightDBUtils {

	private static final String TAG = DBUtils.class.getSimpleName();
	private final ObjectMapper mapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	public static DBUtils newInstance(Context context, String name, int version) {
		DBUtils dbUtils = new DBUtils();
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
