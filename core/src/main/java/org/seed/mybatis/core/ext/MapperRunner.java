package org.seed.mybatis.core.ext;

import org.apache.ibatis.session.SqlSession;
import org.seed.mybatis.core.util.IOUtil;

import java.util.Optional;
import java.util.function.Function;

/**
 * Mapper包装类，用来执行mapper方法，控制事务提交、回滚、session关闭功能
 *
 */
public class MapperRunner<Mapper> {

    private final Mapper mapper;

    // 使用Optional包装，因为spring环境下自动管理session，不需要这个
    private final Optional<SqlSession> sessionOptional;

    public MapperRunner(Mapper mapper, SqlSession session) {
        this.mapper = mapper;
        this.sessionOptional = Optional.ofNullable(session);
    }

    /**
     * 执行mapper方法，自动提交事务，报错回滚事务，自动关闭session
     * @param runner 执行器，参数为mapper实例
     * @param <R> 返回类型
     * @return 返回结果
     */
    public <R> R run(Function<Mapper, R> runner) {
        try {
            R ret = runner.apply(mapper);
            sessionOptional.ifPresent(SqlSession::commit);
            return ret;
        } catch (Throwable e) {
            sessionOptional.ifPresent(SqlSession::rollback);
            throw new RuntimeException(e);
        } finally {
            sessionOptional.ifPresent(IOUtil::closeQuietly);
        }
    }

    public Mapper getMapper() {
        return mapper;
    }

    public Optional<SqlSession> getSessionOptional() {
        return sessionOptional;
    }
}
