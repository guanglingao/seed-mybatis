package org.seed.mybatis.core.ext;

import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.seed.mybatis.core.SeedMybatisConfig;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

/**
 * 扩展SqlSessionFactoryBuilder
 */
public class SqlSessionFactoryBuilderExt extends SqlSessionFactoryBuilder {

    private final String basePackage;

    private final SeedMybatisConfig config;

    private final String dialect;


    public SqlSessionFactoryBuilderExt(String basePackage, SeedMybatisConfig config) {
        this(basePackage, config, null);
    }

    public SqlSessionFactoryBuilderExt(String basePackage, SeedMybatisConfig config, String dialect) {
        this.basePackage = basePackage;
        this.config = config;
        this.dialect = dialect;
    }

    @Override
    public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
        try {
            XmlConfigBuilderExt parser = new XmlConfigBuilderExt(inputStream, environment, properties);
            parser.setBasePackage(basePackage);
            parser.setConfig(config);
            parser.setDialect(dialect);
            SqlSessionFactory sqlSessionFactory = build(parser.parse());
            String envId = sqlSessionFactory.getConfiguration().getEnvironment().getId();
            ExtContext.addSqlSessionFactory(envId, sqlSessionFactory);
            Set<Class<?>> mapperClasses = parser.getMapperLocationsBuilder().getMapperClasses();
            for (Class<?> mapperClass : mapperClasses) {
                ExtContext.addSqlSessionFactory(mapperClass, sqlSessionFactory);
            }
            return sqlSessionFactory;
        } catch (Exception e) {
            throw ExceptionFactory.wrapException("Error building SqlSession.", e);
        } finally {
            ErrorContext.instance().reset();
            try {
                inputStream.close();
            } catch (IOException e) {
                // Intentionally ignore. Prefer previous error.
            }
        }
    }

}
