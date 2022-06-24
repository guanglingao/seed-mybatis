package org.seed.mybatis.core.query;


import org.seed.mybatis.core.SqlConsts;
import org.seed.mybatis.core.query.expression.*;
import org.seed.mybatis.core.query.expression.builder.ConditionBuilder;
import org.seed.mybatis.core.query.param.IParam;
import org.seed.mybatis.core.query.param.SchSortableParam;

import java.util.*;

/**
 * 查询类
 *
 * <pre>
 * 查询姓名为张三，并且年龄为22岁的用户：
 * Query query = new Query().eq("username","张三").eq("age",22);
 * List<User> users = mapper.list(query);
 *
 * 查询年龄为10,20,30的用户：
 * Query query = new Query().in("age",Arrays.asList(10,20,30));
 * List<User> users = mapper.list(query);
 *
 * 查询注册日期大于2017-11-11的用户：
 * Date regDate = ...
 * Query query = new Query().gt("reg_date",regDate);
 * List<User> users = mapper.list(query);
 *
 * 查询性别为男的，年龄大于等于20岁的用户，按年龄降序：
 * Query query = new Query().eq("gender",1).ge("age",20).orderby("age",Sort.DESC);
 * List<User> users = mapper.list(query);
 *
 * 分页查询：
 * Query query = new Query().eq("age",10).page(1,10); // 第一页，每页10条数据
 * List<User> users = mapper.list(query);
 *
 * 查询总记录数：
 * Query query = new Query().eq("age",10).page(1,10); // 第一页，每页10条数据
 * long total = mapper.getCount(query); // 该条件下总记录数
 * </pre>
 */
public class Query implements Queryable {

    private static final String DEFAULT_SQL_INJECT_REGEX = "([';])+|(--)+";

    private static final String DEFAULT_ARG_PLACEHOLDER = "?";

    private String argPlaceholder = DEFAULT_ARG_PLACEHOLDER;

    private String sqlInjectRegex = DEFAULT_SQL_INJECT_REGEX;

    private static final Comparator<Expression> COMPARATOR = Comparator.comparing(Expression::index);

    /**
     * 分页起始位置
     */
    private int start;
    /**
     * 分页大小
     */
    private int limit;
    /**
     * 总记录数，默认为-1，表示没有设置总记录数
     */
    private int total = -1;

    /**
     * 排序信息
     */
    private LinkedHashSet<String> orderInfo;

    /**
     * 额外参数，供xml使用
     */
    private Map<String, Object> paramMap;

    /**
     * 强力查询，设置为true，将无视删除字段
     */
    private boolean forceQuery;

    private boolean distinct;

    private boolean forceUpdate;

    /**
     * 连接表达式
     */
    private List<ExpressionJoinFeature> joinExpressions;

    /**
     * 条件表达式
     */
    protected final List<Expression> expressions = new ArrayList<>(4);

    // ------------ 基本条件 ------------

    /**
     * 添加等于条件
     *
     * @param columnName 数据库字段名
     * @param value      值
     * @return 返回Query对象
     */
    public Query eq(String columnName, Object value) {
        this.addExpression(Expressions.eq(columnName, value));
        return this;
    }

    /**
     * 根据表达式添加等于条件
     * <pre>
     *     query.eq(StringUtils.hasText(name), "name", name);
     *     等同于：
     *     if (StringUtils.hasText(name)) {
     *         query.eq("name", name);
     *     }
     * </pre>
     *
     * @param expression 表达式，当为true时添加条件
     * @param columnName 数据库字段名
     * @param value      值
     * @return 返回Query对象
     */
    public Query eq(boolean expression, String columnName, Object value) {
        if (expression) {
            eq(columnName, value);
        }
        return this;
    }

    /**
     * 添加不等于条件
     *
     * @param columnName 数据库字段名
     * @param value      值
     * @return 返回Query对象
     */
    public Query notEq(String columnName, Object value) {
        this.addExpression(Expressions.notEq(columnName, value));
        return this;
    }

