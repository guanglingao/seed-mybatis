package org.seed.mybatis.core.support;



import org.seed.mybatis.core.SeedMybatisContext;
import org.seed.mybatis.core.PageInfo;
import org.seed.mybatis.core.ext.MapperRunner;
import org.seed.mybatis.core.mapper.CrudMapper;
import org.seed.mybatis.core.query.Query;
import org.seed.mybatis.core.util.ClassUtil;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 通用service接口<br>
 * 使用方式：
 * <pre>
 * <code>
 * {@literal
 * @Service
 * public class UserService implements CommonService<TUser, Integer, TUserMapper> {
 *
 * }
 * }
 * </code>
 * </pre>
 *
 * @param <E> 实体类，如：Student
 * @param <I> 主键类型，如：Long，Integer
 * @param <Mapper> Mapper类，如：TUserMapper
 *
 */
public interface CommonService<E, I, Mapper extends CrudMapper<E, I>> {

    default Mapper getMapper() {
        return getMapperRunner().getMapper();
    }

    default MapperRunner<Mapper> getMapperRunner() {
        Class<E> entityClass = (Class<E>) ClassUtil.getSuperInterfaceGenericType(getClass(), 0);
        return SeedMybatisContext.getCrudMapperRunner(entityClass);
    }


    /**
     * 保存或修改，当数据库存在记录执行UPDATE，否则执行INSERT
     *
     * @param entity 实体类
     * @return 受影响行数
     */
    default int saveOrUpdate(E entity) {
        return getMapperRunner().run(mapper -> mapper.saveOrUpdateWithNull(entity));
    }

    /**
     * 保存或修改，忽略null字段，当数据库存在记录执行UPDATE，否则执行INSERT
     *
     * @param entity 实体类
     * @return 受影响行数
     */
    default int saveOrUpdateIgnoreNull(E entity) {
        return getMapperRunner().run(mapper -> mapper.saveOrUpdate(entity));
    }

    /**
     * 删除记录（底层根据id删除），在有逻辑删除字段的情况下，做UPDATE操作。
     *
     * @param entity 实体类
     * @return 受影响行数
     */
    default int delete(E entity) {
        Objects.requireNonNull(entity);
        return getMapperRunner().run(mapper -> mapper.delete(entity));
    }

    /**
     * 根据id删除，在有逻辑删除字段的情况下，做UPDATE操作
     *
     * @param id 主键id值
     * @return 受影响行数
     */
    default int deleteById(I id) {
        Objects.requireNonNull(id);
        return getMapperRunner().run(mapper -> mapper.deleteById(id));
    }

    /**
     * 根据多个主键id删除，在有逻辑删除字段的情况下，做UPDATE操作
     *
     * @param ids 主键id
     * @return 返回影响行数
     */
    default int deleteByIds(Collection<I> ids) {
        return getMapperRunner().run(mapper -> mapper.deleteByIds(ids));
    }

    /**
     * 根据指定字段值删除，在有逻辑删除字段的情况下，做UPDATE操作<br>
     * <pre>
     * 根据数组删除
     * {@literal mapper.deleteByColumn("username", Arrays.asList("jim", "tom")); }
     * 对应SQL:DELETE FROM table WHERE username in ('jim', 'tom')
     *
     * 根据某个值删除
     * {@literal mapper.deleteByColumn("username", "jim")); }
     * 对应SQL:DELETE FROM table WHERE username = 'jim'
     * </pre>
     *
     * @param column 数据库字段名
     * @param value  条件值，可以是单值String，int，也可以是集合List，Collection
     * @return 返回影响行数
     */
    default int deleteByColumn(String column, Object value) {
        return getMapperRunner().run(mapper -> mapper.deleteByColumn(column, value));
    }

    /**
     * 根据条件删除，在有逻辑删除字段的情况下，做UPDATE操作<br>
     * <pre>
     * {@literal
     * Query query = new Query());
     * query.eq("state", 3));
     * int i = mapper.deleteByQuery(query));
     * }
     * 对应SQL:
     * DELETE FROM `t_user` WHERE state = 3
     * </pre>
     *
     * @param query 查询对象
     * @return 受影响行数
     */
    default int deleteByQuery(Query query) {
        return getMapperRunner().run(mapper -> mapper.deleteByQuery(query));
    }

