package org.seed.mybatis.core.mapper;


import org.apache.ibatis.annotations.Param;
import org.seed.mybatis.core.query.Query;

import java.util.Map;

/**
 * 具备更新功能的Mapper
 *
 * @param <E> 实体类
 */
public interface UpdateMapper<E> extends Mapper<E> {
    /**
     * 更新，更新所有字段
     *
     * @param entity 实体类
     * @return 受影响行数
     */
    int update(E entity);

    /**
     * 更新，忽略null字段
     *
     * @param entity 实体类
     * @return 受影响行数
     */
    int updateIgnoreNull(E entity);

    /**
     * 根据条件更新<br>
     * <pre>
     * {@literal
     * Query query = new Query().eq("state", 2);
     * TUser user = new TUser();
     * user.setUsername("李四");
     * int i = mapper.updateByQuery(user, query);
     * }
     * 对应SQL: UPDATE `t_user` SET `username`=? WHERE state = ?
     * </pre>
     *
     * @param entity 待更新的数据
     * @param query  更新条件
     * @return 受影响行数
     */
    int updateByQuery(@Param("entity") E entity, @Param("query") Query query);

    /**
     * 根据条件更新，map中的数据转化成update语句set部分，key为数据库字段名<br>
     * <pre>
     * {@literal
     * Query query = new Query().eq("id", 1);
     * // key为数据库字段名
     * Map<String, Object> map = new LinkedHashMap<>();
     * map.put("username", "李四2");
     * map.put("remark", "123");
     * int i = mapper.updateByMap(map, query);
     * }
     * 对应SQL：UPDATE `t_user` SET username = ? , remark = ? WHERE id = ?
     * </pre>
     *
     * @param map   待更新的数据，key为数据库字段名
     * @param query 更新条件
     * @return 受影响行数
     */
    int updateByMap(@Param("entity") Map<String, Object> map, @Param("query") Query query);
}
