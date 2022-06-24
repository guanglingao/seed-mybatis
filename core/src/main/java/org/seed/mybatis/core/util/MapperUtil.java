package org.seed.mybatis.core.util;


import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.seed.mybatis.core.PageInfo;
import org.seed.mybatis.core.PageResult;
import org.seed.mybatis.core.exception.QueryException;
import org.seed.mybatis.core.ext.spi.BeanExecutor;
import org.seed.mybatis.core.ext.spi.SpiContext;
import org.seed.mybatis.core.mapper.QueryMapper;
import org.seed.mybatis.core.query.Query;
import org.seed.mybatis.core.query.param.IParam;
import org.seed.mybatis.core.support.PageEasyui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 查询工具
 */
public class MapperUtil {

    private static final Log logger = LogFactory.getLog(MapperUtil.class);

    private MapperUtil() {
    }


    /**
     * 为easyui表格(datagrid)提供的查询
     *
     * @param mapper mapper
     * @param query  查询条件
     * @param clazz  返回VO的类型
     * @param <E>    结果集
     * @param <T>    VO
     * @return 返回查询结果，将此对象转换成json，可被datagrid识别
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <E, T> PageEasyui<T> queryForEasyuiDatagrid(QueryMapper<E, ?> mapper, Query query, Class<T> clazz) {
        PageEasyui pageInfo = queryForEasyuiDatagrid(mapper, query);
        List list = pageInfo.getRows();
        if (list != null) {
            List<Object> newList = new ArrayList<>(list.size());
            BeanExecutor beanExecutor = SpiContext.getBeanExecutor();
            try {
                for (Object element : list) {
                    if (clazz == element.getClass()) {
                        newList.add(element);
                    } else {
                        T t = clazz.newInstance();
                        beanExecutor.copyProperties(element, t);
                        newList.add(t);
                    }
                }
                pageInfo.setList(newList);
            } catch (Exception e) {
                throw new QueryException(e);
            }
        } else {
            pageInfo.setList(Collections.<T>emptyList());
        }
        return (PageEasyui<T>) pageInfo;
    }

    /**
     * 查询并转换结果集
     *
     * @param mapper mapper
     * @param query  查询条件
     * @param clazz  返回VO的类型
     * @param <E>    结果集
     * @param <T>    VO
     * @return 返回查询结果
     * @see #queryAndConvert(QueryMapper, Query, Supplier)
     * @deprecated 使用com.gitee.fastmybatis.core.util.MapperUtil#queryAndConvert(com.gitee.fastmybatis.core.mapper.SchMapper, com.gitee.fastmybatis.core.query.Query, java.util.function.Supplier)
     */
    @Deprecated
    public static <E, T> PageInfo<T> queryAndConvert(QueryMapper<E, ?> mapper, Query query, Class<T> clazz) {
        return queryAndConvert(mapper, query, () -> {
            try {
                return clazz.newInstance();
            } catch (Exception e) {
                logger.error("生成对象错误，class=" + clazz, e);
                return null;
            }
        });
    }

    /**
     * 查询并转换结果集
     *
     * @param mapper   mapper
     * @param query    查询条件
     * @param supplier 返回实例对象
     * @param <E>      结果集
     * @param <T>      实例对象
     * @return 返回查询结果
     */
    public static <E, T> PageInfo<T> queryAndConvert(QueryMapper<E, ?> mapper, Query query, Supplier<T> supplier) {
        PageInfo pageInfo = query(mapper, query);
        List list = pageInfo.getData();
        if (list != null) {
            List<Object> newList = new ArrayList<>(list.size());
            BeanExecutor beanExecutor = SpiContext.getBeanExecutor();
            try {
                for (Object element : list) {
                    T t = supplier.get();
                    beanExecutor.copyProperties(element, t);
                    newList.add(t);
                }
                pageInfo.setList(newList);
            } catch (Exception e) {
                throw new QueryException(e);
            }
        } else {
            pageInfo.setList(Collections.<T>emptyList());
        }
        return pageInfo;
    }

