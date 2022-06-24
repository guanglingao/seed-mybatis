package org.seed.mybatis.core.support;


import org.seed.mybatis.core.query.annotation.Condition;
import org.seed.mybatis.core.query.param.PageSortParam;

/**
 * 支持easyui表格参数
 */
public class EasyuiDatagridParam extends PageSortParam {

    /**
     * 第几页
     *
     * @mock 1
     */
    @Condition(ignore = true)
    private int page;

    /**
     * 每页记录数
     *
     * @mock 10
     */
    @Condition(ignore = true)
    private int rows;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
        this.setPageIndex(page);
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
        this.setPageSize(rows);
    }

}