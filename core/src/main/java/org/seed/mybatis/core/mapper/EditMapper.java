package org.seed.mybatis.core.mapper;

/**
 * 负责编辑的mapper
 *
 * @param <E> 实体类
 * @param <I> 主键类型，如Long
 */
public interface EditMapper<E, I> extends SaveMapper<E>, UpdateMapper<E>, DeleteMapper<E, I> {

}