    /**
     * 强制删除（底层根据id删除），忽略逻辑删除字段，执行DELETE语句
     *
     * @param entity 实体类
     * @return 受影响行数
     */
    default int forceDelete(E entity) {
        Objects.requireNonNull(entity);
        return getMapperRunner().run(mapper -> mapper.forceDelete(entity));
    }

    /**
     * 根据id强制删除，忽略逻辑删除字段，执行DELETE语句
     *
     * @param id 主键id值
     * @return 受影响行数
     */
    default int forceDeleteById(I id) {
        Objects.requireNonNull(id);
        return getMapperRunner().run(mapper -> mapper.forceDeleteById(id));
    }

    /**
     * 根据条件强制删除，忽略逻辑删除字段，执行DELETE语句
     *
     * @param query 查询对象
     * @return 受影响行数
     */
    default int forceDeleteByQuery(Query query) {
        return getMapperRunner().run(mapper -> mapper.forceDeleteByQuery(query));
    }

    /**
     * 保存，保存所有字段
     *
     * @param entity 实体类
     * @return 受影响行数
     */
    default int save(E entity) {
        Objects.requireNonNull(entity);
        return getMapperRunner().run(mapper -> mapper.save(entity));
    }

    /**
     * 保存，忽略null字段
     *
     * @param entity 实体类
     * @return 受影响行数
     */
    default int saveIgnoreNull(E entity) {
        Objects.requireNonNull(entity);
        return getMapperRunner().run(mapper -> mapper.saveIgnoreNull(entity));
    }

    /**
     * 批量保存<br>
     *
     * @param entities 实体类集合
     * @return 受影响行数
     */
    default int saveBatch(Collection<E> entities) {
        if (entities == null || entities.isEmpty()) {
            throw new IllegalArgumentException("parameter 'entities' can not empty");
        }
        return getMapperRunner().run(mapper -> mapper.saveBatch(entities));
    }

    /**
     * 批量保存,兼容更多的数据库版本,忽略重复行.<br>
     * 此方式采用union的方式批量insert.
     *
     * @param entities 实体类集合
     * @return 受影响行数
     */
    default int saveMultiSet(Collection<E> entities) {
        if (entities == null || entities.isEmpty()) {
            throw new IllegalArgumentException("parameter 'entities' can not empty");
        }
        return getMapperRunner().run(mapper -> mapper.saveMultiSet(entities));
    }

    /**
     * 批量保存，去除重复行，通过对象是否相对判断重复数据，实体类需要实现equals方法.<br>
     *
     * @param entities 实体类集合，需要实现equals方法
     * @return 受影响行数
     */
    default int saveUnique(Collection<E> entities) {
        return getMapperRunner().run(mapper -> mapper.saveUnique(entities));
    }

    /**
     * 批量保存，去除重复行，指定比较器判断<br>
     *
     * @param entities 实体类集合，需要实现equals方法
     * @param comparator 对象比较器
     * @return 受影响行数
     */
    default int saveUnique(Collection<E> entities, Comparator<E> comparator) {
        Objects.requireNonNull(comparator);
        return getMapperRunner().run(mapper -> mapper.saveUnique(entities, comparator));
    }

    /**
     * 根据主键查询<br>
     * <pre>
     * {@literal
     * TUser user = mapper.getById(3));
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
    default E getById(I id) {
        Objects.requireNonNull(id);
        return getMapperRunner().run(mapper -> mapper.getById(id));
    }

    /**
     * 根据主键查询强制查询，忽略逻辑删除字段<br>
     * <pre>
     * {@literal
     * TUser user = mapper.getById(3));
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
    default E forceById(I id) {
        Objects.requireNonNull(id);
        return getMapperRunner().run(mapper -> mapper.forceById(id));
    }

    /**
     * 根据条件查找单条记录<br>
     * <pre>
     * {@literal
     * // 查询id=3,金额大于1的用户
     * Query query = new Query()
     *         .eq("id", 3)
     *         .gt("money", 1));
     * TUser user = mapper.getByQuery(query));
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
    default E getByQuery(Query query) {
        return getMapperRunner().run(mapper -> mapper.getByQuery(query));
    }

    /**
     * 查询单条数据并返回指定字段<br>
     * <pre>
     * {@literal
     * Query query = new Query().eq("id", 6));
     * TUser tUser = mapper.getBySpecifiedColumns(Arrays.asList("id", "username"), query));
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
        return getMapperRunner().run(mapper -> mapper.getBySpecifiedColumns(columns, query));
    }

    /**
     * 查询单条数据返回指定字段并转换到指定类中<br>
     * <pre>
     * {@literal
     * Query query = new Query().eq("id", 6));
     * UserVO userVo = mapper.getBySpecifiedColumns(Arrays.asList("id", "username"), query, UserVO.class));
     * }
     * 对应SQL:
     * SELECT id , username FROM `t_user` t WHERE id = 6 AND LIMIT 0,1
     * </pre>
     *
     * @param columns 指定返回的数据字段
     * @param query   查询条件
     * @param clazz   待转换的类
     * @param <T>     转换类类型
     * @return 返回转换类，查不到返回null
     */
    default <T> T getBySpecifiedColumns(List<String> columns, Query query, Class<T> clazz) {
        return getMapperRunner().run(mapper -> mapper.getBySpecifiedColumns(columns, query, clazz));
    }

