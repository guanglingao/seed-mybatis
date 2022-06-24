package org.seed.mybatis.core.support;


import org.seed.mybatis.core.SeedMybatisContext;
import org.seed.mybatis.core.ext.MapperRunner;
import org.seed.mybatis.core.mapper.CrudMapper;
import org.seed.mybatis.core.util.ClassUtil;

/**
 * 通用service<br>
 * 使用方式：
 * <pre>
 * <code>
 * {@literal
 * @Service
 * public class UserService extends BaseService<TUser, Integer, TUserMapper> {
 *
 * }
 * }
 * </code>
 * </pre>
 *
 * @param <E>      实体类，如：Student
 * @param <I>      主键类型，如：Long，Integer
 * @param <Mapper> Mapper接口
 */
public abstract class BaseService<E, I, Mapper extends CrudMapper<E, I>> implements CommonService<E, I, Mapper> {

    @Override
    public MapperRunner<Mapper> getMapperRunner() {
        Class<E> entityClass = (Class<E>) ClassUtil.getSuperClassGenricType(getClass(), 0);
        return SeedMybatisContext.getCrudMapperRunner(entityClass);
    }
}
