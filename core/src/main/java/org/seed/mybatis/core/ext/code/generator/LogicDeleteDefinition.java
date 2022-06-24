package org.seed.mybatis.core.ext.code.generator;


public class LogicDeleteDefinition {

    /**
     * 未删除数据库保存的值,不指定默认为0
     */
    private String notDeleteValue;

    /**
     * 删除后数据库保存的值,不指定默认为1
     */
    private String deleteValue;

    public String getNotDeleteValue() {
        return notDeleteValue;
    }

    public void setNotDeleteValue(String notDeleteValue) {
        this.notDeleteValue = notDeleteValue;
    }

    public String getDeleteValue() {
        return deleteValue;
    }

    public void setDeleteValue(String deleteValue) {
        this.deleteValue = deleteValue;
    }
}
