package org.seed.mybatis.core.query.param;


public class SortInfo {

    /**
     * 排序字段
     *
     */
    private String sortName;

    /**
     * 排序方式，ASC,DESC
     *
     * @mock DESC
     */
    private String sortOrder;

    public SortInfo() {
    }

    public SortInfo(String sortName, String sortOrder) {
        this.sortName = sortName;
        this.sortOrder = sortOrder;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
