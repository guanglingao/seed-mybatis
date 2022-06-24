package org.seed.commons;

import java.io.Serializable;


/**
 * 分页参数
 */
public class PageParamDto implements Serializable {

    /**
     * 当前页码
     */
    private int pageIndex = 1;

    /**
     * 每页记录数
     *
     * @mock 20
     */
    private int pageSize = 35;

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }


}
