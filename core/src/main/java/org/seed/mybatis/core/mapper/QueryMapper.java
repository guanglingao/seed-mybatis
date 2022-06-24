package org.seed.mybatis.core.mapper;


import org.apache.ibatis.annotations.Param;
import org.seed.mybatis.core.SeedMybatisContext;
import org.seed.mybatis.core.PageInfo;
import org.seed.mybatis.core.ext.spi.BeanExecutor;
import org.seed.mybatis.core.ext.spi.SpiContext;
import org.seed.mybatis.core.query.Query;
import org.seed.mybatis.core.support.PageEasyui;
import org.seed.mybatis.core.util.ClassUtil;
import org.seed.mybatis.core.util.MapperUtil;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 具备查询功能的Mapper
 *
 * @param <E> 实体类，如：Student
 * @param <I> 主键类型，如：Long，Integer
 */
public interface QueryMapper<E, I> extends Mapper<E> {

    /**
     * 内置resultMap名称
     */
    String BASE_RESULT_MAP = "baseResultMap";

    /**
     * 根据主键查询<br>
     * <pre>
     * {@literal
     * TUser user = mapper.getById(3);
     * }
     * 对应SQL:
     * SELECT col1, col2, ...
     * FROM `t_user` t
     * WHERE id = 3
     * </pre>
     *
     * @param id 主键值
     * @return 返回实体对象，没有返回null
     */
    E getById(I id);

    /**
     * 根据主键查询强制查询，忽略逻辑删除字段<br>
     * <pre>
     * {@literal
     * TUser user = mapper.getById(3);
     * }
     * 对应SQL:
     * SELECT col1, col2, ...
     * FROM `t_user` t
     * WHERE id = 3
     * </pre>
     *
     * @param id 主键值
     * @return 返回实体对象，没有返回null
     */
    E forceById(I id);

    /**
     * 根据条件查找单条记录<br>
     * <pre>
     * {@literal
     * // 查询id=3,金额大于1的用户
     * Query query = new Query()
     *         .eq("id", 3)
     *         .gt("money", 1);
     * TUser user = mapper.getByQuery(query);
     * }
     * 对应SQL:
     * SELECT col1, col2, ...
     * FROM `t_user` t
     * WHERE id = ? AND money > ? LIMIT 1
     * </pre>
     *
     * @param query 查询条件
     * @return 返回实体对象，没有返回null
     */
    E getByQuery(@Param("query") Query query);

