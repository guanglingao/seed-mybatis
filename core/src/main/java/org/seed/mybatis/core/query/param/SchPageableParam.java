package org.seed.mybatis.core.query.param;


import org.seed.mybatis.core.query.Query;
import org.seed.mybatis.core.query.TenantQuery;

/**
 * 分页查询参数
 */
public interface SchPageableParam extends IParam {
    /**
     * 返回第一条记录的索引值
     *
     * @return 返回第一条记录的索引值
     */
    int fetchStart();

    /**
     * 返回第一条记录的索引值
     *
     * @return 返回第一条记录的索引值
     */
    int fetchLimit();

    @Override
    default Query toQuery() {
        return IParam.super.toQuery().limit(fetchStart(), fetchLimit());
    }

    @Override
    default TenantQuery toTenantQuery() {
        TenantQuery tenantQuery = IParam.super.toTenantQuery();
        tenantQuery.limit(fetchStart(), fetchLimit());
        return tenantQuery;
    }
}
