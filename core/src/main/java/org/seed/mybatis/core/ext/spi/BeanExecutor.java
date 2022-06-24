package org.seed.mybatis.core.ext.spi;

import java.util.List;
import java.util.Map;


public interface BeanExecutor {

    /**
     * 将实体对象转换成Map, key:实体类中的字段名称
     *
     * @param pojo 实体类
     * @return 返回map, key:实体类中的字段名称， value：实体类中的字段值
     */
    Map<String, Object> pojoToMap(Object pojo);

    /**
     * 取对象中的某一个值
     *
     * @param pojo     对象
     * @param valClass 待转换的对象类型
     * @param column   数据库字段
     * @param <T>      范湖类型
     * @return 返回单值，没有返回null
     */
    <T> T pojoToValue(Object pojo, Class<T> valClass, String column);

    /**
     * 单值转换
     *
     * @param value    单值
     * @param valClass 转换的class类型
     * @param <T>      类型
     * @return 转换后的值
     */
    <T> T parseValue(Object value, Class<T> valClass);

    /**
     * 深层次拷贝
     *
     * @param from    待转换的集合类
     * @param toClass 目标类class
     * @param <T>     目标类
     * @return 返回目标类
     */
    <T> List<T> copyBean(List<?> from, Class<T> toClass);

    /**
     * 拷贝对象
     *
     * @param from    待转换的类
     * @param toClass 目标类class
     * @param <T>     目标类
     * @return 返回目标类
     */
    <T> T copyBean(Object from, Class<T> toClass);

    /**
     * 属性拷贝,第一个参数中的属性值拷贝到第二个参数中<br>
     * 注意:当第一个参数中的属性有null值时,不会拷贝进去
     *
     * @param source 源对象
     * @param target 目标对象
     */
    void copyProperties(Object source, Object target);

    /**
     * 属性拷贝,第一个参数中的属性值拷贝到第二个参数中<br>
     * 注意:当第一个参数中的属性有null值时,不会拷贝进去
     *
     * @param source 源对象
     * @param target 目标对象
     */
    void copyPropertiesIgnoreNull(Object source, Object target);

}
