package org.seed.mybatis.core.ext.code.generator;


import org.seed.mybatis.core.ext.code.util.FieldUtil;
import org.seed.mybatis.core.ext.code.util.JavaTypeUtil;
import org.seed.mybatis.core.handler.FillType;
import org.seed.mybatis.core.handler.Identities;

/**
 * 表字段信息
 */
public class ColumnDefinition {

    private static final String PREFIX = "entity.";


    /** java字段名 */
    private String javaFieldName;
    /** 数据库字段名 */
    private String columnName;
    /** javaBean字段类型，String，Integer等 */
    private String type;
    /** javaBean字段完整类型，java.lang.String */
    private String fullType;
    private boolean isTransient;
    /** 是否自增 */
    private boolean isIdentity;
    /** 是否auto策略 */
    private boolean isAuto;
    /** 是否uuid策略 */
    private boolean isUuid;
    /** 是否sequence策略(oracle) */
    private boolean isSequence;
    /** 序列名称 */
    private String sequenceName;
    private boolean isCustomFill;

    /** 是否主键 */
    private boolean isPrimaryKey;
    private boolean isEnum;
    /** 是否乐观锁字段 */
    private boolean isVersion;
    /** 是否逻辑删除 */
    private boolean isLogicDelete;
    /** 是否是忽略更新字段 */
    private boolean isIgnoreUpdate;
    /** 删除值 */
    private Object logicDeleteValue;
    /** 未删除值 */
    private Object logicNotDeleteValue;
    private String comment;
    private String typeHandler;
    private FillType fillType;
    
    private int orderIndex = 1;

    public boolean getIsCustomFillUpdate() {
        return getIsCustomFill() && fillType == FillType.UPDATE;
    }

    /**
     * 是否是插入字段，不为自增且未设置忽略
     * @return true：是插入字段
     */
    public boolean getIsInsertColumn() {
        return (!this.isIdentity && !isIgnoreUpdate);
    }

    /**
     * 是否是更新字段，不为主键且未设置忽略
     * @return
     */
    public boolean getIsUpdateColumn() {
        return (!this.isPrimaryKey && !isIgnoreUpdate);
    }

    public boolean getIsCustomIdFill() {
        return isAuto && typeHandler != null;
    }

    public String getCustomIdTypeHandlerValue() {
        return "#{" + this.getJavaFieldName() + ", typeHandler=org.seed.mybatis.core.handler.CustomIdTypeHandler}";
    }

    public Object getAutoId() {
        try {
            return Identities.get();
        } finally {
            Identities.remove();
        }
    }

    public String getLogicDeleteValueString() {
        return formatValue(logicDeleteValue);
    }

    public String getLogicNotDeleteValueString() {
        return formatValue(logicNotDeleteValue);
    }

    public String getJdbcTypeProperty() {
        return typeHandler == null ? "jdbcType=\"" + this.getMybatisJdbcType() + "\"" : "";
    }

    public String getJavaTypeProperty() {
        return typeHandler != null ? "javaType=\"" + this.getFullType() + "\"" : "";
    }

    public String getTypeHandlerProperty() {
        return typeHandler != null ? " typeHandler=\"" + typeHandler + "\" " : "";
    }

    private String getTypeHandlerValue(FillType type) {
        return hasTypeHandler(type)
                ? (", typeHandler=" + typeHandler)
                : "";
                // jdbcType=VARCHAR
                //: type == FillType.INSERT || type == FillType.UPDATE
                //? String.format(MYBATIS_JDBC_TYPE, this.getMybatisJdbcType()) : "";
    }

    public boolean getHasTypeHandlerInsert() {
        return this.hasTypeHandler(FillType.INSERT);
    }

    public boolean getHasTypeHandlerUpdate() {
        return this.hasTypeHandler(FillType.UPDATE);
    }

    private boolean hasTypeHandler(FillType type) {
        return typeHandler != null && FillType.checkPower(this.fillType, type);
    }

    public String getMybatisInsertValue() {
        return getMybatisValue(FillType.INSERT);
    }

    public String getMybatisInsertValuePrefix() {
        return getMybatisValue(FillType.INSERT, PREFIX);
    }

    public String getMybatisUpdateValue() {
        return getMybatisValue(FillType.UPDATE);
    }

    public String getMybatisUpdateValuePrefix() {
        return getMybatisValue(FillType.UPDATE, PREFIX);
    }

    public String getMybatisSelectValue() {
        return getMybatisValue(FillType.SELECT);
    }

    private String getMybatisValue(FillType fillType) {
        return this.getMybatisValue(fillType, "");
    }

