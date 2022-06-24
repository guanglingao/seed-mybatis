package org.seed.mybatis.core.ext;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import java.util.function.Function;

/**
 * Mapper辅助类
 * <pre>
 * {@literal
 * // 根据主键id查询
 * TUser user = Mappers.run(TUserMapper.class, mapper -> {
 *    return mapper.getById(6);
 * });
 * print(user);
 *
 * // 多条件查询
 * Query query = new Query()
 *         .eq("id", 6)
 *         .gt("money", 1);
 *
 * TUser user = Mappers.run(TUserMapper.class, mapper -> mapper.getByQuery(query));
 * print(user);
 * }
 * </pre>
 */
public class Mappers {

    /**
     * 执行mapper方法，自动提交事务，报错回滚事务，自动关闭session
     * @param mapperClass mapper class，如：<code>TUserMapper.class</code>
     * @param runner 执行器，参数为mapper实例
     * @param <T> mapper类型
     * @param <R> 返回结果
     * @return 返回结果
     */
    public static <T, R> R run(Class<T> mapperClass, Function<T, R> runner) {
        SqlSessionFactory sqlSessionFactory = ExtContext.getSqlSessionFactoryByMapperClass(mapperClass);
        if (sqlSessionFactory == null) {
            throw new IllegalArgumentException("SqlSessionFactory not found by mapper class:" + mapperClass);
        }
        return getMapperRunner(mapperClass).run(runner);
    }

    /**
     * 获取Mapper包装类，可以获取SqlSession<br>
     * 执行run()方法可以自动commit并close。如果单独获取SqlSession进行操作，完了之后必须手动commit并close
     * <pre>
     * {@literal
     * TUser user = Mappers.getMapperRunner(TUserMapper.class).run(mapper -> mapper.getById(3));
     * print(user);
     * }
     * </pre>
     * @param mapperClass Mapper class
     * @param <T> Mapper类型
     * @return 返回包装类
     */
    public static <T> MapperRunner<T> getMapperRunner(Class<T> mapperClass) {
        SqlSessionFactory sqlSessionFactory = ExtContext.getSqlSessionFactoryByMapperClass(mapperClass);
        if (sqlSessionFactory == null) {
            throw new IllegalArgumentException("SqlSessionFactory not found by mapper class:" + mapperClass);
        }
        SqlSession session = sqlSessionFactory.openSession();
        T mapper = session.getMapper(mapperClass);
        return new MapperRunner<>(mapper, session);
    }


}
