package org.seed.mybatis.springboot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * Configuration properties for MyBatis.
 *
 * @author Eddú Meléndez
 * @author Kazuki Shimizu
 */
@ConfigurationProperties(prefix = MybatisProperties.MYBATIS_PREFIX)
public class MybatisProperties {

    private static final String COMMON_SQL_CLASSPATH = "seed-mybatis/commonSql.xml";

    public static final String MYBATIS_PREFIX = "mybatis";

    /**
     * Location of MyBatis xml config file.
     */
    private String configLocation;

    /**
     * Locations of MyBatis mapper files.
     */
    private String[] mapperLocations;

    /**
     * Packages to search type aliases. (Package delimiters are ",; \t\n")
     */
    private String typeAliasesPackage;

    /**
     * Packages to search for type handlers. (Package delimiters are ",; \t\n")
     */
    private String typeHandlersPackage;

    /**
     * Indicates whether perform presence check of the MyBatis xml config file.
     */
    private boolean checkConfigLocation = false;

    /**
     * Execution mode for {@link org.mybatis.spring.SqlSessionTemplate}.
     */
    private ExecutorType executorType;

    /**
     * Externalized properties for MyBatis configuration.
     */
    private Properties configurationProperties;

    /**
     * A Configuration object for customize default settings. If
     * {@link #configLocation} is specified, this property is not used.
     */
    @NestedConfigurationProperty
    private Configuration configuration;

    /**
     * 驼峰转换下划线
     */
    private boolean camel2underline = Boolean.TRUE;
    /**
     * 生成mapper的线程池大小
     */
    private int mapperExecutorPoolSize = 20;
    /**
     * 模板文件classpath
     */
    private String templateClasspath;
    /**
     * 通用mapper路径
     */
    private String commonSqlClasspath = COMMON_SQL_CLASSPATH;
    /**
     * mapper文件保存文件夹地址，如：C:/mapper
     */
    private String mapperSaveDir;
    /**
     * Dao路径
     */
    private String basePackage;

    /**
     * 全局模板路径，支持file:，classpath:方式
     */
    private String globalVmLocation;

    /**
     * key:填充器全路径类名,value:构造函数参数值
     */
    private Map<String, String> fill;

    /**
     * 逻辑删除,删除后库数库据保存的值
     */
    private String logicDeleteValue = "1";
    /**
     * 逻辑删除,未删除库数库据保存的值
     */
    private String logicNotDeleteValue = "0";

    /**
     * 开启mapper文件热部署，开启后修改mapper文件无需重启
     */
    private boolean hotDeploy;

    /**
     * 为true，级联查询忽略逻辑删除字段，默认false
     */
    private boolean ignoreLogicDeleteWithAssociation;

    /**
     * 忽略空字符串
     */
    private boolean ignoreEmptyString = true;

    /**
     * 去掉首尾空格判断空字符串
     */
    private boolean emptyStringWithTrim = true;

    /**
     * 数据库忽略更新的字段，如gmt_create,gmt_modified<br>
     *
     * @since 1.8.1
     */
    private List<String> ignoreUpdateColumns = new ArrayList<>(8);

    public boolean isCamel2underline() {
        return camel2underline;
    }

    public void setCamel2underline(boolean camel2underline) {
        this.camel2underline = camel2underline;
    }

    public int getMapperExecutorPoolSize() {
        return mapperExecutorPoolSize;
    }

    public void setMapperExecutorPoolSize(int mapperExecutorPoolSize) {
        this.mapperExecutorPoolSize = mapperExecutorPoolSize;
    }

    public String getTemplateClasspath() {
        return templateClasspath;
    }

    public void setTemplateClasspath(String templateClasspath) {
        this.templateClasspath = templateClasspath;
    }

    public String getCommonSqlClasspath() {
        return commonSqlClasspath;
    }

    public void setCommonSqlClasspath(String commonSqlClasspath) {
        this.commonSqlClasspath = commonSqlClasspath;
    }

    public String getMapperSaveDir() {
        return mapperSaveDir;
    }

    public void setMapperSaveDir(String mapperSaveDir) {
        this.mapperSaveDir = mapperSaveDir;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    /**
     * @since 1.1.0
     */
    public String getConfigLocation() {
        return this.configLocation;
    }

    /**
     * @since 1.1.0
     */
    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }

