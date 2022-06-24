package org.seed.mybatis.core.query.param;


import org.seed.commons.PageParamDto;
import org.seed.mybatis.core.query.annotation.Condition;

/**
 * 分页查询参数
 */
public class PageParam implements SchPageableParam {

    public PageParam() {
    }

    public static PageParam fromPageParamDto(PageParamDto pageParamDto){
        PageParam pageParam = new PageParam();
        pageParam.setPageIndex(pageParamDto.getPageIndex());
        pageParam.setPageSize(pageParamDto.getPageSize());
        return pageParam;
    }

    public PageParam(int pageIndex, int pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    /**
     * 当前第几页
     *
     * @mock 1
     */
    @Condition(ignore = true)
    private int pageIndex = 1;

    /**
     * 每页记录数
     *
     * @mock 20
     */
    @Condition(ignore = true)
    private int pageSize = 35;

    /**
     * @return
     * @ignore
     */
    @Override
    public int fetchStart() {
        return (this.getPageIndex() - 1) * this.getPageSize();
    }

    /**
     * @return
     * @ignore
     */
    @Override
    public int fetchLimit() {
        return this.getPageSize();
    }

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
