package org.seed.mybatis.core.query.param;


import org.seed.mybatis.core.query.annotation.Condition;

/**
 * 排序查询参数
 */
public class SortParam implements SchSortableParam {
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
