package org.seed.mybatis.core;


import org.seed.mybatis.core.ext.spi.SpiContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 分页支持类
 *
 * @param <E> 实体类
 */
public class PageSupport<E> implements PageResult<E> {

    private static final long serialVersionUID = 5931004082164727399L;

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
    private int pageSize = 10;
    /**
     * 总页数
     */
    private int pageCount = 0;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public PageResult process(EntityProcessor<E> processor) {
        if (processor == null) {
            throw new IllegalArgumentException("processor不能为null");
        }
        PageResult pageResult = this;
        List<Object> jsonObjList = processEntityToJSONObject(list, processor);
        pageResult.setList(jsonObjList);
        return pageResult;
    }

    /**
     * 将list中的entity对象处理成JSONObject对象
     *
     * @param list      数据源
     * @param processor 处理器
     * @param <E>       实体类
     * @return 返回新的结果集
     */
    public static <E> List<Object> processEntityToJSONObject(List<E> list,
                                                             EntityProcessor<E> processor) {
        List<Object> jsonObjList = new ArrayList<>(list.size());

        for (E entity : list) {
            Map<String, Object> jsonObject = SpiContext.getBeanExecutor().pojoToMap(entity);
            processor.process(entity, jsonObject);
            jsonObjList.add(jsonObject);
        }

        return jsonObjList;
    }

    protected int fetchCurrentPageIndex() {
        return pageIndex;
    }

    /**
     * 上一页
     *
     * @return 返回上一页页码
     */
    protected int fetchPrePageIndex() {
        return (pageIndex - 1 <= 0) ? 1 : pageIndex - 1;
    }

    /**
     * 下一页
     *
     * @return 返回下一页页码
     */
    protected int fetchNextPageIndex() {
        return (pageIndex + 1 > pageCount) ? pageCount : pageIndex + 1;
    }

    /**
     * 首页
     *
     * @return 返回1
     */
    protected int fetchFirstPageIndex() {
        return 1;
    }

    /**
     * 最后一页
     *
     * @return 返回最后一页页码
     */
    protected int fetchLastPageIndex() {
        return pageCount;
    }

    /**
     * 结果集
     *
     * @return 返回结果集
     */
    protected List<E> fetchList() {
        return list;
    }

    /**
     * 总记录数
     *
     * @return 返回总记录数
     */
    protected long fetchTotal() {
        return total;
    }

    /**
     * 当前页索引,等同于getCurrentPageIndex()
     *
     * @return 返回当前页索引
     */
    protected int fetchPageIndex() {
        return pageIndex;
    }

    /**
     * 每页显示几条记录
     *
     * @return 返回每页大小
     */
    protected int fetchPageSize() {
        return pageSize;
    }

    /**
     * 起始页索引，从0开始
     *
     * @return 返回起始页索引
     */
    protected int fetchStart() {
        return start;
    }

    /**
     * 共几页
     *
     * @return 返回总页数
     */
    protected int fetchPageCount() {
        return pageCount;
    }

    @Override
    public void setTotal(long total) {
        this.total = total;
    }

    @Override
    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    @Override
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    @Override
    public void setStart(int start) {
        this.start = start;
    }

    @Override
    public void setList(List<E> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "PageSupport{" +
                "list=" + list +
                ", total=" + total +
                ", start=" + start +
                ", pageIndex=" + pageIndex +
                ", pageSize=" + pageSize +
                ", pageCount=" + pageCount +
                '}';
    }
}