    /**
     * 根据表达式添加不等于条件
     *
     * @param expression 表达式，当为true时添加条件
     * @param columnName 数据库字段名
     * @param value      值
     * @return 返回Query对象
     */
    public Query notEq(boolean expression, String columnName, Object value) {
        if (expression) {
            notEq(columnName, value);
        }
        return this;
    }

    /**
     * 添加大于条件,>
     *
     * @param columnName 数据库字段名
     * @param value      值
     * @return 返回Query对象
     */
    public Query gt(String columnName, Object value) {
        this.addExpression(Expressions.gt(columnName, value));
        return this;
    }

    /**
     * 根据表达式添加大于条件,>
     *
     * @param expression 表达式，当为true时添加条件
     * @param columnName 数据库字段名
     * @param value      值
     * @return 返回Query对象
     */
    public Query gt(boolean expression, String columnName, Object value) {
        if (expression) {
            gt(columnName, value);
        }
        return this;
    }

    /**
     * 添加大于等于条件,>=
     *
     * @param columnName 数据库字段名
     * @param value      值
     * @return 返回Query对象
     */
    public Query ge(String columnName, Object value) {
        this.addExpression(Expressions.ge(columnName, value));
        return this;
    }

    /**
     * 根据表达式添加大于等于条件,>=
     *
     * @param expression 表达式，当为true时添加条件
     * @param columnName 数据库字段名
     * @param value      值
     * @return 返回Query对象
     */
    public Query ge(boolean expression, String columnName, Object value) {
        if (expression) {
            ge(columnName, value);
        }
        return this;
    }

    /**
     * 添加小于条件,<
     *
     * @param columnName 数据库字段名
     * @param value      值
     * @return 返回Query对象
     */
    public Query lt(String columnName, Object value) {
        this.addExpression(Expressions.lt(columnName, value));
        return this;
    }

    /**
     * 根据表达式添加小于条件,<
     *
     * @param expression 表达式，当为true时添加条件
     * @param columnName 数据库字段名
     * @param value      值
     * @return 返回Query对象
     */
    public Query lt(boolean expression, String columnName, Object value) {
        if (expression) {
            lt(columnName, value);
        }
        return this;
    }

    /**
     * 小于等于,<=
     *
     * @param columnName 数据库字段名
     * @param value      值
     * @return 返回Query对象
     */
    public Query le(String columnName, Object value) {
        this.addExpression(Expressions.le(columnName, value));
        return this;
    }

    /**
     * 根据表达式小于等于条件,<=
     *
     * @param expression 表达式，当为true时添加条件
     * @param columnName 数据库字段名
     * @param value      值
     * @return 返回Query对象
     */
    public Query le(boolean expression, String columnName, Object value) {
        if (expression) {
            le(columnName, value);
        }
        return this;
    }

    /**
     * 添加两边模糊查询条件，两边模糊匹配，即name like '%value%'
     *
     * @param columnName 数据库字段名
     * @param value      值,不需要加%
     * @return 返回Query对象
     * @see #likeLeftBlur(String, String) 左边模糊匹配
     * @see #likeRightBlur(String, String) 右边模糊匹配
     */
    public Query like(String columnName, String value) {
        this.addExpression(Expressions.like(columnName, value));
        return this;
    }

    /**
     * 根据表达式添加两边模糊查询条件，两边模糊匹配，即name like '%value%'
     *
     * @param expression 表达式，当为true时添加条件
     * @param columnName 数据库字段名
     * @param value      值,不需要加%
     * @return 返回Query对象
     * @see #likeLeftBlur(boolean, String, String) 左模糊
     * @see #likeRightBlur(boolean, String, String) 右模糊
     */
    public Query like(boolean expression, String columnName, String value) {
        if (expression) {
            like(columnName, value);
        }
        return this;
    }

