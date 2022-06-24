package org.seed.mybatis.core.query;


@FunctionalInterface
public interface TenantQueryListener {

    /**
     * 每次初始化Query时触发
     * @param query TenantQuery
     */
    void onInitQuery(TenantQuery query);

}
