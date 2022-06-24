package org.seed.mybatis.core.ext.code.util;


public class JavaType {

    /** 基本类型 */
    private String primaryType;
    /** 装箱类型 */
    private String boxedType;
    /** 对应的mybatis类型 */
    private String mybatisType;

    /**
     * @param primaryType
     *            基本类型
     * @param boxedType
     *            装箱类型
     * @param mybatisType
     *            对应的mybatis类型
     */
    public JavaType(String primaryType, String boxedType, String mybatisType) {
        super();
        this.primaryType = primaryType;
        this.boxedType = boxedType;
        this.mybatisType = mybatisType;
    }

    public String getPrimaryType() {
        return primaryType;
    }

    public String getBoxedType() {
        return boxedType;
    }

    public String getMybatisType() {
        return mybatisType;
    }
}