    /**
     * 添加左模糊查询条件，左边模糊匹配，即name like '%value'
     *
     * @param columnName 数据库字段名
     * @param value      值,不需要加%
     * @return 返回Query对象
     */
    public Query likeLeftBlur(String columnName, String value) {
        this.addExpression(Expressions.likeLeft(columnName, value));
        return this;
    }

    /**
     * 根据表达式添加左模糊查询条件，左边模糊匹配，即name like '%value'
     *
     * @param expression 表达式，当为true时添加条件
     * @param columnName 数据库字段名
     * @param value      值,不需要加%
     * @return 返回Query对象
     */
    public Query likeLeftBlur(boolean expression, String columnName, String value) {
        if (expression) {
            likeLeftBlur(columnName, value);
        }
        return this;
    }

    /**
     * 添加右模糊查询条件，右边模糊匹配，即name like 'value%'。mysql推荐用这种
     *
     * @param columnName 数据库字段名
     * @param value      值,不需要加%
     * @return 返回Query对象
     */
    public Query likeRightBlur(String columnName, String value) {
        this.addExpression(Expressions.likeRight(columnName, value));
        return this;
    }

    /**
     * 根据表达式添加右模糊查询条件，右边模糊匹配，即name like 'value%'。mysql推荐用这种
     *
     * @param expression 表达式，当为true时添加条件
     * @param columnName 数据库字段名
     * @param value      值,不需要加%
     * @return 返回Query对象
     */
    public Query likeRightBlur(boolean expression, String columnName, String value) {
        if (expression) {
            likeRightBlur(columnName, value);
        }
        return this;
    }

    /**
     * 添加IN条件
     *
     * @param columnName 数据库字段名
     * @param value      值
     * @return 返回Query对象
     */
    public Query in(String columnName, Collection<?> value) {
        this.addExpression(Expressions.in(columnName, value));
        return this;
    }

    /**
     * 根据表达式添加IN条件
     *
     * @param expression 表达式，当为true时添加条件
     * @param columnName 数据库字段名
     * @param value      值
     * @return 返回Query对象
     */
    public Query in(boolean expression, String columnName, Collection<?> value) {
        if (expression) {
            in(columnName, value);
        }
        return this;
    }

    /**
     * 添加IN条件
     *
     * @param columnName   数据库字段名
     * @param value        值
     * @param valueConvert 转换
     * @return 返回Query对象
     */
    public <T> Query in(String columnName, Collection<T> value, ValueConvert<T> valueConvert) {
        this.addExpression(Expressions.in(columnName, value, valueConvert));
        return this;
    }

    /**
     * 根据表达式添加IN条件
     *
     * @param expression   表达式，当为true时添加条件
     * @param columnName   数据库字段名
     * @param value        值
     * @param valueConvert 转换
     * @return 返回Query对象
     */
    public <T> Query in(boolean expression, String columnName, Collection<T> value, ValueConvert<T> valueConvert) {
        if (expression) {
            in(columnName, value, valueConvert);
        }
        return this;
    }

    /**
     * 添加IN条件
     *
     * @param columnName 数据库字段名
     * @param value      值
     * @return 返回Query对象
     */
    public Query in(String columnName, Object[] value) {
        this.addExpression(Expressions.in(columnName, value));
        return this;
    }

    /**
     * 根据表达式添加IN条件
     *
     * @param expression 表达式，当为true时添加条件
     * @param columnName 数据库字段名
     * @param value      值
     * @return 返回Query对象
     */
    public Query in(boolean expression, String columnName, Object[] value) {
        if (expression) {
            in(columnName, value);
        }
        return this;
    }

    /**
     * 添加not in条件
     *
     * @param columnName 数据库字段名
     * @param value      值
     * @return 返回Query对象
     */
    public Query notIn(String columnName, Collection<?> value) {
        this.addExpression(Expressions.notIn(columnName, value));
        return this;
    }

    /**
     * 根据表达式添加not in条件
     *
     * @param expression 表达式，当为true时添加条件
     * @param columnName 数据库字段名
     * @param value      值
     * @return 返回Query对象
     */
    public Query notIn(boolean expression, String columnName, Collection<?> value) {
        if (expression) {
            notIn(columnName, value);
        }
        return this;
    }

