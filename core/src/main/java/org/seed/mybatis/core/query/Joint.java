package org.seed.mybatis.core.query;

/**
 * 表达式之间的连接符
 */
public enum Joint {
    /**
     * 连接符 and
     */
    AND("AND"),
    /**
     * 连接符 or
     */
    OR("OR");
    private String joint;

    Joint(String joint) {
        this.joint = joint;
    }

    public String getJoint() {
        return joint;
    }
}
