package com.rightutils.rightutils.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.rightutils.rightutils.collections.Operation;
import com.rightutils.rightutils.collections.Predicate;
import com.rightutils.rightutils.collections.RightList;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;

public abstract class RightDBUtils {

    private static final String TAG = RightDBUtils.class.getName();
    private static final ObjectMapper MAPPER = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    protected RightDBHandler dbHandler;
    protected SQLiteDatabase db;

    protected void setDBContext(Context context, String name, int version) {
        dbHandler = new RightDBHandler(context, name, version);
        try {
            dbHandler.createDataBase();
            if (db != null && db.isOpen()) {
            } else {
                db = dbHandler.getWritableDatabase();
            }
        } catch (IOException e) {
            Log.e(TAG, "Create DB", e);
        }
    }

    public <T> void deleteWhere(Class<T> type, String where) {
        db.delete(getTableName(type), where, null);
    }

    public <T> void deleteAll(Class<T> type) {
        db.delete(getTableName(type), null, null);
    }

    public <T> void delete(Class<T> type, String columnId, RightList<Long> ids) {
        deleteWhere(type, String.format("%s IN (%s)", columnId, TextUtils.join(",", ids)));
    }

    public int countResultsByQuery(String query) {
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return cursor.getCount();
        }
        cursor.close();
        return 0;
    }

    public <T> RightList<T> executeQuery(String query, Class<T> type) {
        return queryListMapper(query, type);
    }

    public <T> RightList<T> getAll(Class<T> type) {
        String query = String.format("select * from `%s`", getTableName(type));
        return queryListMapper(query, type);
    }

    public <T> RightList<T> getAllLimited(Class<T> type, long limit) {
        String query = String.format("select * from `%s` limit %d", getTableName(type), limit);
        return queryListMapper(query, type);
    }

    public <T> RightList<T> getAllWhere(String where, Class<T> type) {
        String query = String.format("select * from `%s` where %s", getTableName(type), where);
        return queryListMapper(query, type);
    }

    public <T> void add(RightList<T> elements) {
        elements.foreach(new Operation<T>() {
            @Override
            public void execute(T value) {
                add(value);
            }
        });
    }

    public <T> long add(final T element) {
        final ContentValues values = new ContentValues();
        RightList.asRightList(element.getClass().getDeclaredFields()).filter(new Predicate<Field>() {
            @Override
            public boolean apply(Field value) {
                return !value.isAnnotationPresent(ColumnIgnore.class) && !Modifier.isStatic(value.getModifiers());
            }
        }).foreach(new Operation<Field>() {
            @Override
            public void execute(Field value) {
                if (value.isAnnotationPresent(ColumnAutoInc.class)) {
                    valueAutoIncMapper(values, value, element);
                } else if (value.isAnnotationPresent(ColumnDAO.class)) {
                    valueDAOMapper(values, value, element);
                } else if (!value.isAnnotationPresent(ColumnChild.class)) {
                    valueMapper(values, value, element);
                }
            }
        });
        long count = db.insert(getTableName(element.getClass()), null, values);
        values.clear();
        addColumnChild(element);
        return count;
    }

    private <T> void addColumnChild(final T element) {
        RightList.asRightList(element.getClass().getDeclaredFields()).filter(new Predicate<Field>() {
            @Override
            public boolean apply(Field value) {
                return value.isAnnotationPresent(ColumnChild.class);
            }
        }).foreach(new Operation<Field>() {
            @Override
            public void execute(Field value) {
                value.setAccessible(true);
                try {
                    if (value.getType().isAssignableFrom(RightList.class)) {
                        //TODO for each element of list must be set foreignKey
                        add((RightList) value.get(element));
                    } else {
                        //TODO MUST BE TEST
                        Long parentIdValue = (Long) element.getClass().getDeclaredField(getParentKey(value)).get(element);
                        Object childObject = value.get(element);
                        Field foreignKeyField = childObject.getClass().getDeclaredField(getForeignKey(value));
                        foreignKeyField.setAccessible(true);
                        foreignKeyField.set(childObject, parentIdValue);
                        //--------
                        add(childObject);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "valueMapper", e);
                }
            }
        });
    }

    //INNER METHODS

    private <T> void valueAutoIncMapper(ContentValues values, Field field, T element) {
        field.setAccessible(true);
        try {
            long id = (Long) field.get(element);
            if (id > 0) {
                values.put(getColumnName(field), id);
            }
        } catch (IllegalAccessException e) {
            Log.e(TAG, "valueAutoIncMapper", e);
        }
    }

    private <T> void valueDAOMapper(ContentValues values, Field field, T element) {
        field.setAccessible(true);
        try {
            values.put(getColumnName(field), field.get(element) != null ? MAPPER.writeValueAsString(field.get(element)) : null);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "valueDaoMapper", e);
        } catch (Exception e) {
            Log.e(TAG, "valueDaoMapper", e);
        }
    }


    private <T> void valueMapper(ContentValues values, Field field, T element) {
        field.setAccessible(true);
        try {
            if (field.getType().isAssignableFrom(String.class)) {
                values.put(getColumnName(field), (String) field.get(element));
            } else if (field.getType().isAssignableFrom(long.class)) {
                values.put(getColumnName(field), (Long) field.get(element));
            } else if (field.getType().isAssignableFrom(int.class)) {
                values.put(getColumnName(field), (Integer) field.get(element));
            } else if (field.getType().isAssignableFrom(boolean.class)) {
                values.put(getColumnName(field), ((Boolean) field.get(element)) ? 1 : 0);
            } else if (field.getType().isAssignableFrom(float.class)) {
                values.put(getColumnName(field), (Float) field.get(element));
            } else if (field.getType().isAssignableFrom(double.class)) {
                values.put(getColumnName(field), (Double) field.get(element));
            } else if (field.getType().isAssignableFrom(Date.class)) {
                values.put(getColumnName(field), field.get(element) == null ? null : ((Date) field.get(element)).getTime());
            } else {
                Log.w(TAG, String.format("In class '%s' type '%s' of field '%s' not supported.", element.getClass().getSimpleName(), field.getType().toString(), field.getName()));
            }
        } catch (IllegalAccessException e) {
            Log.e(TAG, "valueMapper", e);
        }
    }

    private <T> RightList<T> queryListMapper(String query, Class<T> type) {
        RightList<T> results = new RightList<T>();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                results.add(cursorMapper(cursor, type));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return results;
    }

    private <T> T cursorMapper(Cursor cursor, Class<T> type) {
        T result = null;
        try {
            result = type.newInstance();
            for (Field field : result.getClass().getDeclaredFields()) {
                if (!field.isAnnotationPresent(ColumnIgnore.class)) {
                    if (!Modifier.isStatic(field.getModifiers())) {
                        fieldMapper(result, cursor, field, getColumnName(field));
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "cursorMapper", e);
        }
        return result;
    }

    private <T> void fieldMapper(T result, Cursor cursor, final Field field, String columnName) {
        field.setAccessible(true);
        try {
            if (field.getType().isAssignableFrom(String.class)) {
                field.set(result, cursor.getString(cursor.getColumnIndex(columnName)));
            } else if (field.getType().isAssignableFrom(long.class)) {
                field.set(result, cursor.getLong(cursor.getColumnIndex(columnName)));
            } else if (field.getType().isAssignableFrom(int.class)) {
                field.set(result, cursor.getInt(cursor.getColumnIndex(columnName)));
            } else if (field.getType().isAssignableFrom(boolean.class)) {
                field.set(result, cursor.getLong(cursor.getColumnIndex(columnName)) == 1);
            } else if (field.getType().isAssignableFrom(float.class)) {
                field.set(result, cursor.getFloat(cursor.getColumnIndex(columnName)));
            } else if (field.getType().isAssignableFrom(double.class)) {
                field.set(result, cursor.getDouble(cursor.getColumnIndex(columnName)));
            } else if (field.getType().isAssignableFrom(Date.class)) {
                field.set(result, cursor.getLong(cursor.getColumnIndex(columnName)) == 0 ? null : new Date(cursor.getLong(cursor.getColumnIndex(columnName))));
            } else if (field.isAnnotationPresent(ColumnChild.class)) {
                String foreignKey = getForeignKey(field);
                long parentKeyValue = cursor.getLong(cursor.getColumnIndex(getParentKey(field)));
                if (field.getType().isAssignableFrom(RightList.class)) {
                    field.set(result, getAllWhere(String.format("%s = %d", foreignKey, parentKeyValue), (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]));
                } else {
                    RightList resultList = getAllWhere(String.format("%s = %d", foreignKey, parentKeyValue), field.getType());
                    field.set(result, resultList.isEmpty() ? null : resultList.getFirst());
                }
            } else if (field.isAnnotationPresent(ColumnDAO.class)) {
                String value = cursor.getString(cursor.getColumnIndex(columnName));
                field.set(result, value != null ? MAPPER.readValue(value, new TypeReference<Object>() {
                    @Override public Type getType() {
                        return field.getGenericType();
                    }
                }) : null);
            } else {
                Log.w(TAG, String.format("In class '%s' type '%s' of field '%s' not supported.", result.getClass().getSimpleName(), field.getType().toString(), field.getName()));
            }
        } catch (IllegalAccessException e) {
            Log.e(TAG, "fieldMapper", e);
        } catch (Exception e) {
            Log.e(TAG, "fieldMapper", e);
        }
    }

    private <T> String getTableName(Class<T> type) {
        String tableName = type.getSimpleName();
        if (type.isAnnotationPresent(TableName.class)) {
            tableName = type.getAnnotation(TableName.class).value();
        }
        return tableName;
    }

    private String getColumnName(Field field) {
        String columnName = field.getName();
        if (field.isAnnotationPresent(ColumnName.class)) {
            columnName = field.getAnnotation(ColumnName.class).value();
        }
        return columnName;
    }

    private String getForeignKey(Field field) {
        String columnName = field.getName();
        if (field.isAnnotationPresent(ColumnChild.class)) {
            columnName = field.getAnnotation(ColumnChild.class).foreignKey();
        }
        return columnName;
    }

    private String getParentKey(Field field) {
        String columnName = field.getName();
        if (field.isAnnotationPresent(ColumnChild.class)) {
            columnName = field.getAnnotation(ColumnChild.class).parentKey();
        }
        return columnName;
    }

    public RightDBHandler getDbHandler() {
        return dbHandler;
    }
}
