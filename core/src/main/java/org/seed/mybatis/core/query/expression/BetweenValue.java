package org.seed.mybatis.core.query.expression;


public class BetweenValue {
    /**
     * 开始值
     */
    private final Object startValue;

    /**
     * 结束值
     */
    private final Object endValue;

    public BetweenValue(Object startValue, Object endValue) {
        this.startValue = startValue;
        this.endValue = endValue;
    }


    public Object getStartValue() {
        return startValue;
    }

    public Object getEndValue() {
        return endValue;
    }
}
