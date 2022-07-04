package org.seed.mybatis.springboot.scatter;

public interface ShardingStrategy {

    /**
     * 数据源ID
     *
     * <p>配置在application.yml中的多项数据源</p>
     * @return 数据源ID
     */
    String getDataSourceId();

    /**
     * 表名前缀
     *
     * SQL执行阶段进行拼接
     * @return
     */
    String getTablePrefix();

    /**
     * 表名后缀
     *
     * SQL执行阶段进行拼接
     * @return
     */
    String getTableSuffix();


    /**
     * 设置分库分表的依据
     *
     * @param basis
     */
    void setBasis(Object basis);

}