    /**
     * 查询某一行某个字段值<br>
     * <pre>
     * {@literal
     * Query query = new Query().eq("id", 6));
     * String username = mapper.getColumnValue("username", query, String.class));
     * }
     * 转换成SQL：
     * SELECT username FROM `t_user` t WHERE id = 6 LIMIT 0,1
     * </pre>
     *
     * @param column 数据库字段
     * @param query  查询条件
     * @param clazz  待转换的类
     * @param <T>    转换类类型
     * @return 返回单值，查不到返回null
     */
    default <T> T getColumnValue(String column, Query query, Class<T> clazz) {
        return getMapperRunner().run(mapper -> mapper.getColumnValue(column, query, clazz));
    }

    /**
     * 根据字段查询一条记录<br>
     * <pre>
     * {@literal
     * TUser user = mapper.getByColumn("username", "王五"));
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
    default E getByColumn(String column, Object value) {
        if (column == null || "".equals(column)) {
            throw new IllegalArgumentException("parameter 'column' can not blank");
        }
        Objects.requireNonNull(value, "parameter 'value' can not null");
        return getMapperRunner().run(mapper -> mapper.getByColumn(column, value));
    }

    /**
     * 查询总记录数<br>
     * <pre>
     * Query query = new Query());
     * // 添加查询条件
     * query.eq("state", 0));
     * // 获取总数
     * long total = mapper.getCount(query));
     * </pre>
     *
     * @param query 查询条件
     * @return 返回总记录数
     */
    default long getCount(Query query) {
        return getMapperRunner().run(mapper -> mapper.getCount(query));
    }

    /**
     * 根据字段查询结果集<br>
     *
     * <pre>
     * {@literal
     * List<TUser> list = mapper.listByColumn("age", 20));
     * }
     * 对应SQL:
     * SELECT col1, col2, ... FROM t_user WHERE age = 20;
     * </pre>
     *
     *
     * @param column 数据库字段名
     * @param value  字段值
     * @return 返回实体对象集合，没有返回空集合
     */
    default List<E> listByColumn(String column, Object value) {
        if (column == null || "".equals(column)) {
            throw new IllegalArgumentException("parameter 'column' can not blank");
        }
        Objects.requireNonNull(value, "parameter 'value' can not null");
        return getMapperRunner().run(mapper -> mapper.listByColumn(column, value));
    }

    /**
     * 查询结果集<br>
     * <pre>
     * {@literal
     * Query query = new Query()
     *         .eq("state", 0)
     *         .in("money", Arrays.asList(100, 1.0, 3));
     * List<TUser> list = mapper.list(query));
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
    default List<E> list(Query query) {
        return getMapperRunner().run(mapper -> mapper.list(query));
    }

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
        return getMapperRunner().run(mapper -> mapper.listByIds(ids));
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
        return getMapperRunner().run(mapper -> mapper.listByArray(column, values));
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
        return getMapperRunner().run(mapper -> mapper.listByCollection(column, values));
    }

    /**
     * 查询返回指定的列，返回实体类集合<br>
     * <pre>
     * {@literal
     * Query query = new Query());
     * // 添加查询条件
     * query.eq("username", "张三"));
     *
     * // 自定义字段
     * List<String> columns = Arrays.asList("id", "username"));
     * // 查询，返回一个Map集合
     * List<TUser> list = mapper.listBySpecifiedColumns(columns, query));
     * }
     * 对应SQL:SELECT id , username FROM `t_user` t WHERE username = ?
     * </pre>
     *
     * @param columns 指定字段，数据库字段名
     * @param query   查询条件
     * @return 返回实体类集合，没有则返回空list
     */
    default List<E> listBySpecifiedColumns(List<String> columns, Query query) {
        if (columns == null || columns.isEmpty()) {
            throw new IllegalArgumentException("parameter 'columns' can not empty");
        }
        return getMapperRunner().run(mapper -> mapper.listBySpecifiedColumns(columns, query));
    }

