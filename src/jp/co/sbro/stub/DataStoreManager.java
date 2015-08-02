package jp.co.sbro.stub;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import jp.co.sbro.stub.entity.ParentGetter;

import com.google.appengine.api.datastore.Key;

/**
 *
 * @author suzuki_yuu
 *
 */
public class DataStoreManager {

	private static final PersistenceManagerFactory factory = JDOHelper.getPersistenceManagerFactory("transactions-optional");

	/**
	 * 作成
	 * @param o
	 * @return
	 */
	public static final <T> T create(T o) {
		PersistenceManager manager = factory.getPersistenceManager();
		T result = manager.makePersistent(o);
		manager.close();
		return result;
	}

	/**
	 * 子データの作成
	 * @param parentClass
	 * @param parentKey
	 * @param childListName
	 * @param child
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static final <T, E> E createChild(Class<T> parentClass, Key parentKey, String childListName, E child) {
		PersistenceManager manager = factory.getPersistenceManager();
		T parent = manager.getObjectById(parentClass, parentKey);
		try {
			PropertyDescriptor properties = new PropertyDescriptor(childListName, parentClass);
			((List<E>) properties.getReadMethod().invoke(parent)).add(child);
		} catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		manager.close();
		return child;
	}

	/**
	 * 全件取得
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static final <T> List<T> read(Class<T> clazz, String order) {
		PersistenceManager manager = factory.getPersistenceManager();
		Query query = manager.newQuery(clazz);
		query.setOrdering(order);
		List<T> result = (List<T>) query.execute();
		readParent(result);
		manager.close();
		return result;
	}

	private static final <T> void readParent(List<T> childs) {
		for (T child : childs) {
			for (Method m : child.getClass().getMethods()) {
				if (m.getAnnotation(ParentGetter.class) != null) {
					try {
						Object parent = m.invoke(child);
						readParentProperties(parent);
						break;
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private static final void readParentProperties(Object parent) {
		Class<?> parentClass = parent.getClass();
		for (Field f : parentClass.getDeclaredFields()) {
			try {
				PropertyDescriptor properties = new PropertyDescriptor(f.getName(), parentClass);
				properties.getReadMethod().invoke(parent);
			} catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			}
		}
	}

	/**
	 * 与えられたパラメータに一致するデータのみ取得
	 * @param clazz
	 * @param condition
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static final <T> List<T> read(Class<T> clazz, Map<String, Object> condition) {
		PersistenceManager manager = factory.getPersistenceManager();
		StringBuilder where = new StringBuilder();
		StringBuilder declare = new StringBuilder();
		boolean isFirst = true;
		for (Entry<String, Object> entry : condition.entrySet()) {
			String name = entry.getKey();
			Object value = entry.getValue();
			String type = value.getClass().getSimpleName();
			where.append(!isFirst ? " && " : "").append(name).append(" ==").append(" p" + name);
			declare.append(!isFirst ? ", " : "").append(type).append(" p" + name);
			isFirst = false;
		}
		Query query = manager.newQuery(clazz, where.toString());
		query.setFilter(where.toString());
		query.declareParameters(declare.toString());
		return (List<T>) query.executeWithArray(condition.values().toArray(new Object[0]));
	}

	/**
	 * キーで絞って1件のみ取得
	 * @param clazz
	 * @param key
	 * @return
	 */
	public static final <T> T read(Class<T> clazz, Key key) {
		PersistenceManager manager = factory.getPersistenceManager();
		return manager.getObjectById(clazz, key);
	}

	/**
	 * 更新
	 * @param clazz
	 * @param key
	 * @param values
	 * @return
	 */
	public static final <T> T update(Class<T> clazz, Key key, Map<String, Object> values) {
		PersistenceManager manager = factory.getPersistenceManager();
		T o = (T) manager.getObjectById(clazz, key);
		for (Entry<String, Object> entry : values.entrySet()) {
			try {
				PropertyDescriptor properties = new PropertyDescriptor(entry.getKey(), clazz);
				properties.getWriteMethod().invoke(o, entry.getValue());
			} catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		manager.close();
		return o;
	}

	/**
	 * 削除
	 * @param clazz
	 * @param key
	 * @return
	 */
	public static final void delete(Class<?> clazz, Key key) {
		PersistenceManager manager = factory.getPersistenceManager();
		manager.deletePersistent(manager.getObjectById(clazz, key));
		manager.close();
	}

}
