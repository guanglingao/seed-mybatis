package org.seed.mybatis.core.query.param;


import org.seed.mybatis.core.query.annotation.Condition;

/**
 * 不规则分页查询参数
 */
public class OffsetParam implements SchPageableParam {

    /**
     * 记录起始位置
     *
     * @mock 1
     */
    private int start = 0;

    /**
     * 记录结束位置
     *
     * @mock 20
     */
    private int limit = 20;

    /**
     * @ignore
     */
    @Condition(ignore = true)
    @Override
    public int fetchStart() {
        return getStart();
    }

    /**
     * @ignore
     */
    @Condition(ignore = true)
    @Override
    public int fetchLimit() {
        return getLimit();
    }

    @Condition(ignore = true)
    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    @Condition(ignore = true)
    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