    /**
     * 查询单条数据并返回指定字段<br>
     * <pre>
     * {@literal
     * Query query = new Query().eq("id", 6);
     * TUser tUser = mapper.getBySpecifiedColumns(Arrays.asList("id", "username"), query);
     * }
     * 对应SQL:
     * SELECT id , username FROM `t_user` t WHERE id = 6 AND LIMIT 0,1
     * </pre>
     *
     * @param columns 指定返回的数据字段
     * @param query   查询条件
     * @return 返回某一条数据
     */
    default E getBySpecifiedColumns(List<String> columns, Query query) {
        if (columns == null || columns.isEmpty()) {
            throw new IllegalArgumentException("parameter 'columns' can not empty");
        }
        query.limit(0, 1);
        List<E> list = this.listBySpecifiedColumns(columns, query);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 查询单条数据返回指定字段并转换到指定类中<br>
     * <pre>
     * {@literal
     * Query query = new Query().eq("id", 6);
     * UserVO userVo = mapper.getBySpecifiedColumns(Arrays.asList("id", "username"), query, UserVO.class);
     * }
     * 对应SQL:
     * SELECT id , username FROM `t_user` t WHERE id = 6 AND LIMIT 0,1
     * </pre>
     *
     * @param columns 指定返回的数据字段
     * @param query   查询条件
     * @param clazz   待转换的类，类中的字段类型必须跟实体类的中类型一致
     * @param <T>     转换类类型
     * @return 返回转换类，查不到返回null
     */
    default <T> T getBySpecifiedColumns(List<String> columns, Query query, Class<T> clazz) {
        Objects.requireNonNull(clazz, "parameter 'clazz' can not null");
        E e = getBySpecifiedColumns(columns, query);
        // 如果是单值
        if (ClassUtil.isPrimitive(clazz.getSimpleName())) {
            return SpiContext.getBeanExecutor().pojoToValue(e, clazz, columns.get(0));
        }
        return SpiContext.getBeanExecutor().copyBean(e, clazz);
    }

    /**
     * 查询某一行某个字段值<br>
     * <pre>
     * {@literal
     * Query query = new Query().eq("id", 6);
     * String username = mapper.getColumnValue("username", query, String.class);
     * }
     * 转换成SQL：
     * SELECT username FROM `t_user` t WHERE id = 6 LIMIT 0,1
     * </pre>
     *
     * @param column 数据库字段
     * @param query  查询条件
     * @param clazz  待转换的类，类中的字段类型必须跟实体类的中类型一致
     * @param <T>    转换类类型
     * @return 返回单值，查不到返回null
     */
    default <T> T getColumnValue(String column, Query query, Class<T> clazz) {
        if (column == null || "".equals(column)) {
            throw new IllegalArgumentException("parameter 'column' can not blank");
        }
        Objects.requireNonNull(clazz, "parameter 'clazz' can not null");
        if (!ClassUtil.isPrimitive(clazz.getSimpleName())) {
            throw new IllegalArgumentException("parameter `clazz` must be a single value class, such as: Integer.class, String.class, Date.class");
        }
        return getBySpecifiedColumns(Collections.singletonList(column), query, clazz);
    }

    /**
     * 根据字段查询一条记录<br>
     * <pre>
     * {@literal
     * TUser user = mapper.getByColumn("username", "王五");
     * }
     * </pre>
     * <code>
     * SELECT col1,col2,... FROM table WHERE {column} = {value} LIMIT 1
     * </code>
     *
     * @param column 数据库字段名
     * @param value  字段值
     * @return 返回实体对象，没有返回null
     */
    E getByColumn(@Param("column") String column, @Param("value") Object value);

    /**
     * 查询总记录数<br>
     * <pre>
     * {@literal
     * Query query = new Query();
     * // 添加查询条件
     * query.eq("state", 0);
     * // 获取总数
     * long total = mapper.getCount(query);
     *
     * 对应SQL:
     * SELECT COUNT(*) FROM t_user WHERE `state` = 0
     * }
     * </pre>
     *
     * @param query 查询条件
     * @return 返回总记录数
     */
    long getCount(@Param("query") Query query);

    /**
     * 根据字段查询结果集<br>
     *
     * <pre>
     * {@literal
     * List<TUser> list = mapper.listByColumn("age", 20);
     * }
     * </pre>
     * 对应SQL:
     * <code>
     * SELECT col1, col2, ... FROM t_user WHERE age = 20;
     * </code>
     *
     * @param column 数据库字段名
     * @param value  字段值
     * @return 返回实体对象集合，没有返回空集合
     */
    List<E> listByColumn(@Param("column") String column, @Param("value") Object value);

    /**
     * 查询结果集<br>
     * <pre>
     * {@literal
     * Query query = new Query()
     *         .eq("state", 0)
     *         .in("money", Arrays.asList(100, 1.0, 3));
     * List<TUser> list = mapper.list(query);
     * }
     * 对应SQL:
     * SELECT col1, col2, ...
     * FROM `t_user` t
     * WHERE state = ? AND money IN ( ? , ? , ? )
     * </pre>
     *
     * @param query 查询条件
     * @return 返回实体对象集合，没有返回空集合
     */
    List<E> list(@Param("query") Query query);

    /**
     * 根据多个主键查询<br>
     *
     * <code>
     * SELECT col1, col2, ... FROM table WHERE id in (val1, val2, ...)
     * </code>
     *
     * @param ids id集合
     * @return 返回结果集，没有返回空list
     */
    default List<E> listByIds(Collection<I> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("parameter 'ids' can not empty");
        }
        Class<E> entityClass = (Class<E>) ClassUtil.getSuperInterfaceGenericType(getClass(), 0);
        String pkColumnName = SeedMybatisContext.getPkColumnName(entityClass);
        return listByCollection(pkColumnName, ids);
    }

