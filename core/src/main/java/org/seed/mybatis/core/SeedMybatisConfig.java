package org.seed.mybatis.core;


import org.seed.mybatis.core.handler.BaseFill;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * fastmybatis配置项
 */
public class SeedMybatisConfig {

    public static final String COUNT_EXPRESSION = "count(*)";

    private static final String GLOBAL_VM_PLACEHOLDER = "<!--_global_vm_-->";

    /**
     * 忽略空字符串，全局配置
     */
    public static volatile boolean ignoreEmptyString = true;

    /**
     * 查询字段去掉首尾空格
     */
    public static volatile boolean emptyStringWithTrim = true;

    /**
     * 数据库忽略更新的字段，如gmt_create,gmt_modified
     */
    public static volatile List<String> defaultIgnoreUpdateColumns = Collections.emptyList();

    /**
     * Mybatis xml文件存放位置，如：mybatis/mapper
     */
    private String[] mapperLocations;

    /**
     * 驼峰转换下划线
     */
    private boolean camel2underline = Boolean.TRUE;

    /**
     * 生成mapper的线程池大小
     */
    private int mapperExecutorPoolSize = 5;

    /**
     * 模板文件classpath
     */
    private String templateClasspath;

    /**
     * 通用mapper路径
     */
    private String commonSqlClasspath = "seed-mybatis/commonSql.xml";

    /**
     * mapper文件保存文件夹地址，如：C:/mapper
     */
    private String mapperSaveDir;

    private String countExpression = COUNT_EXPRESSION;

    /**
     * 全局模板classpath
     */
    private String globalVmLocation;

    /**
     * 逻辑删除,指定未删除时的值
     */
    private String logicNotDeleteValue = "0";

    /**
     * 逻辑删除,删除后库数库据保存的值
     */
    private String logicDeleteValue = "1";

    /**
     * 填充器
     */
    private List<BaseFill<?>> fillList = new ArrayList<>(8);

    /**
     * 开启mapper文件热部署，开启后修改mapper文件无需重启
     */
    private boolean hotDeploy;

    /**
     * 级联查询忽略逻辑删除字段
     */
    private boolean ignoreLogicDeleteWithAssociation;

    /**
     * 全局主键名称
     */
    private String globalIdName = "id";

    /**
     * 全局主键是否自增
     */
    private boolean globalIdIncrement = true;

    /**
     * 数据库忽略更新的字段，如gmt_create,gmt_modified
     */
    private List<String> ignoreUpdateColumns = new ArrayList<>(8);

    public SeedMybatisConfig() {
        ignoreUpdateColumns.addAll(defaultIgnoreUpdateColumns);
    }

    public String[] getMapperLocations() {
        return mapperLocations;
    }

    public void setMapperLocations(String[] mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

    public String getGlobalVmPlaceholder() {
        return GLOBAL_VM_PLACEHOLDER;
    }

    /**
     * 驼峰转换下划线，如果配置了true，则java字段映射成数据库字段将自动转成下划线形式
     *
     * @param camel2underline 如果配置了true，则java字段映射成数据库字段将自动转成下划线形式
     */
    public void setCamel2underline(boolean camel2underline) {
        this.camel2underline = camel2underline;
    }

    /**
     * 是否执行驼峰转下划线. 如果配置了true，则java字段映射成数据库字段将自动转成下划线形式
     *
     * @return 配置了true，则java字段映射成数据库字段将自动转成下划线形式
     */
    public boolean isCamel2underline() {
        return camel2underline;
    }

    public int getMapperExecutorPoolSize() {
        return mapperExecutorPoolSize;
    }

    public void setMapperExecutorPoolSize(int mapperExecutorPoolSize) {
        if (mapperExecutorPoolSize <= 0) {
            throw new IllegalArgumentException("mapperExecutorPoolSize必须大于0");
        }
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

    /**
     * 将mapper文件保存到指定文件夹中<br>
     * 因为fastmybatis是直接将mapper内容注入到内存当中，开发人员无感知，并且不知道mapper内容是什么样子<br>
     * 这个功能就是让开发人员能够查看到对应的mapper内容，方便定位和排查问题。<br>
     * 一般情况下此项不用开启。
     *
     * @param mapperSaveDir mapper文件保存文件夹地址，如：C:/mapper
     */
    public void setMapperSaveDir(String mapperSaveDir) {
        File dir = new File(mapperSaveDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IllegalArgumentException("创建Mapper文件夹失败：" + mapperSaveDir);
        }
        this.mapperSaveDir = mapperSaveDir;
    }

    /**
     * 设置字段填充
     *
     * @param <T>   填充器类型
     * @param fills 填充器数组
     */
    public <T extends BaseFill<?>> void setFills(List<T> fills) {
        for (BaseFill<?> fill : fills) {
            fill.setSeedMybatisConfig(this);
            fillList.add(fill);
        }
        fillList.sort(Comparator.comparingInt(BaseFill::getOrder));
    }

    public BaseFill<?> getFill(Class<?> entityClass, Field field, String columnName) {
        for (BaseFill<?> fillHandler : fillList) {
            if (fillHandler.match(entityClass, field, columnName)) {
                return fillHandler;
            }
        }
        return null;
    }

    public void setCountExpression(String countExpression) {
        this.countExpression = countExpression;
    }

    public String getCountExpression() {
        return countExpression;
    }

    public String getGlobalVmLocation() {
        return globalVmLocation;
    }

    public void setGlobalVmLocation(String globalVmLocation) {
        this.globalVmLocation = globalVmLocation;
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

    public boolean isHotDeploy() {
        return hotDeploy;
    }

    public void setHotDeploy(boolean hotDeploy) {
        this.hotDeploy = hotDeploy;
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

    public String getGlobalIdName() {
        return globalIdName;
    }

    public void setGlobalIdName(String globalIdName) {
        this.globalIdName = globalIdName;
    }

    public boolean isGlobalIdIncrement() {
        return globalIdIncrement;
    }

    public void setGlobalIdIncrement(boolean globalIdIncrement) {
        this.globalIdIncrement = globalIdIncrement;
    }
}
