package org.seed.mybatis.core.support;


import org.seed.mybatis.core.mapper.CrudMapper;

/**
 * 通用service接口<br>
 * 使用方式：
 * <pre>
 * <code>
 * {@literal
 * @Service
 * public class UserService implements IService<TUser, Integer> {
 *
 * }
 * }
 * </code>
 * </pre>
 *
 * @param <E> 实体类，如：Student
 * @param <I> 主键类型，如：Long，Integer
 * @see CommonService
 */
public interface IService<E, I> extends CommonService<E, I, CrudMapper<E, I>> {

}
