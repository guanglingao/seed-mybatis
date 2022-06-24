package org.seed.mybatis.springboot;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.seed.mybatis.core.SeedMybatisConfig;
import org.seed.mybatis.core.ext.spi.SpiContext;
import org.seed.mybatis.core.handler.BaseFill;
import org.seed.mybatis.springboot.mapper.SqlSessionFactoryBeanExt;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.*;

/**
 * {@link EnableAutoConfiguration Auto-Configuration} for Mybatis. Contributes a
 * {@link SqlSessionFactory} and a {@link SqlSessionTemplate}.
 * <p>
 * If {@link org.mybatis.spring.annotation.MapperScan} is used, or a
 * configuration file is specified as a property, those will be considered,
 * otherwise this auto-configuration will attempt to register mappers based on
 * the interface definitions in or under the root auto-configuration package.
 *
 * @author Eddú Meléndez
 * @author Josh Long
 * @author Kazuki Shimizu
 * @author Eduardo Macarrón
 */
@Slf4j
@Configuration
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
@ConditionalOnBean(DataSource.class)
@EnableConfigurationProperties(MybatisProperties.class)
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
public class SeedMybatisAutoConfiguration implements InitializingBean {


    private final MybatisProperties properties;

    private final Interceptor[] interceptors;

    private final ResourceLoader resourceLoader;

    private final DatabaseIdProvider databaseIdProvider;

    private final List<ConfigurationCustomizer> configurationCustomizers;

    private static ThreadLocal<Collection<String>> packageList = new ThreadLocal<>();

    private final ApplicationContext applicationContext;

    public SeedMybatisAutoConfiguration(MybatisProperties properties, ObjectProvider<Interceptor[]> interceptorsProvider,
                                        ResourceLoader resourceLoader, ObjectProvider<DatabaseIdProvider> databaseIdProvider,
                                        ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider, ApplicationContext applicationContext) {
        this.properties = properties;
        this.interceptors = interceptorsProvider.getIfAvailable();
        this.resourceLoader = resourceLoader;
        this.databaseIdProvider = databaseIdProvider.getIfAvailable();
        this.configurationCustomizers = configurationCustomizersProvider.getIfAvailable();
        this.applicationContext = applicationContext;
        SeedMybatisConfig.ignoreEmptyString = properties.isIgnoreEmptyString();
        SeedMybatisConfig.emptyStringWithTrim = properties.isEmptyStringWithTrim();
    }


    private void checkConfigFileExists() {
        if (this.properties.isCheckConfigLocation() && StringUtils.hasText(this.properties.getConfigLocation())) {
            Resource resource = this.resourceLoader.getResource(this.properties.getConfigLocation());
            Assert.state(resource.exists(), "Cannot find config location: " + resource
                    + " (please add config file or check your Mybatis configuration)");
        }
    }