    /**
     * 为easyui表格(datagrid)提供的查询
     *
     * @param mapper mapper
     * @param query  查询对象
     * @param <E>    结果集对象
     * @return 返回查询结果，将此对象转换成json，可被datagrid识别
     */
    @SuppressWarnings("unchecked")
    public static <E> PageEasyui<E> queryForEasyuiDatagrid(QueryMapper<E, ?> mapper, Query query) {
        return query(mapper, query, PageEasyui.class);
    }

    /**
     * 分页数算法:页数 = (总记录数 + 每页记录数 - 1) / 每页记录数
     *
     * @param total    总记录数
     * @param pageSize 每页记录数
     * @return 返回页数
     */
    public static int calcPageCount(long total, int pageSize) {
        return (int) (pageSize == 0 ? 1 : (total + pageSize - 1) / pageSize);
    }

    /**
     * 分页查询
     *
     * @param <Entity> 实体类
     * @param mapper   查询mapper
     * @param bean     查询bean
     * @return 返回PageInfo
     */
    @SuppressWarnings("unchecked")
    public static <Entity> PageInfo<Entity> query(QueryMapper<Entity, ?> mapper, Object bean) {
        return query(mapper, Query.build(bean), PageInfo.class);
    }

    /**
     * 分页查询
     *
     * @param <Entity>    实体类
     * @param mapper      查询mapper
     * @param searchParam 查询pojo
     * @return 返回PageInfo，里面存放结果以及分页信息
     */
    @SuppressWarnings("unchecked")
    public static <Entity> PageInfo<Entity> query(QueryMapper<Entity, ?> mapper, IParam searchParam) {
        return query(mapper, Query.build(searchParam), PageInfo.class);
    }

    /**
     * 分页查询
     *
     * @param <Entity> 实体类
     * @param mapper   查询mapper
     * @param query    查询条件
     * @return 返回PageInfo，里面存放结果以及分页信息
     */
    @SuppressWarnings("unchecked")
    public static <Entity> PageInfo<Entity> query(QueryMapper<Entity, ?> mapper, Query query) {
        return query(mapper, query, PageInfo.class);
    }


    /**
     * 分页查询
     *
     * @param <Entity>        实体类
     * @param <T>             返回结果类
     * @param mapper          查询mapper
     * @param query           查询条件
     * @param pageResultClass 结果类
     * @return 返回结果类
     */
    public static <Entity, T extends PageResult<Entity>> T query(QueryMapper<Entity, ?> mapper, Query query,
                                                                 Class<T> pageResultClass) {
        T result = null;
        try {
            result = pageResultClass.newInstance();
        } catch (Exception e) {
            throw new QueryException(e);
        }

        try {
            // 总页数
            int pageCount = 0;
            // 总条数
            long total = 0;
            // 结果集
            List<Entity> list = Collections.emptyList();

            // 如果是查询全部则直接返回结果集条数
            // 如果是分页查询则还需要带入条件执行一下sql
            if (query.getIsQueryAll()) {
                list = mapper.list(query);
                total = list.size();
                if (total > 0) {
                    pageCount = 1;
                }
            } else {
                if (query.getIsSetTotal()) {
                    //如果设置了total总记录数，直接获取该total
                    total = query.getTotal();
                } else {
                    //如果没有设置total，先去count执行一下sql
                    total = mapper.getCount(query);
                }
                // 如果有数据
                if (total > 0) {
                    list = mapper.list(query);

                    int start = query.getStart();
                    // 每页记录数
                    int pageSize = query.getLimit();
                    // 当前第几页
                    int pageIndex = (start / pageSize) + 1;

                    result.setStart(start);
                    result.setPageIndex(pageIndex);
                    result.setPageSize(pageSize);

                    pageCount = calcPageCount(total, pageSize);
                }
            }

            result.setList(list);
            result.setTotal(total);
            result.setPageCount(pageCount);
        } catch (Exception e) {
            throw new QueryException(e);
        }

        return result;
    }