    /**
     * 添加not in条件
     *
     * @param columnName   数据库字段名
     * @param value        值
     * @param valueConvert 转换器
     * @return 返回Query对象
     */
    public <T> Query notIn(String columnName, Collection<T> value, ValueConvert<T> valueConvert) {
        this.addExpression(Expressions.notIn(columnName, value, valueConvert));
        return this;
    }

    /**
     * 根据表达式添加not in条件
     *
     * @param expression   表达式，当为true时添加条件
     * @param columnName   数据库字段名
     * @param value        值
     * @param valueConvert 转换器
     * @return 返回Query对象
     */
    public <T> Query notIn(boolean expression, String columnName, Collection<T> value, ValueConvert<T> valueConvert) {
        if (expression) {
            notIn(columnName, value, valueConvert);
        }
        return this;
    }

    /**
     * 添加not in条件
     *
     * @param columnName 数据库字段名
     * @param value      值
     * @return 返回Query对象
     */
    public Query notIn(String columnName, Object[] value) {
        this.addExpression(Expressions.notIn(columnName, value));
        return this;
    }

    /**
     * 根据表达式添加not in条件
     *
     * @param expression 表达式，当为true时添加条件
     * @param columnName 数据库字段名
     * @param value      值
     * @return 返回Query对象
     */
    public Query notIn(boolean expression, String columnName, Object[] value) {
        if (expression) {
            notIn(columnName, value);
        }
        return this;
    }

    /**
     * 添加between条件
     *
     * @param columnName 数据库字段名
     * @param startValue 起始值
     * @param endValue   结束值
     * @return 返回Query对象
     */
    public Query between(String columnName, Object startValue, Object endValue) {
        addExpression(new BetweenExpression(columnName, startValue, endValue));
        return this;
    }

    /**
     * 添加between条件
     *
     * @param expression 表达式，当为true时添加条件
     * @param columnName 数据库字段名
     * @param startValue 起始值
     * @param endValue   结束值
     * @return 返回Query对象
     */
    public Query between(boolean expression, String columnName, Object startValue, Object endValue) {
        if (expression) {
            between(columnName, startValue, endValue);
        }
        return this;
    }

    /**
     * 添加between条件
     * <pre>
     * {@literal
     * Object[] arr = new Object[]{1, 100};
     * query.between(arr);
     * }
     * </pre>
     *
     * @param values 存放起始值、结束值，只能存放2个值，values[0]表示开始值，value[1]表示结束值
     * @return 返回Query对象
     */
    public Query between(String columnName, Object[] values) {
        addExpression(new BetweenExpression(columnName, values));
        return this;
    }

    /**
     * 添加between条件
     *
     * @param expression 表达式，当为true时添加条件
     * @param columnName 数据库字段名
     * @param values     存放起始值、结束值，只能存放2个值，values[0]表示开始值，value[1]表示结束值
     * @return 返回Query对象
     */
    public Query between(boolean expression, String columnName, Object[] values) {
        if (expression) {
            between(columnName, values);
        }
        return this;
    }

    /**
     * 添加between条件
     * <pre>
     * {@literal
     * query.between(Arrays.asList(1, 100));
     * }
     * </pre>
     *
     * @param values 存放起始值、结束值，只能存放2个值
     * @return 返回Query对象
     */
    public Query between(String columnName, List<?> values) {
        addExpression(new BetweenExpression(columnName, values));
        return this;
    }

    /**
     * 添加between条件
     *
     * @param expression 表达式，当为true时添加条件
     * @param columnName 数据库字段名
     * @param values     存放起始值、结束值，只能存放2个值
     * @return 返回Query对象
     */
    public Query between(boolean expression, String columnName, List<?> values) {
        if (expression) {
            between(columnName, values);
        }
        return this;
    }

