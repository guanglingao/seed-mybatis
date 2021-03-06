package org.seed.mybatis.core.query.expression;


/**
 * Like条件查询,右边模糊匹配,即'aaa%'
 */
public class LikeRightBlurExpression extends AbstractLikeExpression {

    /**
     * Like条件查询,右边模糊匹配,即'aaa%'
     *
     * @param column 数据库字段名
     * @param value  查询的值
     */
    public LikeRightBlurExpression(String column, Object value) {
        super(column, value);
    }

    /**
     * Like条件查询,右边模糊匹配,即'aaa%'
     *
     * @param joint  连接符,如AND或OR
     * @param column 数据库字段名
     * @param value  查询的值
     */
    public LikeRightBlurExpression(String joint, String column, Object value) {
        super(joint, column, value);
    }

    @Override
    public Object getValue() {
        return super.getValue() + "%";
    }
}
