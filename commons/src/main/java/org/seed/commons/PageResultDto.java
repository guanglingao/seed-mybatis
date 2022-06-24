package org.seed.commons;

import java.io.Serializable;
import java.util.List;


/**
 * 分页数据结果返回
 *
 * @param <E>
 */
public class PageResultDto<E> implements Serializable {

    /**
     * 结果集
     */
    private List<E> list;
    /**
     * 总记录数
     */
    private long total = 0;
    /**
     * 当前记录位置
     */
    private int start = 0;
    /**
     * 当前第几页
     */
    private int pageIndex = 1;
    /**
     * 每页记录数
     */
    private int pageSize = 35;
    /**
     * 总页数
     */
    private int pageCount = 0;


    public List<E> getList() {
        return list;
    }

    public void setList(List<E> list) {
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
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

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

}