    /**
     * 添加between条件
     * <pre>
     * {@literal
     * query.between(new BetweenValue(1, 100));
     * }
     * </pre>
     *
     * @param betweenValue 起始值、结束值包装对象
     * @return 返回Query对象
     */
    public Query between(String columnName, BetweenValue betweenValue) {
        addExpression(new BetweenExpression(columnName, betweenValue));
        return this;
    }

    /**
     * 添加between条件
     *
     * @param expression   表达式，当为true时添加条件
     * @param columnName   数据库字段名
     * @param betweenValue 存放起始值、结束值，只能存放2个值
     * @return 返回Query对象
     */
    public Query between(boolean expression, String columnName, BetweenValue betweenValue) {
        if (expression) {
            between(columnName, betweenValue);
        }
        return this;
    }

    /**
     * 添加自定义sql条件
     *
     * @param sql 自定义sql
     * @return 返回Query对象
     */
    public Query where(String sql) {
        this.addExpression(Expressions.sql(sql));
        return this;
    }

    /**
     * 根据表达式添加自定义sql条件
     *
     * @param expression 表达式，当为true时添加条件
     * @param sql        自定义sql
     * @return 返回Query对象
     */
    public Query where(boolean expression, String sql) {
        if (expression) {
            where(sql);
        }
        return this;
    }

