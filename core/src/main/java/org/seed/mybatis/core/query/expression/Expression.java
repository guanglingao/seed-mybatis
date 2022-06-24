package org.seed.mybatis.core.query.expression;


public interface Expression {

    /**
     * 默认排序索引
     */
    int DEFAULT_INDEX = Integer.MAX_VALUE;

    /**
     * 设置表达式index。默认为Integer.MAX_VALUE<br/>
     * <p>
     * 该值决定WHERE后面表达式顺序，值小的靠左。
     *
     * @param index index值
     */
    void setIndex(int index);

    /**
     * 决定WHERE后面表达式顺序，值小的靠左。
     *
     * @return 返回index值
     */
    int index();

    /**
     * 指定条件类型
     *
     * @return 返回条件类型
     */
    ExpressionType expressionType();

    /**
     * 类型，0：单值查询，1：in查询，2：between，3：自定义sql
     *
     * @return 返回查询类型
     */
    default int getType() {
        return expressionType().getType();
    }

}
