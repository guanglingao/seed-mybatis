package org.seed.mybatis.core.query.param;


import org.seed.mybatis.core.ext.code.util.FieldUtil;
import org.seed.mybatis.core.query.Query;
import org.seed.mybatis.core.query.TenantQuery;
import org.seed.mybatis.core.query.annotation.Condition;

import java.util.List;

/**
 * 分页排序查询参数，支持多参数排序
 */
public class PageMultiSortParam extends PageParam implements SchPageableParam {

    public PageMultiSortParam(){}

    public PageMultiSortParam(int pageIndex, int pageSize,List<SortInfo> sorts){
        this.setPageIndex(pageIndex);
        this.setPageSize(pageSize);
        this.setSorts(sorts);
    }

    /**
     * 排序字段
     */
    @Condition(ignore = true)
    private List<SortInfo> sorts;

    /**
     * 排字段，格式：<code>sortname1=sortorder1,sortname2=sortorder2</code><br>
     * 前端需要urlencode处理encodeURIComponent("id=desc,addTime=asc")，拼接请求链接：xx/order/find?sortInfo=id%3Ddesc%2CaddTime%3Dasc
     */
    @Condition(ignore = true)
    private String sortInfo;

    @Override
    public Query toQuery() {
        Query query = super.toQuery();
        addSort(query);
        return query;
    }

    @Override
    public TenantQuery toTenantQuery() {
        TenantQuery tenantQuery = super.toTenantQuery();
        addSort(tenantQuery);
        return tenantQuery;
    }

    private void addSort(Query query) {
        if (sortInfo != null && sortInfo.length() > 0) {
            String[] split = sortInfo.split(",");
            for (String item : split) {
                String[] pair = item.split("=");
                query.orderByAsc(formatSortName(pair[0]), pair[1]);
            }
        } else if (sorts != null) {
            for (SortInfo sort : sorts) {
                query.orderByAsc(formatSortName(sort.getSortName()), sort.getSortOrder());
            }
        }
    }

    protected String formatSortName(String sortName) {
        return FieldUtil.camelToUnderline(sortName);
    }

    public List<SortInfo> getSorts() {
        return sorts;
    }

    public void setSorts(List<SortInfo> sorts) {
        this.sorts = sorts;
    }

    public String getSortInfo() {
        return sortInfo;
    }

    public void setSortInfo(String sortInfo) {
        this.sortInfo = sortInfo;
    }
}