    /**
     * 添加自定义sql条件
     *
     * @param sqlFormat SQL模板，参数值使用?代替，如：<code>username = ? and nickname like '%?%'</code>
     * @param args      参数
     * @return 返回Query对象
     */
    public Query where(String sqlFormat, Object... args) {
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg instanceof CharSequence) {
                    args[i] = arg.toString().replaceAll(sqlInjectRegex, SqlConsts.EMPTY);
                }
            }
            for (Object arg : args) {
                sqlFormat = sqlFormat.replaceFirst(String.format("\\%s", this.argPlaceholder), String.valueOf(arg));
            }
        }
        this.addExpression(Expressions.sql(sqlFormat));
        return this;
    }

    /**
     * 根据表达式添加自定义sql条件
     *
     * @param expression 表达式，当为true时添加条件
     * @param sqlFormat  SQL模板，参数值使用?代替，如：<code>username = ? and nickname like '%?%'</code>
     * @param args       参数
     * @return 返回Query对象
     */
    public Query where(boolean expression, String sqlFormat, Object... args) {
        if (expression) {
            where(sqlFormat, args);
        }
        return this;
    }

    /**
     * 添加字段不为null的条件
     *
     * @param column 数据库字段名
     * @return 返回Query对象
     */
    public Query notNull(String column) {
        return this.where(column + " IS NOT NULL");
    }

    /**
     * 根据表达式添加字段不为null的条件
     *
     * @param expression 表达式，当为true时添加条件
     * @param column     数据库字段名
     * @return 返回Query对象
     */
    public Query notNull(boolean expression, String column) {
        if (expression) {
            notNull(column);
        }
        return this;
    }

    /**
     * 添加字段是null的条件
     *
     * @param column 数据库字段名
     * @return 返回Query对象
     */
    public Query isNull(String column) {
        return this.where(column + " IS NULL");
    }

    /**
     * 根据表达式添加字段是null的条件
     *
     * @param expression 表达式，当为true时添加条件
     * @param column     数据库字段名
     * @return 返回Query对象
     */
    public Query isNull(boolean expression, String column) {
        if (expression) {
            isNull(column);
        }
        return this;
    }

    /**
     * 添加不为空字符串条件
     *
     * @param column 数据库字段名
     * @return 返回Query对象
     */
    public Query notEmpty(String column) {
        return this.where(column + " IS NOT NULL AND " + column + " <> '' ");
    }

    /**
     * 根据表达式添加不为空字符串条件
     *
     * @param expression 表达式，当为true时添加条件
     * @param column     数据库字段名
     * @return 返回Query对象
     */
    public Query notEmpty(boolean expression, String column) {
        if (expression) {
            notEmpty(column);
        }
        return this;
    }

    /**
     * 添加空字段条件，null或者空字符串
     *
     * @param column 数据库字段名
     * @return 返回Query对象
     */
    public Query isEmpty(String column) {
        return this.where(column + " IS NULL OR " + column + " = '' ");
    }

    /**
     * 根据表达式添加空字段条件，null或者空字符串
     *
     * @param expression 表达式，当为true时添加条件
     * @param column     数据库字段名
     * @return 返回Query对象
     */
    public Query isEmpty(boolean expression, String column) {
        if (expression) {
            isEmpty(column);
        }
        return this;
    }

    /**
     * 添加1=2条件
     *
     * @return 返回Query对象
     */
    public Query oneEqTwo() {
        return this.where("1=2");
    }

    /**
     * 根据表达式添加1=2条件
     *
     * @param expression 表达式，当为true时添加条件
     * @return 返回Query对象
     */
    public Query oneEqTwo(boolean expression) {
        if (expression) {
            oneEqTwo();
        }
        return this;
    }

    /**
     * 添加关联条件
     *
     * @param joinSql 连接sql语句，如：“left join user_info t2 on t.id=t2.user_id”
     * @return 返回Query对象
     */
    public Query joinSql(String joinSql) {
        this.addExpression(Expressions.join(joinSql));
        return this;
    }

    /**
     * 使用key/value进行多个等于的比对,相当于多个eq的效果
     *
     * @param map 键值对
     * @return 返回Query对象
     */
    public Query allEq(LinkedHashMap<String, Object> map) {
        Set<String> keys = map.keySet();
        for (String columnName : keys) {
            this.eq(columnName, map.get(columnName));
        }
        return this;
    }

    /**
     * 根据表达式添加使用key/value进行多个等于的比对,相当于多个eq的效果
     *
     * @param expression 表达式，当为true时添加条件
     * @param map        键值对
     * @return 返回Query对象
     */
    public Query allEq(boolean expression, LinkedHashMap<String, Object> map) {
        if (expression) {
            allEq(map);
        }
        return this;
    }
    // ------------ 基本条件 end------------

    // ------------ 设置分页信息 ------------

    /**
     * 设置分页信息
     *
     * @param pageIndex 当前第几页,从1开始
     * @param pageSize  每页结果集大小
     * @return 返回Query对象
     */
    public Query page(int pageIndex, int pageSize) {
        if (pageIndex < 1) {
            throw new IllegalArgumentException("pageIndex必须大于等于1");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize必须大于等于1");
        }
        int start = (int) ((pageIndex - 1) * pageSize);
        return this.limit(start, pageSize);
    }

    /**
     * 设置分页信息,针对不规则分页。对应mysql分页语句：limit {start},{offset}
     *
     * @param start  记录起始位置
     * @param offset 偏移量
     * @return 返回Query对象
     */
    public Query limit(int start, int offset) {
        if (offset == 0) {
            this.setQueryAll(true);
            return this;
        }
        if (start < 0) {
            throw new IllegalArgumentException("public Query limit(int start, int offset)方法start必须大于等于0");
        }
        if (offset < 1) {
            throw new IllegalArgumentException("public Query limit(int start, int offset)方法offset必须大于等于1");
        }
        this.start = start;
        this.limit = offset;
        return this;
    }

    @Override
    public int getStart() {
        return this.start;
    }

    @Override
    public int getTotal() {
        return total;
    }

    @Override
    public boolean getIsSetTotal() {
        //不为-1，设置了总记录数
        return total != -1;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public int getLimit() {
        return this.limit;
    }

    // ------------ 设置分页信息 end ------------

    // ------------ 设置排序 ------------

    /**
     * 字段排序
     *
     * @param column 数据库字段名
     * @param sort     排序类型
     * @return 返回Query对象
     */
    public Query orderBy(String column, Sort sort) {
        return this.orderByAsc(column, sort);
    }

    /**
     * 添加ASC排序字段,
     *
     * @param column 数据库字段名
     * @return 返回Query对象
     */
    public Query orderByAsc(String column) {
        return this.orderByAsc(column, SqlConsts.ASC);
    }

    /**
     * 添加字段排序
     *
     * @param column 数据库字段名
     * @param sort     排序类型
     * @return 返回Query对象
     */
    public Query orderByAsc(String column, Sort sort) {
        return this.orderByAsc(column, sort.name());
    }

    /**
     * 添加排序字段。 已废弃，推荐用：public Query addSort(String sortname, Sort sort)
     *
     * @param column  数据库字段名
     * @param sortType 排序方式,ASC,DESC
     * @return 返回Query对象
     */
    public Query orderByAsc(String column, String sortType) {
        if (!SqlConsts.DESC.equalsIgnoreCase(sortType) && !SqlConsts.ASC.equalsIgnoreCase(sortType)) {
            throw new IllegalArgumentException("error order:" + sortType);
        }
        if (column != null && column.length() > 0) {
            if (this.orderInfo == null) {
                orderInfo = new LinkedHashSet<String>();
            }
            // 简单防止SQL注入
            column = column.replaceAll(sqlInjectRegex, SqlConsts.EMPTY);

            if (!SqlConsts.DESC.equalsIgnoreCase(sortType)) {
                sortType = SqlConsts.ASC;
            }

            orderInfo.add(column + SqlConsts.BLANK + sortType);
        }

        return this;
    }

    @Override
    public boolean getSortable() {
        return orderInfo != null;
    }

    @Override
    public String getOrder() {
        if (orderInfo == null) {
            throw new NullPointerException("orderInfo为空,必须设置排序字段.");
        }
        StringBuilder sb = new StringBuilder();
        for (String order : this.orderInfo) {
            sb.append(",").append(order);
        }
        if (sb.length() > 0) {
            return sb.toString().substring(1);
        } else {
            return "";
        }
    }
    // ------------ 设置排序 end ------------

    /**
     * 添加注解查询条件
     *
     * @param searchEntity 查询实体
     * @return 返回Query对象
     */
    public Query addAnnotationExpression(Object searchEntity) {
        bindExpressionsFromBean(searchEntity, this);
        return this;
    }


    /**
     * 添加排序信息
     *
     * @param searchEntity 查询实体
     * @return 返回Query对象
     */
    public Query addSortInfo(SchSortableParam searchEntity) {
        this.orderByAsc(searchEntity.fetchDBSortname(), searchEntity.fetchSortOrder());
        return this;
    }

    /**
     * 构建查询条件.
     *
     * @param searchEntity 查询实体
     * @return 返回Query对象
     */
    public static Query build(Object searchEntity) {
        if (searchEntity instanceof IParam) {
            return ((IParam) searchEntity).toQuery();
        } else {
            return buildFromBean(searchEntity);
        }
    }

    /**
     * 将bean中的字段转换成条件,字段名会统一转换成下划线形式.已废弃，改用Query.build(bean)
     *
     * <pre>
     * <code>
     * User user = new User();
     * user.setUserName("jim");
     * Query query = Query.buildFromBean(user);
     * </code>
     * 这样会组装成一个条件:where user_name='jim'
     * 更多功能可查看开发文档.
     * </pre>
     *
     * @param bean 查询实体
     * @return 返回Query对象
     */
    private static Query buildFromBean(Object bean) {
        Query query = new Query();
        bindExpressionsFromBean(bean, query);
        return query;
    }

    protected static void bindExpressionsFromBean(Object bean, Query query) {
        List<Expression> expressions = ConditionBuilder.getUnderlineFieldBuilder().buildExpressions(bean);
        for (Expression expression : expressions) {
            query.addExpression(expression);
        }
    }

    /**
     * 将bean中的字段转换成条件,不会将字段名转换成下划线形式.
     *
     * <pre>
     * <code>
     * User user = new User();
     * user.setUserName("jim");
     * Query query = Query.buildFromBeanByProperty(user);
     * </code>
     * 这样会组装成一个条件:where userName='jim'
     * 更多功能可查看开发文档.
     * </pre>
     *
     * @param bean 查询实体
     * @return 返回Query对象
     */
    public static Query buildFromBeanByProperty(Object bean) {
        Query query = new Query();
        List<Expression> expressions = ConditionBuilder.getCamelFieldBuilder().buildExpressions(bean);
        for (Expression expression : expressions) {
            query.addExpression(expression);
        }
        return query;

    }

    @Override
    public ExpressionFeature addExpression(Expression expression) {
        if (expression instanceof ExpressionJoinFeature) {
            if (joinExpressions == null) {
                joinExpressions = new ArrayList<>(2);
            }
            joinExpressions.add((ExpressionJoinFeature) expression);
        } else {
            expressions.add(expression);
            expressions.sort(COMPARATOR);
        }
        return this;
    }

    public void addAll(List<Expression> expressions) {
        if (expressions != null) {
            for (Expression expression : expressions) {
                this.addExpression(expression);
            }
        }
    }

    /**
     * 添加额外参数
     *
     * @param name  参数名
     * @param value 值
     * @return 返回Query对象
     */
    public Query addParam(String name, Object value) {
        if (this.paramMap == null) {
            this.paramMap = new HashMap<>(16);
        }
        this.paramMap.put(name, value);
        return this;
    }

    @Override
    public Map<String, Object> getParam() {
        return this.paramMap;
    }

    @Override
    public boolean getIsQueryAll() {
        return this.limit == 0;
    }

    /**
     * 查询全部
     *
     * @param queryAll true，则查询全部
     * @return 返回Query对象
     */
    public Query setQueryAll(boolean queryAll) {
        if (queryAll) {
            this.limit = 0;
        }
        return this;
    }

    @Override
    public List<ExpressionJoinFeature> getJoinExpressions() {
        return ExpressionSortUtil.sort(this.joinExpressions);
    }

    public boolean getForceQuery() {
        return forceQuery;
    }

    /**
     * 开启强力查询，将无视逻辑删除字段
     *
     * @return 返回Query对象
     */
    public Query enableForceQuery() {
        this.forceQuery = true;
        return this;
    }

    /**
     * 无视逻辑删除字段
     *
     * @return 返回Query对象
     */
    public Query ignoreLogicDeleteColumn() {
        this.forceQuery = true;
        return this;
    }

    /**
     * 关闭强力查询，逻辑删除字段生效
     *
     * @return 返回Query对象
     */
    public Query disableForceQuery() {
        this.forceQuery = false;
        return this;
    }

    /**
     * 使用distinct，开启后，会在主键上加distinct。distinct(t.id)<br>
     * 仅限于mysql
     */
    public Query enableDistinct() {
        this.distinct = true;
        return this;
    }

    public Query disableDistinct() {
        this.distinct = false;
        return this;
    }

    public boolean getDistinct() {
        return this.distinct;
    }

    /**
     * 开启强制更新，那么null字段也会更新进去
     *
     * @return 返回query对象
     */
    public Query enableForceUpdate() {
        this.forceUpdate = true;
        return this;
    }

    public Query disableForceUpdate() {
        this.forceUpdate = false;
        return this;
    }

    public boolean getForceUpdate() {
        return this.forceUpdate;
    }

    /**
     * 设置参数占位符，默认'?'
     *
     * @param argPlaceholder 占位符
     * @return 返回Query对象
     */
    public Query argPlaceholder(String argPlaceholder) {
        this.argPlaceholder = argPlaceholder;
        return this;
    }

    /**
     * 设置SQL注入正则表达式
     *
     * @param sqlInjectRegex 正则
     * @return 返回Query对象
     */
    public Query sqlInjectRegex(String sqlInjectRegex) {
        this.sqlInjectRegex = sqlInjectRegex;
        return this;
    }


}
