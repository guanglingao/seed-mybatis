package org.seed.mybatis.core;


import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.seed.mybatis.core.ext.ExtContext;
import org.seed.mybatis.core.ext.MapperRunner;
import org.seed.mybatis.core.ext.info.EntityInfo;
import org.seed.mybatis.core.ext.spi.MapperBuilder;
import org.seed.mybatis.core.ext.spi.SpiContext;
import org.seed.mybatis.core.mapper.CrudMapper;
import org.seed.mybatis.core.util.ClassUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


public class SeedMybatisContext {

    private static final Log LOG = LogFactory.getLog(SeedMybatisContext.class);

    private static final Map<String, EntityInfo> ENTITY_INFO_MAP = new HashMap<>(16);

    private static Object applicationContext;

    public static void setApplicationContext(Object applicationContext) {
        SeedMybatisContext.applicationContext = applicationContext;
    }

    public static <T extends CrudMapper> MapperRunner<T> getCrudMapperRunner(Class<?> entityClass) {
        Class<T> mapperClass = (Class<T>) ExtContext.getMapperClass(entityClass);
        MapperBuilder mapperBuilder = SpiContext.getMapperBuilder();
        return mapperBuilder.getMapperRunner(mapperClass, applicationContext);
    }

    public static EntityInfo getEntityInfo(Class<?> entityClass) {
        return ENTITY_INFO_MAP.get(entityClass.getName());
    }

    public static void setEntityInfo(Class<?> entityClass, EntityInfo entityInfo) {
        ENTITY_INFO_MAP.put(entityClass.getName(), entityInfo);
    }

    /**
     * 获取实体类对应的数据库主键名称
     *
     * @param entityClass 实体类class
     * @return 返回数据库主键名称
     */
    public static String getPkColumnName(Class<?> entityClass) {
        EntityInfo entityInfo = getEntityInfo(entityClass);
        if (entityInfo == null) {
            return null;
        }
        return entityInfo.getKeyColumn();
    }

    /**
     * 获取实体类主键对应的JAVA字段名称
     *
     * @param entityClass 实体类class
     * @return 返回主键java字段名称
     */
    public static String getPkJavaName(Class<?> entityClass) {
        EntityInfo entityInfo = getEntityInfo(entityClass);
        if (entityInfo == null) {
            return null;
        }
        return entityInfo.getKeyJavaField();
    }

    /**
     * 获取主键值
     *
     * @param entity 实体类对象
     * @return 返回主键值
     */
    public static Object getPkValue(Object entity) {
        if (entity == null) {
            return null;
        }
        Class<?> entityClass = entity.getClass();
        String pkJavaName = getPkJavaName(entityClass);
        Field field = ClassUtil.findField(entityClass, pkJavaName);
        if (field == null) {
            return null;
        }
        ClassUtil.makeAccessible(field);
        try {
            return field.get(entity);
        } catch (IllegalAccessException e) {
            LOG.error("反射出错", e);
            return null;
        }
    }

}