    /**
     * 根据多个字段值查询结果集<br>
     *
     * <code>
     * SELECT col1, col2, ... FROM table WHERE {column} IN (val1, val2, ...)
     * </code>
     *
     * @param column 数据库字段名
     * @param values 多个字段值
     * @return 返回实体对象集合，没有返回空集合
     */
    default List<E> listByArray(String column, Object[] values) {
        if (column == null || "".equals(column)) {
            throw new IllegalArgumentException("parameter 'column' can not blank");
        }
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("parameter 'values' can not empty");
        }
        return listByCollection(column, Arrays.asList(values));
    }

    /**
     * 根据字段多个值查询结果集<br>
     *
     * <code>
     * SELECT col1, col2, ... FROM table WHERE {column} in (val1, val2, ...)
     * </code>
     *
     * @param column 数据库字段名
     * @param values 多个字段值
     * @return 返回实体对象集合，没有返回空集合
     */
    default List<E> listByCollection(String column, Collection<?> values) {
        if (column == null || "".equals(column)) {
            throw new IllegalArgumentException("parameter 'column' can not blank");
        }
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("parameter 'values' can not empty");
        }
        Query query = new Query()
                .in(column, values);
        return list(query);
    }

    /**
     * 查询返回指定的列，返回实体类集合<br>
     * <pre>
     * {@literal
     * Query query = new Query();
     * // 添加查询条件
     * query.eq("username", "张三");
     *
     * // 自定义字段
     * List<String> columns = Arrays.asList("id", "username");
     * // 查询，返回一个Map集合
     * List<TUser> list = mapper.listBySpecifiedColumns(columns, query);
     * }
     * 对应SQL:SELECT id , username FROM `t_user` t WHERE username = ?
     * </pre>
     *
     * @param columns 指定字段，数据库字段名
     * @param query   查询条件
     * @return 返回实体类集合，没有则返回空list
     */
    List<E> listBySpecifiedColumns(@Param("columns") List<String> columns, @Param("query") Query query);

    /**
     * 查询返回指定的列，返指定类集合<br>
     * <pre>
     * {@literal
     * Query query = new Query();
     * // 添加查询条件
     * query.eq("username", "张三");
     *
     * // 自定义字段
     * List<String> columns = Arrays.asList("t.id", "t.username", "t.add_time");
     * // 查询，自定义集合
     * List<UserVO> list = mapper.listBySpecifiedColumns(columns, query, UserVO.class);
     * }
     * </pre>
     *
     * @param columns 指定字段，数据库字段名
     * @param query   查询条件
     * @param clazz   集合元素类型，可以是对象class，也可以是基本类型class，如：UserVO.class, Integer.class, String.class。<br>
     *                当指定基本类型class时，<code>columns</code>参数只能指定一列。当为对象类型时，类中的字段类型必须跟实体类的中类型一致
     * @return 返回实体类集合，没有则返回空list
     * @see #pageBySpecifiedColumns(List, Query, Class) 分页查询
     */
    default <T> List<T> listBySpecifiedColumns(List<String> columns, Query query, Class<T> clazz) {
        if (columns == null || columns.isEmpty()) {
            throw new IllegalArgumentException("parameter 'columns' can not empty");
        }
        Objects.requireNonNull(query, "parameter 'query' can not null");
        Objects.requireNonNull(clazz, "parameter 'clazz' can not null");
        List<E> list = this.listBySpecifiedColumns(columns, query);
        if (list == null) {
            return new ArrayList<>(0);
        }
        BeanExecutor beanExecutor = SpiContext.getBeanExecutor();
        // 如果是单值
        if (ClassUtil.isPrimitive(clazz.getSimpleName())) {
            return list.stream()
                    .map(obj -> beanExecutor.pojoToValue(obj, clazz, columns.get(0)))
                    .collect(Collectors.toList());
        }
        return beanExecutor.copyBean(list, clazz);
    }

    /**
     * 查询指定列，返指定列集合<br>
     * <pre>
     * {@literal
     * // 返回id集合
     * List<Integer> idList = mapper.listColumnValues("id", query, Integer.class);
     * }
     * </pre>
     *
     * @param column 指定列名，数据库字段名
     * @param query  查询条件
     * @param clazz  集合元素类型，基本类型class，如：Integer.class, String.class
     * @return 返回指定列集合，没有则返回空list
     * @see #pageBySpecifiedColumns(List, Query, Class) 分页查询
     */
    default <T> List<T> listColumnValues(String column, Query query, Class<T> clazz) {
        if (column == null || "".equals(column)) {
            throw new IllegalArgumentException("parameter 'column' can not blank");
        }
        Objects.requireNonNull(clazz, "parameter 'clazz' can not null");
        if (!ClassUtil.isPrimitive(clazz.getSimpleName())) {
            throw new IllegalArgumentException("param `clazz` must be a single value class, such as: Integer.class, String.class, Date.class");
        }
        return this.listBySpecifiedColumns(Collections.singletonList(column), query, clazz);
    }

    /**
     * 查询返回指定的列，返回分页数据<br>
     * <pre>
     * {@literal
     * Query query = new Query()
     *         .eq("state", 0)
     *         .page(1, 6);
     * PageInfo<MyUser> pageInfo = mapper.pageBySpecifiedColumns(Arrays.asList("id", "username"), query, MyUser.class);
     * }
     * </pre>
     *
     * @param columns 数据库列名
     * @param query   查询条件
     * @param clazz   元素class，类中的字段类型必须跟实体类的中类型一致
     * @param <T>     元素类
     * @return 返回分页信息
     */
    default <T> PageInfo<T> pageBySpecifiedColumns(List<String> columns, Query query, Class<T> clazz) {
        return MapperUtil.query(columns, this, query, clazz);
    }

    /**
     * 分页查询<br>
     * <pre>
     * {@literal
     * Query query = new Query();
     * // 添加查询条件
     * query.eq("username", "张三")
     *         .page(1, 2) // 分页查询，按页码分，通常使用这种。
     * ;
     *
     * // 分页信息
     * PageInfo<TUser> pageInfo = mapper.page(query);
     *
     * List<TUser> list = pageInfo.getList(); // 结果集
     * long total = pageInfo.getTotal(); // 总记录数
     * int pageCount = pageInfo.getPageCount(); // 共几页
     * }
     * </pre>
     *
     * @param query 查询条件
     * @return 返回分页信息
     */
    default PageInfo<E> page(Query query) {
        return MapperUtil.query(this, query);
    }

    /**
     * 查询结果集，并转换结果集中的记录
     *
     * @param query 查询条件
     * @param clazz 结果集转换成指定的class类（通过属性拷贝），类中的字段类型必须跟实体类的中类型一致
     * @return 返回分页信息
     * @see #page(Query, Supplier)
     */
    default <T> PageInfo<T> page(Query query, Class<T> clazz) {
        return MapperUtil.queryAndConvert(this, query, clazz);
    }

    /**
     * 查询结果集，并转换结果集中的记录<br>
     * <code>
     * {@literal PageInfo<UserVO> pageInfo = mapper.page(query, UserVO::new);}
     * </code>
     *
     * @param query  查询条件
     * @param target 转换类
     * @return 返回分页信息
     */
    default <T> PageInfo<T> page(Query query, Supplier<T> target) {
        return MapperUtil.queryAndConvert(this, query, target);
    }

    /**
     * 查询结果集，并转换结果集中的记录，转换处理每一行<br>
     * <pre>
     * {@literal
     *  PageInfo<TUser> pageInfo = mapper.page(query, tUser -> {
     *      // 对每行数据进行转换
     *      String username = tUser.getUsername();
     *      if ("张三".equals(username)) {
     *          tUser.setUsername("法外狂徒");
     *      }
     *      return tUser;
     *   });
     * }
     * 或者：
     * {@literal
     *  // 对结果集进行手动转换，如果仅仅是属性拷贝可以直接：mapper.page(query, UserVO::new);
     *  PageInfo<UserVO> page = mapper.page(query, user -> {
     *      UserVO userVO = new UserVO();
     *      BeanUtils.copyProperties(user, userVO);
     *      return userVO;
     *   });
     * }
     * </pre>
     *
     * @param query     查询条件
     * @param converter 转换类
     * @return 返回分页信息
     */
    default <R> PageInfo<R> page(Query query, Function<E, R> converter) {
        PageInfo pageInfo = this.page(query);
        List<E> list = (List<E>) pageInfo.getData();
        List<R> retList = list.stream()
                .map(converter)
                .collect(Collectors.toList());
        pageInfo.setList(retList);
        return (PageInfo<R>) pageInfo;
    }

    /**
     * 查询结果集，并转换结果集中的记录，转换处理list<br>
     * <pre>
     * {@literal
     * Query query = new Query()
     *         .eq("state", 0);
     * PageInfo<UserVO> pageInfo = mapper.pageAndConvert(query, list -> {
     *     List<UserVO> retList = new ArrayList<>(list.size());
     *     for (TUser tUser : list) {
     *         UserVO userVO = new UserVO();
     *         BeanUtils.copyProperties(tUser, userVO);
     *         retList.add(userVO);
     *     }
     *     return retList;
     * });
     * }
     * </pre>
     *
     * @param query     查询条件
     * @param converter 转换类
     * @return 返回分页信息
     * @since 1.10.11
     */
    default <R> PageInfo<R> pageAndConvert(Query query, Function<List<E>, List<R>> converter) {
        PageInfo pageInfo = this.page(query);
        List<E> list = (List<E>) pageInfo.getData();
        List<R> retList = converter.apply(list);
        pageInfo.setList(retList);
        return (PageInfo<R>) pageInfo;
    }

    /**
     * 查询结果集，并转换结果集中的记录，并对记录进行额外处理<br>
     * <pre>
     * {@literal
     *  PageInfo<UserVO> page = mapper.page(query, UserVO::new, userVO -> {
     *      System.out.println(userVO.getUsername());
     *  });
     * }
     * </pre>
     *
     * @param query  查询条件
     * @param target 转换后的类
     * @param format 对转换后的类格式化，此时的对象已经完成属性拷贝
     * @param <R>    结果集类型
     * @return 返回PageInfo对象
     */
    default <R> PageInfo<R> page(Query query, Supplier<R> target, Consumer<R> format) {
        return this.page(query, t -> {
            R r = target.get();
            SpiContext.getBeanExecutor().copyProperties(t, r);
            format.accept(r);
            return r;
        });
    }

    /**
     * 查询返回easyui结果集<br>
     * 如果前端使用easyui，此返回结果可适用于easyui的datagrid组件
     *
     * @param query 查询条件
     * @return 返回easyui分页信息
     */
    default PageEasyui<E> pageEasyui(Query query) {
        return MapperUtil.queryForEasyuiDatagrid(this, query);
    }

    /**
     * 查询返回easyui结果集，并转换结果集中的记录<br>
     * 如果前端使用easyui，此返回结果可适用于easyui的datagrid组件
     *
     * @param query 查询条件
     * @param clazz 结果集转换成指定的class类（通过属性拷贝）
     * @return 返回easyui分页信息
     */
    default <T> PageEasyui<T> pageEasyui(Query query, Class<T> clazz) {
        return MapperUtil.queryForEasyuiDatagrid(this, query, clazz);
    }
}