    @Deprecated
    public String getConfig() {
        return this.configLocation;
    }

    @Deprecated
    public void setConfig(String config) {
        this.configLocation = config;
    }

    public String[] getMapperLocations() {
        return this.mapperLocations;
    }

    public void setMapperLocations(String[] mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

    public String getTypeHandlersPackage() {
        return this.typeHandlersPackage;
    }

    public void setTypeHandlersPackage(String typeHandlersPackage) {
        this.typeHandlersPackage = typeHandlersPackage;
    }

    public String getTypeAliasesPackage() {
        return this.typeAliasesPackage;
    }

    public void setTypeAliasesPackage(String typeAliasesPackage) {
        this.typeAliasesPackage = typeAliasesPackage;
    }

    public boolean isCheckConfigLocation() {
        return this.checkConfigLocation;
    }

    public void setCheckConfigLocation(boolean checkConfigLocation) {
        this.checkConfigLocation = checkConfigLocation;
    }

    public ExecutorType getExecutorType() {
        return this.executorType;
    }

    public void setExecutorType(ExecutorType executorType) {
        this.executorType = executorType;
    }

    /**
     * @since 1.2.0
     */
    public Properties getConfigurationProperties() {
        return configurationProperties;
    }

    /**
     * @since 1.2.0
     */
    public void setConfigurationProperties(Properties configurationProperties) {
        this.configurationProperties = configurationProperties;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Resource[] resolveMapperLocations() {
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        List<Resource> resources = new ArrayList<Resource>();
        if (this.mapperLocations != null) {
            for (String mapperLocation : this.mapperLocations) {
                try {
                    Resource[] mappers = resourceResolver.getResources(mapperLocation);
                    resources.addAll(Arrays.asList(mappers));
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return resources.toArray(new Resource[resources.size()]);
    }

    public Map<String, String> getFill() {
        return fill;
    }

    public void setFill(Map<String, String> fill) {
        this.fill = fill;
    }

    public String getLogicDeleteValue() {
        return logicDeleteValue;
    }

    public void setLogicDeleteValue(String logicDeleteValue) {
        this.logicDeleteValue = logicDeleteValue;
    }

    public String getLogicNotDeleteValue() {
        return logicNotDeleteValue;
    }

    public void setLogicNotDeleteValue(String logicNotDeleteValue) {
        this.logicNotDeleteValue = logicNotDeleteValue;
    }

    public String getGlobalVmLocation() {
        return globalVmLocation;
    }

    public void setGlobalVmLocation(String globalVmLocation) {
        this.globalVmLocation = globalVmLocation;
    }

    public boolean isHotDeploy() {
        return hotDeploy;
    }

    public void setHotDeploy(boolean hotDeploy) {
        this.hotDeploy = hotDeploy;
    }

    public boolean getHotDeploy() {
        return hotDeploy;
    }

    public List<String> getIgnoreUpdateColumns() {
        return ignoreUpdateColumns;
    }

    public void setIgnoreUpdateColumns(List<String> ignoreUpdateColumns) {
        this.ignoreUpdateColumns = ignoreUpdateColumns;
    }

    public boolean getIgnoreLogicDeleteWithAssociation() {
        return ignoreLogicDeleteWithAssociation;
    }

    public void setIgnoreLogicDeleteWithAssociation(boolean ignoreLogicDeleteWithAssociation) {
        this.ignoreLogicDeleteWithAssociation = ignoreLogicDeleteWithAssociation;
    }

    public boolean isIgnoreLogicDeleteWithAssociation() {
        return ignoreLogicDeleteWithAssociation;
    }

    public boolean isIgnoreEmptyString() {
        return ignoreEmptyString;
    }

    public void setIgnoreEmptyString(boolean ignoreEmptyString) {
        this.ignoreEmptyString = ignoreEmptyString;
    }

    public boolean isEmptyStringWithTrim() {
        return emptyStringWithTrim;
    }

    public void setEmptyStringWithTrim(boolean emptyStringWithTrim) {
        this.emptyStringWithTrim = emptyStringWithTrim;
    }
}
