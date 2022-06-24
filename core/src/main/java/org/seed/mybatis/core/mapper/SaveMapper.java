package org.seed.mybatis.core.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 具备保存功能的Mapper
 *
 * @param <E>实体类
 */
public interface SaveMapper<E> extends Mapper<E> {
    /**
     * 保存，保存所有字段
     *
     * @param entity 实体类
     * @return 受影响行数
     */
    int save(E entity);

    /**
     * 保存，忽略null字段
     *
     * @param entity 实体类
     * @return 受影响行数
     */
    int saveIgnoreNull(E entity);

    /**
     * 批量保存<br>
     *
     * @param entities 实体类集合
     * @return 受影响行数
     */
    int saveBatch(@Param("entities") Collection<E> entities);

    /**
     * 批量保存,兼容更多的数据库版本,忽略重复行.<br>
     * 此方式采用union的方式批量insert.
     *
     * @param entities 实体类集合
     * @return 受影响行数
     * @see #saveIgnoreNull(Object)
     * @see #saveUnique(Collection, Comparator)
     */
    int saveMultiSet(@Param("entities") Collection<E> entities);

    /**
     * 批量保存，去除重复行，通过对象是否相对判断重复数据，实体类需要实现equals方法.<br>
     *
     * @param entities 实体类集合，需要实现equals方法
     * @return 受影响行数
     */
    default int saveUnique(Collection<E> entities) {
        return saveUnique(entities, null);
    }

    /**
     * 批量保存，去除重复行，指定比较器判断<br>
     *
     * @param entities    实体类集合，需要实现equals方法
     * @param comparator 对象比较器
     * @return 受影响行数
     */
    default int saveUnique(Collection<E> entities, Comparator<E> comparator) {
        if (entities == null || entities.isEmpty()) {
            throw new IllegalArgumentException("parameter 'entitys' can not empty");
        }
        if (comparator == null) {
            return saveBatch(new HashSet<>(entities));
        }
        List<E> list = new ArrayList<>(entities);
        for (int i = 0; i < list.size() - 1; i++) {
            E obj1 = list.get(i);
            for (int j = list.size() - 1; j > i; j--) {
                E obj2 = list.get(j);
                if (comparator.compare(obj1, obj2) == 0) {
                    list.remove(j);
                }
            }
        }
        return saveBatch(list);
    }
}