    /**
     * 查询返回指定的列，返指定类集合<br>
     * <pre>
     * {@literal
     * Query query = new Query());
     * // 添加查询条件
     * query.eq("username", "张三"));
     *
     * // 自定义字段
     * List<String> columns = Arrays.asList("t.id", "t.username", "t.add_time"));
     * // 查询，自定义集合
     * List<UserVO> list = mapper.listBySpecifiedColumns(columns, query, UserVO.class));
     * }
     * </pre>
     *
     * @param columns 指定字段，数据库字段名
     * @param query   查询条件
     * @param clazz   集合元素类型，可以是对象class，也可以是基本类型class，如：UserVO.class, Integer.class, String.class。<br>
     *                当指定基本类型class时，<code>columns</code>参数只能指定一列
     * @return 返回实体类集合，没有则返回空list
     * @see #pageBySpecifiedColumns(List, Query, Class) 分页查询
     */
    default <T> List<T> listBySpecifiedColumns(List<String> columns, Query query, Class<T> clazz) {
        return getMapperRunner().run(mapper -> mapper.listBySpecifiedColumns(columns, query, clazz));
    }

    /**
     * 查询指定列，返指定列集合<br>
     * <pre>
     * {@literal
     * // 返回id集合
     * List<Integer> idList = mapper.listColumnValues("id", query, Integer.class));
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
        return getMapperRunner().run(mapper -> mapper.listColumnValues(column, query, clazz));
    }

    /**
     * 查询返回指定的列，返回分页数据<br>
     * <pre>
     * {@literal
     * Query query = new Query()
     *         .eq("state", 0)
     *         .page(1, 6));
     * PageInfo<MyUser> pageInfo = mapper.pageBySpecifiedColumns(Arrays.asList("id", "username"), query, MyUser.class));
     * }
     * </pre>
     *
     * @param columns 数据库列名
     * @param query   查询条件
     * @param clazz   元素class
     * @param <T>     元素类
     * @return 返回分页信息
     */
    default <T> PageInfo<T> pageBySpecifiedColumns(List<String> columns, Query query, Class<T> clazz) {
        return getMapperRunner().run(mapper -> mapper.pageBySpecifiedColumns(columns, query, clazz));
    }

    /**
     * 分页查询<br>
     * <pre>
     * {@literal
     * Query query = new Query());
     * // 添加查询条件
     * query.eq("username", "张三")
     *         .page(1, 2) // 分页查询，按页码分，通常使用这种。
     * ;
     *
     * // 分页信息
     * PageInfo<TUser> pageInfo = mapper.page(query));
     *
     * List<TUser> list = pageInfo.getList()); // 结果集
     * long total = pageInfo.getTotal()); // 总记录数
     * int pageCount = pageInfo.getPageCount()); // 共几页
     * }
     * </pre>
     *
     * @param query 查询条件
     * @return 返回分页信息
     */
    default PageInfo<E> page(Query query) {
        return getMapperRunner().run(mapper -> mapper.page(query));
    }

    /**
     * 查询结果集，并转换结果集中的记录<br>
     * <code>
     * {@literal PageInfo<UserVO> pageInfo = mapper.page(query, UserVO::new));}
     * </code>
     *
     * @param query  查询条件
     * @param target 转换类
     * @return 返回分页信息
     */
    default <T> PageInfo<T> page(Query query, Supplier<T> target) {
        return getMapperRunner().run(mapper -> mapper.page(query, target));
    }

    /**
     * 查询结果集，并转换结果集中的记录，转换处理每一行<br>
     * <pre>
     * {@literal
     * PageInfo<TUser> pageInfo = mapper.page(query, tUser -> {
     *      // 对每行数据进行转换
     *      String username = tUser.getUsername());
     *      if ("张三".equals(username)) {
     *          tUser.setUsername("法外狂徒"));
     *      }
     *      return tUser;
     *   }));
     * }
     * 或者：
     * {@literal
     *  // 对结果集进行手动转换，如果仅仅是属性拷贝可以直接：mapper.page(query, UserVO::new));
     *  PageInfo<UserVO> page = mapper.page(query, user -> {
     *      UserVO userVO = new UserVO());
     *      BeanUtils.copyProperties(user, userVO));
     *      return userVO;
     *   }));
     * }
     * </pre>
     *
     * @param query     查询条件
     * @param converter 转换类
     * @return 返回分页信息
     */
    default <R> PageInfo<R> page(Query query, Function<E, R> converter) {
        return getMapperRunner().run(mapper -> mapper.page(query, converter));
    }

