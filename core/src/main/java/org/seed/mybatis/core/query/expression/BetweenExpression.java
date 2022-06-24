package org.seed.mybatis.core.query.expression;


import org.seed.mybatis.core.SqlConsts;
import org.seed.mybatis.core.query.Joint;

import java.util.List;
import java.util.Objects;


public class BetweenExpression implements ExpressionBetweenFeature {

    private int index = DEFAULT_INDEX;

    private String joint = SqlConsts.AND;

    /**
     * 数据库字段名
     */
    private final String column;

    /**
     * 开始值
     */
    private final Object startValue;

    /**
     * 结束值
     */
    private final Object endValue;

    /**
     * 构建between条件
     *
     * @param column 数据库字段名
     * @param values 条件值
     */
    public BetweenExpression(String column, Object values) {
        Objects.requireNonNull(values);
        Objects.requireNonNull(column);
        this.column = column;
        if (values.getClass().isArray()) {
            Object[] arr = (Object[]) values;
            if (arr.length != 2) {
                throw new IllegalArgumentException("设置 between 表达式错误，数组元素必须为2个，当前个数：" + arr.length + "。array[0]表示开始值，array[1]表示结束值");
            }
            this.startValue = arr[0];
            this.endValue = arr[1];
        } else if (values instanceof List) {
            List<?> list = (List<?>) values;
            if (list.size() != 2) {
                throw new IllegalArgumentException("设置 between 表达式错误，list元素必须为2个，当前个数：" + list.size() + "。list.get(0)表示开始值，list.get(1)表示结束值");
            }
            this.startValue = list.get(0);
            this.endValue = list.get(1);
        } else if (values instanceof BetweenValue) {
            BetweenValue betweenValue = (BetweenValue) values;
            this.startValue = betweenValue.getStartValue();
            this.endValue = betweenValue.getEndValue();
        } else {
            throw new IllegalArgumentException("error between type");
        }
    }

    public BetweenExpression(String column, BetweenValue betweenValue) {
        Objects.requireNonNull(betweenValue);
        this.column = Objects.requireNonNull(column);
        this.startValue = Objects.requireNonNull(betweenValue.getStartValue());
        this.endValue = Objects.requireNonNull(betweenValue.getEndValue());
    }

    public BetweenExpression(String column, Object startValue, Object endValue) {
        this(Joint.AND, column, startValue, endValue);
    }

    public BetweenExpression(Joint joint, String column, Object startValue, Object endValue) {
        this.joint = joint.getJoint();
        this.column = Objects.requireNonNull(column);
        this.startValue = Objects.requireNonNull(startValue);
        this.endValue = Objects.requireNonNull(endValue);
    }

    @Override
    public String getColumn() {
        return column;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public Object getStartValue() {
        return startValue;
    }

    @Override
    public Object getEndValue() {
        return endValue;
    }

    @Override
    public ExpressionType expressionType() {
        return ExpressionType.BETWEEN;
    }
}
