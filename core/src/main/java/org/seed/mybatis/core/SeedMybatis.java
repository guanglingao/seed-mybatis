package org.seed.mybatis.core;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.seed.mybatis.core.ext.SqlSessionFactoryBuilderExt;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;

/**
 *
 */
public class SeedMybatis {

    protected SqlSessionFactory sqlSessionFactory;

    protected String configLocation;

    protected String[] mapperLocations;

    protected String basePackage;

    protected String environment;

    protected Properties properties;

    protected SeedMybatisConfig config = new SeedMybatisConfig();

    protected String dialect;

    /**
     * 创建SeedMybatis对象
     *
     * @return 返回SeedMybatis对象
     */
    public static SeedMybatis create() {
        return new SeedMybatis();
    }

    /**
     * 指定mybatis xml文件classpath目录，如：mybatis/mapper/XXX.xml,则指定：<code>mybatis/mapper</code><br>
     * <p>
     * 一旦指定了这个参数，<code>mybatis-config.xml</code>文件不用配置{@literal <mapper></mapper>}节点，系统会自动加载xml文件
     * </p>
     *
     * @param mapperLocations mybatis xml文件classpath目录
     * @return 返回SeedMybatis对象
     */
    public SeedMybatis mapperLocations(String... mapperLocations) {
        this.mapperLocations = mapperLocations;
        return this;
    }

    /**
     * mybatis-config.xml文件classpath路径，如：mybatis/mybatis-config.xml
     *
     * @param configLocation mybatis-config.xml文件classpath路径
     * @return 返回SeedMybatis对象
     */
    public SeedMybatis configLocation(String configLocation) {
        this.configLocation = configLocation;
        return this;
    }

    /**
     * 扫描mapper所在的package，多个使用英文逗号（,）隔开。尽量指定到mapper所在的package，减少扫描时间，如：com.xx.xx.mapper
     *
     * @param basePackage mapper所在的package
     * @return 返回SeedMybatis对象
     */
    public SeedMybatis basePackage(String basePackage) {
        this.basePackage = basePackage;
        return this;
    }

    /**
     * 设置配置项
     *
     * @param config 配置类
     * @return 返回SeedMybatis对象
     */
    public SeedMybatis config(SeedMybatisConfig config) {
        this.config = config;
        return this;
    }

    /**
     * 指定环境名称。不指定默认为{@literal <environment></environment>}的id属性
     *
     * @param environment 环境名称
     * @return 返回SeedMybatis对象
     */
    public SeedMybatis environment(String environment) {
        this.environment = environment;
        return this;
    }

    /**
     * 指定额外属性
     *
     * @param properties 额外属性
     * @return 返回SeedMybatis对象
     */
    public SeedMybatis properties(Properties properties) {
        this.properties = properties;
        return this;
    }

    /**
     * 设置数据库方言
     *
     * @param dialect 如：mysql
     * @return 返回SeedMybatis对象
     */
    public SeedMybatis dialect(String dialect) {
        this.dialect = Objects.requireNonNull(dialect);
        return this;
    }

    public SeedMybatis load() {
        Objects.requireNonNull(configLocation);
        Objects.requireNonNull(basePackage);
        Objects.requireNonNull(config);
        this.config.setMapperLocations(mapperLocations);
        try {
            InputStream inputStream = Resources.getResourceAsStream(configLocation);
            SqlSessionFactoryBuilderExt sqlSessionFactoryBuilderExt = new SqlSessionFactoryBuilderExt(basePackage, config, dialect);
            this.sqlSessionFactory = sqlSessionFactoryBuilderExt.build(inputStream, environment, properties);
        } catch (IOException e) {
            throw new RuntimeException("初始化mybatis失败", e);
        }
        return this;
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    @Override
    public String toString() {
        return "SeedMybatis{" +
                "configLocation='" + configLocation + '\'' +
                ", mapperLocations=" + Arrays.toString(mapperLocations) +
                ", basePackage='" + basePackage + '\'' +
                '}';
    }
}