    /**
     * 查询结果集，并转换结果集中的记录，转换处理list<br>
     * <pre>
     * {@literal
     * Query query = new Query()
     *         .eq("state", 0));
     * PageInfo<UserVO> pageInfo = mapper.pageAndConvert(query, list -> {
     *     List<UserVO> retList = new ArrayList<>(list.size());
     *     for (TUser tUser : list) {
     *         UserVO userVO = new UserVO());
     *         BeanUtils.copyProperties(tUser, userVO));
     *         retList.add(userVO));
     *     }
     *     return retList;
     * }));
     * }
     * </pre>
     *
     * @param query     查询条件
     * @param converter 转换类
     * @return 返回分页信息
     * @since 1.10.11
     */
    default <R> PageInfo<R> pageAndConvert(Query query, Function<List<E>, List<R>> converter) {
        return getMapperRunner().run(mapper -> mapper.pageAndConvert(query, converter));
    }

    /**
     * 查询结果集，并转换结果集中的记录，并对记录进行额外处理<br>
     * <pre>
     * {@literal
     *  PageInfo<UserVO> page = mapper.page(query, UserVO::new, userVO -> {
     *      System.out.println(userVO.getUsername());
     *  }));
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
        return getMapperRunner().run(mapper -> mapper.page(query, target, format));
    }

    /**
     * 查询返回easyui结果集<br>
     * 如果前端使用easyui，此返回结果可适用于easyui的datagrid组件
     *
     * @param query 查询条件
     * @return 返回easyui分页信息
     */
    default PageEasyui<E> pageEasyui(Query query) {
        return getMapperRunner().run(mapper -> mapper.pageEasyui(query));
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
        return getMapperRunner().run(mapper -> mapper.pageEasyui(query, clazz));
    }

    /**
     * 更新，更新所有字段
     *
     * @param entity 实体类
     * @return 受影响行数
     */
    default int update(E entity) {
        Objects.requireNonNull(entity);
        return getMapperRunner().run(mapper -> mapper.update(entity));
    }

    /**
     * 更新，忽略null字段
     *
     * @param entity 实体类
     * @return 受影响行数
     */
    default int updateIgnoreNull(E entity) {
        Objects.requireNonNull(entity);
        return getMapperRunner().run(mapper -> mapper.updateIgnoreNull(entity));
    }

    /**
     * 根据条件更新<br>
     * <pre>
     * {@literal
     * Query query = new Query().eq("state", 2));
     * TUser user = new TUser());
     * user.setUsername("李四"));
     * int i = mapper.updateByQuery(user, query));
     * }
     * 对应SQL: UPDATE `t_user` SET `username`=? WHERE state = ?
     * </pre>
     *
     * @param entity 待更新的数据
     * @param query  更新条件
     * @return 受影响行数
     */
    default int updateByQuery(E entity, Query query) {
        Objects.requireNonNull(entity);
        return getMapperRunner().run(mapper -> mapper.updateByQuery(entity, query));
    }

    /**
     * 根据条件更新，map中的数据转化成update语句set部分，key为数据库字段名<br>
     * <pre>
     * {@literal
     * Query query = new Query().eq("id", 1));
     * // key为数据库字段名
     * Map<String, Object> setBlock = new LinkedHashMap<>());
     * setBlock.put("username", "李四2"));
     * setBlock.put("remark", "123"));
     * int i = mapper.updateByMap(setBlock, query));
     * }
     * 对应SQL：
     * UPDATE `t_user` SET username = ? , remark = ? WHERE id = ?
     * </pre>
     *
     * @param setBlock 待更新的数据，key为数据库字段名
     * @param query    更新条件
     * @return 受影响行数
     */
    default int updateByMap(Map<String, Object> setBlock, Query query) {
        if (setBlock == null || setBlock.isEmpty()) {
            throw new IllegalArgumentException("parameter 'setBlock' can not empty");
        }
        return getMapperRunner().run(mapper -> mapper.updateByMap(setBlock, query));
    }
}
