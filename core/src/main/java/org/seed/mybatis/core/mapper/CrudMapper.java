package org.seed.mybatis.core.mapper;


import org.seed.mybatis.core.SeedMybatisContext;
import org.seed.mybatis.core.query.Query;

import java.util.Objects;

/**
 * 具备CRUD的mapper
 *
 * @param <E> 实体类，如：Student
 * @param <I> 主键类型，如：Long，Integer
 */
public interface CrudMapper<E, I> extends QueryMapper<E, I>, EditMapper<E, I> {

    /**
     * 保存或修改，当数据库存在记录执行UPDATE，否则执行INSERT
     *
     * @param entity 实体类
     * @return 受影响行数
     */
    default int saveOrUpdateWithNull(E entity) {
        Objects.requireNonNull(entity, "entity can not null");
        Object pkValue = SeedMybatisContext.getPkValue(entity);
        // 如果存在数据，执行更新操作
        if (pkValue != null) {
            String pkColumnName = SeedMybatisContext.getPkColumnName(entity.getClass());
            Object columnValue = getColumnValue(pkColumnName, new Query().eq(pkColumnName, pkValue), Object.class);
            if (columnValue != null) {
                return this.update(entity);
            }
        }
        return this.save(entity);
    }

    /**
     * 保存或修改，忽略null字段，当数据库存在记录执行UPDATE，否则执行INSERT
     *
     * @param entity 实体类
     * @return 受影响行数
     */
    default int saveOrUpdate(E entity) {
        Objects.requireNonNull(entity, "entity can not null");
        Object pkValue = SeedMybatisContext.getPkValue(entity);
        // 如果存在数据，执行更新操作
        if (pkValue != null) {
            String pkColumnName = SeedMybatisContext.getPkColumnName(entity.getClass());
            Object columnValue = getColumnValue(pkColumnName, new Query().eq(pkColumnName, pkValue), Object.class);
            if (columnValue != null) {
                return this.updateIgnoreNull(entity);
            }
        }
        return this.saveIgnoreNull(entity);
    }

}
