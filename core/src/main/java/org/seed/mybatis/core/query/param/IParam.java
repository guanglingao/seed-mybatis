package org.seed.mybatis.core.query.param;


import org.seed.mybatis.core.query.Query;
import org.seed.mybatis.core.query.TenantQuery;

import java.io.Serializable;


public interface IParam extends Serializable {

    /**
     * 生成Query查询对象
     *
     * @return 返回查询对象
     */
    default Query toQuery() {
        return new Query().addAnnotationExpression(this);
    }

    /**
     * 生成TenantQuery查询对象
     *
     * @return 返回TenantQuery
     */
    default TenantQuery toTenantQuery() {
        TenantQuery dynamicQuery = new TenantQuery();
        dynamicQuery.addAnnotationExpression(this);
        return dynamicQuery;
    }
}
