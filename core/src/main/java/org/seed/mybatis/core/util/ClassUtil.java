package org.seed.mybatis.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class ClassUtil {

    private static final String PROXY_PREFIX = "com.sun.proxy";

    private static final Map<String, Class<?>> CACHE = new ConcurrentHashMap<>(16);

    private ClassUtil() {
    }

    ;

    /**
     * 返回定义类时的泛型参数的类型. <br>
     * 如:定义一个BookManager类<br>
     * <code>{@literal public BookManager extends GenricManager<Book,Address>}{...} </code>
     * <br>
     * 调用getSuperClassGenricType(getClass(),0)将返回Book的Class类型<br>
     * 调用getSuperClassGenricType(getClass(),1)将返回Address的Class类型
     *
     * @param clazz 从哪个类中获取
     * @param index 泛型参数索引,从0开始
     * @return 返回泛型类型class
     * @throws IndexOutOfBoundsException
     */
    public static Class<?> getSuperClassGenricType(Class<?> clazz, int index) throws IndexOutOfBoundsException {

        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class<?>) params[index];
    }

    /**
     * 返回接口类的泛型参数的类型
     *
     * @param clazz 从哪个类中获取
     * @param index 泛型参数索引,从0开始
     * @return 返回class对象
     */
    public static Class<?> getSuperInterfaceGenericType(Class<?> clazz, int index) {
        String key = clazz.getName() + index;
        return CACHE.computeIfAbsent(key, k -> doGetSuperInterfaceGenericType(clazz, index));
    }

    private static Class<?> doGetSuperInterfaceGenericType(Class<?> clazz, int index) {
        String name = clazz.getName();
        // 如果是mapper代理类
        if (name.startsWith(PROXY_PREFIX)) {
            Type[] interfacesTypes = clazz.getGenericInterfaces();
            if (interfacesTypes.length == 0) {
                return Object.class;
            }
            clazz = (Class<?>) interfacesTypes[0];
            return doGetSuperInterfaceGenericType(clazz, index);
        }
        // 一个类可能实现多个接口,每个接口上定义的泛型类型都可取到
        Type[] interfacesTypes = clazz.getGenericInterfaces();
        if (interfacesTypes.length == 0) {
            return Object.class;
        }
        // 取第一个接口
        Type firstInterface = interfacesTypes[0];
        Type[] params = ((ParameterizedType) firstInterface).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }

        return (Class<?>) params[index];
    }


    /**
     * 是否是数组或结合类
     *
     * @param value 待检测对象
     * @return true，是结合或数组
     */
    public static boolean isArrayOrCollection(Object value) {
        boolean ret = false;
        if (value.getClass().isArray()) {
            ret = true;
        } else if (value instanceof Collection) {
            ret = true;
        }
        return ret;
    }

    /**
     * Check if it is the basic data type of json data
     *
     * @param type0 java class name
     * @return boolean
     */
    public static boolean isPrimitive(String type0) {
        if (Objects.isNull(type0)) {
            return true;
        }
        String type = type0.contains("java.lang") ? type0.substring(type0.lastIndexOf(".") + 1, type0.length()) : type0;
        type = type.toLowerCase();
        switch (type) {
            case "string":
            case "integer":
            case "int":
            case "object":
            case "void":
            case "long":
            case "double":
            case "float":
            case "short":
            case "bigdecimal":
            case "char":
            case "character":
            case "number":
            case "boolean":
            case "byte":
            case "uuid":
            case "biginteger":
            case "java.sql.timestamp":
            case "java.util.date":
            case "java.time.localdatetime":
            case "java.time.localtime":
            case "localtime":
            case "date":
            case "localdatetime":
            case "localdate":
            case "zoneddatetime":
            case "java.time.localdate":
            case "java.time.zoneddatetime":
            case "java.math.bigdecimal":
            case "java.math.biginteger":
            case "java.util.uuid":
            case "java.io.serializable":
                return true;
            default:
                return false;
        }
    }

    public static <A extends Annotation> A findAnnotation(Class<?> clazz, Class<A> annotationType) {
        Annotation[] declaredAnnotations = clazz.getDeclaredAnnotations();
        for (Annotation declaredAnnotation : declaredAnnotations) {
            if (declaredAnnotation.annotationType() == annotationType) {
                return (A) declaredAnnotation;
            }
        }
        return null;
    }

    public static Field findField(Class<?> clazz, String name) {
        Class<?> searchType = clazz;
        while (Object.class != searchType && searchType != null) {
            Field[] fields = searchType.getDeclaredFields();
            for (Field field : fields) {
                if (name.equals(field.getName())) {
                    return field;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) ||
                !Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
                Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("create instance error", e);
        }
    }

}
