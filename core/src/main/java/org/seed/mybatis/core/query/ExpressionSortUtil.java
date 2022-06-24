package org.seed.mybatis.core.query;



import org.seed.mybatis.core.query.expression.Expression;

import java.util.Comparator;
import java.util.List;



public class ExpressionSortUtil {

    private ExpressionSortUtil() {}

    /**
     * 给条件排序
     * @param expressionList 条件列表
     * @param <T> 表达式
     * @return 返回排序后的列表
     */
    public static <T extends Expression> List<T> sort(List<T> expressionList) {
        if (expressionList == null) {
            return null;
        }

        expressionList.sort(Comparator.comparingInt(Expression::index));
        return expressionList;
    }

}
