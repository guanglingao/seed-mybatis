package org.seed.mybatis.core.ext.info;

/**
 * 实体类信息
 */
public class EntityInfo {

    /**
     * 主键对应的数据库字段名称
     */
    private String keyColumn;

    /**
     * 主键对应的JAVA字段名称
     */
    private String keyJavaField;


    public String getKeyColumn() {
        return keyColumn;
    }

    public void setKeyColumn(String keyColumn) {
        this.keyColumn = keyColumn;
    }

    public String getKeyJavaField() {
        return keyJavaField;
    }

    public void setKeyJavaField(String keyJavaField) {
        this.keyJavaField = keyJavaField;
    }
}
