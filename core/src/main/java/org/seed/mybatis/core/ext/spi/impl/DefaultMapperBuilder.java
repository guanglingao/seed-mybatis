package org.seed.mybatis.core.ext.spi.impl;


import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.seed.mybatis.core.ext.ExtContext;
import org.seed.mybatis.core.ext.MapperRunner;
import org.seed.mybatis.core.ext.spi.MapperBuilder;


public class DefaultMapperBuilder implements MapperBuilder {

    @Override
    public <T> MapperRunner<T> getMapperRunner(Class<T> mapperClass, Object applicationContext) {
        SqlSessionFactory sessionFactory = ExtContext.getSqlSessionFactoryByMapperClass(mapperClass);
        SqlSession session = sessionFactory.openSession();
        T mapper = session.getMapper(mapperClass);
        return new MapperRunner<>(mapper, session);
    }

}
