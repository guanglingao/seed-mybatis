package org.seed.mybatis.core.query;


import org.seed.mybatis.core.query.expression.ExpressionFeature;

import java.util.Map;


public interface Queryable extends Sortable, ExpressionFeature, Pageable {
    /**
     * 返回自定义参数，在xml中使用<code>#{参数名}</code>获取值
     *
     * @return 返回自定义参数
     */
    Map<String, Object> getParam();

}