    /**
     * 分页查询,指定列名
     *
     * @param columns 数据库列名
     * @param mapper  查询mapper
     * @param query   查询条件
     * @return 返回PageInfo
     */
    public static <T> PageInfo<T> query(List<String> columns, QueryMapper<?, ?> mapper, Query query, Class<T> clazz) {
        if (columns == null || columns.isEmpty()) {
            throw new IllegalArgumentException("parameter 'columns' can not empty");
        }
        Objects.requireNonNull(query, "parameter 'query' can not null");
        Objects.requireNonNull(clazz, "parameter 'clazz' can not null");
        PageInfo<T> result = new PageInfo<>();
        try {
            // 总页数
            int pageCount = 0;
            // 总条数
            long total = 0;
            // 结果集
            List<T> list = Collections.emptyList();

            // 如果是查询全部则直接返回结果集条数
            // 如果是分页查询则还需要带入条件执行一下sql
            if (query.getIsQueryAll()) {
                list = mapper.listBySpecifiedColumns(columns, query, clazz);
                total = list.size();
                if (total > 0) {
                    pageCount = 1;
                }
            } else {
                if (query.getIsSetTotal()) {
                    //如果设置了total总记录数，直接获取该total
                    total = query.getTotal();
                } else {
                    //如果没有设置total，先去count执行一下sql
                    total = mapper.getCount(query);
                }
                // 如果有数据
                if (total > 0) {
                    list = mapper.listBySpecifiedColumns(columns, query, clazz);
                    int start = query.getStart();
                    // 每页记录数
                    int pageSize = query.getLimit();
                    // 当前第几页
                    int pageIndex = (start / pageSize) + 1;

                    result.setStart(start);
                    result.setPageIndex(pageIndex);
                    result.setPageSize(pageSize);

                    pageCount = calcPageCount(total, pageSize);
                }
            }

            result.setList(list);
            result.setTotal(total);
            result.setPageCount(pageCount);
        } catch (Exception e) {
            throw new QueryException(e);
        }

        return result;
    }

    /**
     * 自定义分页查询
     * <pre>
     * {@literal
     * PageInfo<UserInfoDTO> pageInfo = MapperUtil.query(query, mapper::getUserInfoTotal, mapper::getUserInfoList);
     * }
     * </pre>
     *
     * @param query       查询条件
     * @param totalGetter 获取总数
     * @param listGetter  获取结果集
     * @param <T>         结果集类型
     * @return 返回分页信息
     */
    public static <T> PageInfo<T> query(Query query, Function<Query, Long> totalGetter, Function<Query, List<T>> listGetter) {
        return query(query, totalGetter, listGetter, PageInfo::new);
    }

    /**
     * 自定义分页查询
     *
     * @param query       查询条件
     * @param totalGetter 获取总数
     * @param listGetter  获取结果集
     * @param <T>         结果集类型
     * @param <R>         分页结果类型
     * @return 返回分页信息
     */
    public static <T, R extends PageResult<T>> R query(Query query, Function<Query, Long> totalGetter, Function<Query, List<T>> listGetter, Supplier<R> pageInfoGetter) {
        R result = pageInfoGetter.get();
        try {
            // 总页数
            int pageCount = 0;
            // 总条数
            long total = 0;
            // 结果集
            List<T> list = Collections.emptyList();

            // 如果是查询全部则直接返回结果集条数
            // 如果是分页查询则还需要带入条件执行一下sql
            if (query.getIsQueryAll()) {
                list = listGetter.apply(query);
                total = list.size();
                if (total > 0) {
                    pageCount = 1;
                }
            } else {
                if (query.getIsSetTotal()) {
                    //如果设置了total总记录数，直接获取该total
                    total = query.getTotal();
                } else {
                    //如果没有设置total，先去count执行一下sql
                    total = totalGetter.apply(query);
                }
                // 如果有数据
                if (total > 0) {
                    list = listGetter.apply(query);

                    int start = query.getStart();
                    // 每页记录数
                    int pageSize = query.getLimit();
                    // 当前第几页
                    int pageIndex = (start / pageSize) + 1;

                    result.setStart(start);
                    result.setPageIndex(pageIndex);
                    result.setPageSize(pageSize);

                    pageCount = MapperUtil.calcPageCount(total, pageSize);
                }
            }

            result.setList(list);
            result.setTotal(total);
            result.setPageCount(pageCount);
        } catch (Exception e) {
            throw new QueryException(e);
        }
        return result;
    }

}