    /**
     * 返回 mybatis值内容
     * @param fillType 填充类型
     * @param prefix 前缀
     * @return 如返回<code>#{userName}</code>
     */
    private String getMybatisValue(FillType fillType, String prefix) {
        // 如果是乐观锁字段
        if (this.isVersion) { 
            return this.columnName + "+1";
        } else {
            StringBuilder mybatisValue = new StringBuilder();
            mybatisValue.append("#{" + prefix + this.getJavaFieldName()).append(this.getTypeHandlerValue(fillType))
                    .append("}");

            return mybatisValue.toString(); 
        }
    }

    /**
     * 是否是乐观锁字段
     * 
     * @return true是
     */
    public boolean getIsVersion() {
        return this.isVersion;
    }

    public void setIsVersion(boolean isVersion) {
        this.isVersion = isVersion;
    }

    /**
     * 是否是自增主键
     * 
     * @return true，是
     */
    public boolean getIsIdentityPk() {
        return isPrimaryKey && isIdentity;
    }

    public boolean getIsSequence() {
        return isSequence;
    }

    public void setIsSequence(boolean isSequence) {
        this.isSequence = isSequence;
    }

    /**
     * 返回java字段名,并且第一个字母大写
     * 
     * @return 返回java字段名,并且第一个字母大写
     */
    public String getJavaFieldNameUF() {
        return FieldUtil.upperFirstLetter(getJavaFieldName());
    }

    /**
     * 返回java字段
     * 
     * @return 返回java字段
     */
    public String getJavaFieldName() {
        return javaFieldName;
    }

    public String getJavaType() {
        return type;
    }

    /**
     * 获得装箱类型,Integer,Float
     * 
     * @return 获得装箱类型,Integer,Float
     */
    public String getJavaTypeBox() {
        return JavaTypeUtil.convertToJavaBoxType(type);
    }

    public String getMybatisJdbcType() {
        return JavaTypeUtil.convertToMyBatisJdbcType(type);
    }

    public void setJavaFieldName(String javaFieldName) {
        this.javaFieldName = javaFieldName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getIsIdentity() {
        return isIdentity;
    }

    public void setIsIdentity(boolean isIdentity) {
        this.isIdentity = isIdentity;
    }

    public boolean getIsPk() {
        return isPrimaryKey;
    }

    public void setIsPk(boolean isPk) {
        this.isPrimaryKey = isPk;
    }

    public boolean getIsUuid() {
        return isUuid;
    }

    public void setIsUuid(boolean isUuid) {
        this.isUuid = isUuid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isTransient() {
        return isTransient;
    }

    public void setTransient(boolean isTransient) {
        this.isTransient = isTransient;
    }

    public boolean getIsEnum() {
        return isEnum;
    }

    public void setEnum(boolean isEnum) {
        this.isEnum = isEnum;
    }

    public String getFullType() {
        return fullType;
    }

    public void setFullType(String fullType) {
        this.fullType = fullType;
    }

    public void setFillType(FillType fillType) {
        this.fillType = fillType;
    }

    public void setTypeHandler(String typeHandler) {
        this.typeHandler = typeHandler;
    }

    public void setIsLogicDelete(boolean isLogicDelete) {
        this.isLogicDelete = isLogicDelete;
    }

    public boolean getIsLogicDelete() {
        return this.isLogicDelete;
    }

    public Object getLogicDeleteValue() {
        return logicDeleteValue;
    }

    public void setLogicDeleteValue(Object logicDeleteValue) {
        this.logicDeleteValue = logicDeleteValue;
    }

    public Object getLogicNotDeleteValue() {
        return logicNotDeleteValue;
    }

    public void setLogicNotDeleteValue(Object logicNotDeleteValue) {
        this.logicNotDeleteValue = logicNotDeleteValue;
    }

    private String formatValue(Object value) {
        if (value instanceof String) {
            return "'" + value + "'";
        } else {
            return String.valueOf(value);
        }
    }

	public int getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(int orderIndex) {
		this.orderIndex = orderIndex;
	}

    public boolean getIsAuto() {
        return isAuto;
    }

    public void setIsAuto(boolean isAuto) {
        this.isAuto = isAuto;
    }

    public boolean isIgnoreUpdate() {
        return isIgnoreUpdate;
    }

    public void setIgnoreUpdate(boolean ignoreUpdate) {
        isIgnoreUpdate = ignoreUpdate;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
        this.isSequence = sequenceName != null && sequenceName.length() > 0;
    }

    public boolean getIsCustomFill() {
        return isCustomFill;
    }

    public void setIsCustomFill(boolean isCustomFill) {
        this.isCustomFill = isCustomFill;
    }
}
