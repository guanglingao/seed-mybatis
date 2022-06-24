package org.seed.mybatis.core.query.param;


import org.seed.mybatis.core.query.Query;
import org.seed.mybatis.core.query.TenantQuery;
import org.seed.mybatis.core.query.annotation.Condition;
import org.seed.mybatis.core.util.StringUtil;

/**
 * 分页排序查询参数，单字段排序，多字段排序参考：{@link PageMultiSortParam}
 *
 * @see PageMultiSortParam 多字段排序
 */
public class PageSortParam extends PageParam implements SchPageableParam, SchSortableParam {


    public PageSortParam(){}

    public PageSortParam(int pageIndex, int pageSize,String sort){
        this.setPageIndex(pageIndex);
        this.setPageSize(pageSize);
        this.setSort(sort);
    }

    /**
     * 排序字段，数据库字段
     *
     * @mock name
     */
    @Condition(ignore = true)
    private String sort;

    /**
     * 排序排序方式，asc或desc
     *
     * @mock desc
     */
    @Condition(ignore = true)
    private String order;

    @Override
    public Query toQuery() {
        Query query = super.toQuery();
        if (StringUtil.hasText(fetchDBSortname()) && StringUtil.hasText(fetchSortOrder())) {
            return query.orderByAsc(fetchDBSortname(), fetchSortOrder());
        }
        return query;
    }

    @Override
    public TenantQuery toTenantQuery() {
        TenantQuery tenantQuery = super.toTenantQuery();
        if (StringUtil.hasText(fetchDBSortname()) && StringUtil.hasText(fetchSortOrder())) {
            tenantQuery.orderByAsc(fetchDBSortname(), fetchSortOrder());
        }
        return tenantQuery;
    }

    @Override
    public String fetchSortName() {
        return getSort();
    }

    @Override
    public String fetchSortOrder() {
        return getOrder();
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