    @Bean
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Autowired(required = false) DataSource dataSource, SeedMybatisConfig seedMybatisConfig) throws Exception {
        SqlSessionFactoryBeanExt factory = new SqlSessionFactoryBeanExt();
        factory.setDataSource(dataSource);
        factory.setVfs(SpringBootVFS.class);
        if (StringUtils.hasText(this.properties.getConfigLocation())) {
            factory.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
        }
        org.apache.ibatis.session.Configuration configuration = this.properties.getConfiguration();
        if (configuration == null && !StringUtils.hasText(this.properties.getConfigLocation())) {
            configuration = new org.apache.ibatis.session.Configuration();
        }
        if (configuration != null && !CollectionUtils.isEmpty(this.configurationCustomizers)) {
            for (ConfigurationCustomizer customizer : this.configurationCustomizers) {
                customizer.customize(configuration);
            }
        }
        factory.setConfiguration(configuration);
        if (this.properties.getConfigurationProperties() != null) {
            factory.setConfigurationProperties(this.properties.getConfigurationProperties());
        }
        if (!ObjectUtils.isEmpty(this.interceptors)) {
            factory.setPlugins(this.interceptors);
        }
        if (this.databaseIdProvider != null) {
            factory.setDatabaseIdProvider(this.databaseIdProvider);
        }
        if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
            factory.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
        }
        if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
            factory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
        }
        if (!ObjectUtils.isEmpty(this.properties.resolveMapperLocations())) {
            factory.setMapperLocations(this.properties.resolveMapperLocations());
        }
        String basePackage = this.properties.getBasePackage();
        if (!StringUtils.hasText(basePackage)) {
            basePackage = StringUtils.collectionToDelimitedString(packageList.get(), ",");
        }
        factory.setBasePackage(basePackage);
        factory.setConfig(seedMybatisConfig);
        factory.setApplicationContext(applicationContext);
        return factory.getObject();
    }

    @Bean
    @ConditionalOnMissingBean
    public SeedMybatisConfig seedMybatisConfig() {
        SeedMybatisConfig config = new SeedMybatisConfig();
        SpiContext.getBeanExecutor().copyPropertiesIgnoreNull(this.properties, config);
        if (this.properties.getFill() != null) {
            config.setFills(this.buildFills(this.properties.getFill()));
        }
        return config;
    }

    private List<BaseFill<?>> buildFills(Map<String, String> fillMap) {
        Set<Map.Entry<String, String>> entrySet = fillMap.entrySet();
        List<BaseFill<?>> fillList = new ArrayList<>(entrySet.size());
        String className = "", parameter = "";
        try {
            for (Map.Entry<String, String> entry : entrySet) {
                className = entry.getKey();
                parameter = entry.getValue();
                BaseFill<?> handler = (BaseFill<?>) buildInstance(className, parameter);
                fillList.add(handler);
            }
            return fillList;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("属性[mybatis.fill." + className + "=" + parameter + "]设置错误.类" + className + "不存在", e);
        } catch (Exception e) {
            String errorMsg = "属性[mybatis.fill." + className + "=" + parameter + "]设置错误";
            log.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    private static Object buildInstance(String className, String parameter) throws ClassNotFoundException, Exception {
        // 根据类名获取Class对象
        Class<?> clazz = Class.forName(className);
        if (StringUtils.hasLength(parameter)) {
            // 参数类型数组
            Class<?>[] parameterTypes = {String.class};
            // 根据参数类型获取相应的构造函数
            java.lang.reflect.Constructor<?> constructor = clazz.getConstructor(parameterTypes);
            // 根据获取的构造函数和参数，创建实例
            return constructor.newInstance(parameter);
        } else {
            return clazz.getDeclaredConstructor().newInstance();
        }
    }

    @Bean
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        ExecutorType executorType = this.properties.getExecutorType();
        if (executorType != null) {
            return new SqlSessionTemplate(sqlSessionFactory, executorType);
        } else {
            return new SqlSessionTemplate(sqlSessionFactory);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        checkConfigFileExists();
    }

    /**
     * This will just scan the same base package as Spring Boot does. If you
     * want more power, you can explicitly use
     * {@link org.mybatis.spring.annotation.MapperScan} but this will get typed
     * mappers working correctly, out-of-the-box, similar to using Spring Data
     * JPA repositories.
     */
    public static class AutoConfiguredMapperScannerRegistrar implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

        private BeanFactory beanFactory;

        private ResourceLoader resourceLoader;

        private Environment environment;

        @Override
        public void setEnvironment(Environment environment) {
            this.environment = environment;
        }

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                            BeanDefinitionRegistry registry) {

            log.debug("Searching for mappers annotated with @Mapper");

            ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);

            try {
                if (this.resourceLoader != null) {
                    scanner.setResourceLoader(this.resourceLoader);
                }

                Collection<String> packages = AutoConfigurationPackages.get(this.beanFactory);
                String basePackage = environment.getProperty("mybatis.base-package");
                if (StringUtils.hasText(basePackage)) {
                    packages = new HashSet<>();
                    String[] packageArr = basePackage.split(";|,");
                    for (String pkg : packageArr) {
                        packages.add(pkg);
                    }
                }
                if (log.isDebugEnabled()) {
                    for (String pkg : packages) {
                        log.debug("Using auto-configuration base package '{}'", pkg);
                    }
                }

                scanner.setMarkerInterface(org.seed.mybatis.core.mapper.Mapper.class);
                scanner.setAnnotationClass(Mapper.class);
                scanner.registerFilters();
                scanner.doScan(StringUtils.toStringArray(packages));
                log.debug("Scanned packages of: {}", StringUtils.toStringArray(packages));
                packageList.set(packages);
            } catch (IllegalStateException ex) {
                log.debug("Could not determine auto-configuration package, automatic mapper scanning disabled.", ex);
            }
        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory = beanFactory;
        }

        @Override
        public void setResourceLoader(ResourceLoader resourceLoader) {
            this.resourceLoader = resourceLoader;
        }

    }

    /**
     * {@link org.mybatis.spring.annotation.MapperScan} ultimately ends up
     * creating instances of {@link MapperFactoryBean}. If
     * {@link org.mybatis.spring.annotation.MapperScan} is used then this
     * auto-configuration is not needed. If it is _not_ used, however, then this
     * will bring in a bean registrar and automatically register components
     * based on the same component-scanning path as Spring Boot itself.
     */
    @org.springframework.context.annotation.Configuration
    @Import({AutoConfiguredMapperScannerRegistrar.class})
    @ConditionalOnMissingBean(MapperFactoryBean.class)
    public static class MapperScannerRegistrarNotFoundConfiguration implements InitializingBean {
        public void afterPropertiesSet() {
            log.debug("No {} found.", MapperFactoryBean.class.getName());
        }
    }

}
