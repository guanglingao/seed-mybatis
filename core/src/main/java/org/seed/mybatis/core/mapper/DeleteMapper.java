package org.seed.mybatis.core.mapper;


import org.apache.ibatis.annotations.Param;
import org.seed.mybatis.core.SeedMybatisContext;
import org.seed.mybatis.core.query.Query;
import org.seed.mybatis.core.util.ClassUtil;

import java.util.Collection;
import java.util.Objects;

/**
 * 具备删除功能的Mapper
 *
 * @param <E> 实体类
 * @param <I> 主键类型，如Long
 */
public interface DeleteMapper<E, I> extends Mapper<E> {

    /**
     * 删除记录（底层根据id删除），在有逻辑删除字段的情况下，做UPDATE操作。
     *
     * @param entity 实体类
     * @return 受影响行数
     */
    int delete(E entity);

    /**
     * 根据id删除，在有逻辑删除字段的情况下，做UPDATE操作
     *
     * @param id 主键id值
     * @return 受影响行数
     */
    int deleteById(I id);

    /**
     * 根据多个主键id删除，在有逻辑删除字段的情况下，做UPDATE操作
     *
     * @param ids 主键id
     * @return 返回影响行数
     */
    default int deleteByIds(Collection<I> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("parameter 'ids' can not empty");
        }
        Class<E> entityClass = (Class<E>) ClassUtil.getSuperInterfaceGenericType(getClass(), 0);
        String pkColumnName = SeedMybatisContext.getPkColumnName(entityClass);
        return this.deleteByColumn(pkColumnName, ids);
    }

    /**
     * 根据指定字段值删除，在有逻辑删除字段的情况下，做UPDATE操作<br>
     * <pre>
     * 根据数组删除
     * {@literal mapper.deleteByColumn("username", Arrays.asList("jim", "tom")); }
     * 对应SQL:DELETE FROM table WHERE username in ('jim', 'tom')
     *
     * 根据某个值删除
     * {@literal mapper.deleteByColumn("username", "jim"); }
     * 对应SQL:DELETE FROM table WHERE username = 'jim'
     * </pre>
     *
     * @param column 数据库字段名
     * @param value  条件值，可以是单值String，int，也可以是集合List，Collection
     * @return 返回影响行数
     */
    default int deleteByColumn(String column, Object value) {
        if (column == null || "".equals(column)) {
            throw new IllegalArgumentException("parameter 'columns' can not blank");
        }
        Objects.requireNonNull(value, "value can not null");
        Query query;
        if (value instanceof Collection) {
            query = new Query().in(column, (Collection<?>) value);
        } else {
            query = new Query().eq(column, value);
        }
        return deleteByQuery(query);
    }

    /**
     * 根据条件删除，在有逻辑删除字段的情况下，做UPDATE操作<br>
     * <pre>
     * {@literal
     * Query query = new Query();
     * query.eq("state", 3);
     * int i = mapper.deleteByQuery(query);
     * }
     * 对应SQL:
     * DELETE FROM `t_user` WHERE state = 3
     * </pre>
     *
     * @param query 查询对象
     * @return 受影响行数
     */
    int deleteByQuery(@Param("query") Query query);


    /**
     * 强制删除（底层根据id删除），忽略逻辑删除字段，执行DELETE语句
     *
     * @param entity 实体类
     * @return 受影响行数
     */
    int forceDelete(E entity);

    /**
     * 根据id强制删除，忽略逻辑删除字段，执行DELETE语句
     *
     * @param id 主键id值
     * @return 受影响行数
     */
    int forceDeleteById(I id);

    /**
     * 根据条件强制删除，忽略逻辑删除字段，执行DELETE语句
     *
     * @param query 查询对象
     * @return 受影响行数
     */
    int forceDeleteByQuery(@Param("query") Query query);

}
