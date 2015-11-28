package com.rightutils.rightutils.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.github.andreyrage.leftdb.utils.SerializeUtils;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;

/**
 * eKreative
 * Created by Anton Maniskevich on 12/22/14.
 */
public abstract class CacheUtils {

	private static final String TAG = CacheUtils.class.getSimpleName();

	public static boolean debug = false;

	public interface CallBack<T> {
		boolean run(T cache);
	}

    @Deprecated
    public static synchronized <T> void getCache(ObjectMapper mapper, Class<T> type, Context context, CallBack<T> callback, boolean saveCache) {
        getCache(mapper,type,context,callback);
    }

	public static synchronized <T> void getCache(ObjectMapper mapper, Class<T> type, Context context, CallBack<T> callback) {
		T cache = null;
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		if (!"".equals(sharedPreferences.getString(type.getSimpleName(), ""))) {
			try {
				cache = mapper.readValue(sharedPreferences.getString(type.getSimpleName(), ""), type);
			} catch (Exception e) {
				Log.e(TAG, "get CACHE", e);
			}
		}
		if (cache == null) {
			try {
				cache = type.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if(callback != null){
			log(context,"Cache Read: " + cache);
			if(callback.run(cache)){
				log(context,"Cache Write: " + cache);
                try {
                    sharedPreferences.edit().remove(type.getSimpleName()).commit();
                    sharedPreferences.edit().putString(type.getSimpleName(), mapper.writeValueAsString(cache)).commit();
                } catch (Exception e) {
                    Log.e(TAG, "save CACHE", e);
                }
            }
		}
	}

	public static synchronized <T> void getCache(Class<T> type, Context context, CallBack<T> callback) {
		T cache = null;
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		if (!"".equals(sharedPreferences.getString(type.getSimpleName(), ""))) {
			try {
				cache = deserializeObject(sharedPreferences.getString(type.getSimpleName(), ""), type);
			} catch (Exception e) {
				Log.e(TAG, "get CACHE", e);
			}
		}
		if (cache == null) {
			try {
				cache = type.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if(callback != null){
			log(context,"Cache Read: " + cache);
			if(callback.run(cache)){
				log(context,"Cache Write: " + cache);
				try {
					sharedPreferences.edit().remove(type.getSimpleName()).commit();
					sharedPreferences.edit().putString(type.getSimpleName(), serializeObject(cache)).commit();
				} catch (Exception e) {
					Log.e(TAG, "save CACHE", e);
				}
			}
		}
	}

	protected static String serializeObject(Object object) {
		try {
			return Arrays.toString(SerializeUtils.serialize(object));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected static <T> T deserializeObject(String string, Class<T> tClass) {
		String[] byteValues = string.substring(1, string.length() - 1).split(",");
		byte[] bytes = new byte[byteValues.length];
		for (int i=0, len=bytes.length; i<len; i++) {
			bytes[i] = Byte.parseByte(byteValues[i].trim());
		}

		try {
			Object o = SerializeUtils.deserialize(bytes);
			if (o != null) {
				return tClass.cast(o);
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void log(Context context,String log){
		if(debug) {
			Log.i(TAG + " " + context.getClass().getSimpleName(), log);
		}
	}

}

