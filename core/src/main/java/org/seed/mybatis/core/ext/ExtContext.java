package org.seed.mybatis.core.ext;

import org.apache.ibatis.session.SqlSessionFactory;
import org.seed.mybatis.core.util.ClassUtil;

import java.util.HashMap;
import java.util.Map;


public class ExtContext {

    // key: entityClassName, value: mapperClass
    private static final Map<String, Class<?>> entityMapper = new HashMap<>(16);

    private static final Map<String, SqlSessionFactory> sqlSessionFactoryMapForEnvironment = new HashMap<>();

    private static final Map<String, SqlSessionFactory> sqlSessionFactoryMapForMapper = new HashMap<>();

    public static void addMapperClass(Class<?> mapperClass) {
        Class<?> entityClass = getEntityClass(mapperClass);
        entityMapper.put(entityClass.getName(), mapperClass);
    }

    public static Class<?> getMapperClass(Class<?> entityClass) {
        return entityMapper.get(entityClass.getName());
    }

    public static Class<?> getEntityClass(Class<?> mapperClass) {
        if (mapperClass.isInterface()) {
            return ClassUtil.getSuperInterfaceGenericType(mapperClass, 0);
        } else {
            return ClassUtil.getSuperClassGenricType(mapperClass, 0);
        }
    }

    public static void addSqlSessionFactory(String env, SqlSessionFactory sqlSessionFactory) {
        sqlSessionFactoryMapForEnvironment.put(env, sqlSessionFactory);
    }

    public static void addSqlSessionFactory(Class<?> mapperClass, SqlSessionFactory sqlSessionFactory) {
        sqlSessionFactoryMapForMapper.put(mapperClass.getName(), sqlSessionFactory);
    }

    public static SqlSessionFactory getSqlSessionFactoryByEnvironment(String environment) {
        return sqlSessionFactoryMapForEnvironment.get(environment);
    }

    public static SqlSessionFactory getSqlSessionFactoryByMapperClass(Class<?> mapperClass) {
        return sqlSessionFactoryMapForMapper.get(mapperClass.getName());
    }
}
