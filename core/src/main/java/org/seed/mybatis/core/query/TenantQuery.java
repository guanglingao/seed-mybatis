package org.seed.mybatis.core.query;


import org.seed.mybatis.core.query.expression.ExpressionValueFeature;
import org.seed.mybatis.core.query.param.IParam;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用于多租户查询
 * <pre>
 * {@literal
 * // 使用TenantQuery表示使用了多租户查询
 * TenantQuery query = new TenantQuery();
 * log.info("是否设置了多租户id：{}, tenantId -> {}", query.existTenantId(), query.getTenantId());
 * tenantDemoService.list(query);
 * }
 * </pre>
 */
public class TenantQuery extends Query {

    /**
     * 默认的拦截器
     */
    private static TenantQueryListener DEFAULT_TENANT_QUERY_LISTENER = query -> {
    };

    /**
     * 默认多租户id数据库字段名
     */
    private static String DEFAULT_TENANT_COLUMN_NAME = "tenant_id";

    /**
     * 多租户id数据库字段名
     */
    private final String tenantColumnName;

    public TenantQuery() {
        this(DEFAULT_TENANT_QUERY_LISTENER, DEFAULT_TENANT_COLUMN_NAME);
    }

    /**
     * 生成多租户查询类
     *
     * @param queryInterceptor 拦截器
     * @param tenantColumnName 指定表中租户字段名，如：tenant_id
     */
    public TenantQuery(TenantQueryListener queryInterceptor, String tenantColumnName) {
        this.tenantColumnName = tenantColumnName;
        if (queryInterceptor != null) {
            queryInterceptor.onInitQuery(this);
        }
    }

    /**
     * 生成多租户查询类
     *
     * @param tenantColumnName 指定表中租户字段名，如：tenant_id
     */
    public TenantQuery(String tenantColumnName) {
        this(DEFAULT_TENANT_QUERY_LISTENER, tenantColumnName);
    }

    /**
     * 构建TenantQuery对象
     *
     * @param param 查询参数
     * @return 返回TenantQuery对象
     */
    public static TenantQuery build(Object param) {
        if (param instanceof IParam) {
            return ((IParam) param).toTenantQuery();
        } else {
            return buildFromBean(param);
        }
    }

    private static TenantQuery buildFromBean(Object bean) {
        TenantQuery query = new TenantQuery();
        bindExpressionsFromBean(bean, query);
        return query;
    }

    /**
     * 返回条件中的多租户值
     *
     * @return 返回条件中的多租户值，没有返回null
     */
    public Object getTenantValue() {
        List<ExpressionValueFeature> valueExpressions = this.getValueExpressions();
        if (valueExpressions == null || valueExpressions.isEmpty()) {
            return null;
        }
        return valueExpressions.stream()
                .filter(expressionValueable -> Objects.equals(getTenantColumnName(), expressionValueable.getColumn()))
                .findFirst()
                .map(ExpressionValueFeature::getValue)
                .orElse(null);
    }

    public <T> List<ExpressionValueFeature> getValueExpressions() {
        return expressions.stream()
                .filter(expression -> expression instanceof ExpressionValueFeature)
                .map(expression -> (ExpressionValueFeature) expression)
                .collect(Collectors.toList());
    }

    public static void setDefaultTenantQueryListener(TenantQueryListener defaultTenantQueryListener) {
        DEFAULT_TENANT_QUERY_LISTENER = defaultTenantQueryListener;
    }

    public static void setDefaultTenantColumnName(String defaultTenantColumnName) {
        DEFAULT_TENANT_COLUMN_NAME = defaultTenantColumnName;
    }

    /**
     * 返回表中租户字段名
     *
     * @return 返回表中租户字段名，默认：tenant_id
     */
    public String getTenantColumnName() {
        return tenantColumnName;
    }

    /**
     * 查询条件是否已经设置了多租户值
     *
     * @return true：已经设置
     */
    public boolean existTenantValue() {
        return getTenantValue() != null;
    }


}
