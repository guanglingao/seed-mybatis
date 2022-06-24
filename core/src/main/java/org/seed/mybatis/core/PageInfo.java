package org.seed.mybatis.core;

import org.seed.commons.PageResultDto;

import java.util.List;
import java.util.function.Function;

/**
 * 封装查询结果
 *
 * @param <E> 实体类
 * @author tanghc
 */
public class PageInfo<E> extends PageSupport<E> {


    private static final long serialVersionUID = 5104636317609298856L;

    /**
     * 当前页
     *
     * @return 返回当前页页码
     * @mock 1
     */
    public int getCurrentPageIndex() {
        return this.fetchCurrentPageIndex();
    }

    /**
     * 上一页
     *
     * @return 返回上一页页码
     * @mock 1
     */
    public int getPrePageIndex() {
        return this.fetchPrePageIndex();
    }

    /**
     * 下一页
     *
     * @return 返回下一页页码
     * @mock 2
     */
    public int getNextPageIndex() {
        return this.fetchNextPageIndex();
    }

    /**
     * 首页
     *
     * @return 返回1
     * @mock 1
     */
    public int getFirstPageIndex() {
        return this.fetchFirstPageIndex();
    }

    /**
     * 最后一页
     *
     * @return 返回最后一页页码
     * @mock 10
     */
    public int getLastPageIndex() {
        return this.fetchLastPageIndex();
    }

    /**
     * 结果集
     *
     * @return 返回结果集
     */
    public List<E> getData() {
        return this.fetchList();
    }

    /**
     * 总记录数
     *
     * @return 返回总记录数
     * @mock 20
     */
    public long getTotal() {
        return this.fetchTotal();
    }

    /**
     * 当前页索引,等同于currentPageIndex
     *
     * @return 返回当前页索引
     * @mock 1
     * @see #getCurrentPageIndex()
     */
    public int getPageIndex() {
        return this.fetchCurrentPageIndex();
    }

    /**
     * 每页显示几条记录
     *
     * @return 返回每页大小
     * @mock 10
     */
    public int getPageSize() {
        return this.fetchPageSize();
    }

    /**
     * 共几页
     *
     * @return 返回总页数
     * @mock 10
     */
    public int getPageCount() {
        return this.fetchPageCount();
    }

    /**
     * 起始页索引，从0开始，不同于pageIndex
     *
     * @return 返回起始页索引
     * @mock 10
     */
    protected int getStart() {
        return this.fetchStart();
    }


    /**
     * 转化对象类型
     *
     * @return
     */
    public PageResultDto toPageResultDto(){
        PageResultDto pageResultDto = new PageResultDto();
        pageResultDto.setPageIndex(this.getPageIndex());
        pageResultDto.setPageSize(this.getPageSize());
        pageResultDto.setPageCount(this.getPageCount());
        pageResultDto.setStart(this.getStart());
        pageResultDto.setTotal(this.getTotal());
        pageResultDto.setList(this.getData());
        return pageResultDto;
    }

}
