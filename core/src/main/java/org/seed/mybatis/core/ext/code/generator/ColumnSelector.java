package org.seed.mybatis.core.ext.code.generator;



import org.seed.mybatis.core.SeedMybatisConfig;
import org.seed.mybatis.core.SeedMybatisContext;
import org.seed.mybatis.core.ext.ExtContext;
import org.seed.mybatis.core.ext.code.util.FieldUtil;
import org.seed.mybatis.core.ext.code.util.JavaTypeUtil;
import org.seed.mybatis.core.ext.code.util.ReflectUtil;
import org.seed.mybatis.core.ext.exception.GenerateCodeException;
import org.seed.mybatis.core.ext.info.EntityInfo;
import org.seed.mybatis.core.handler.BaseEnum;
import org.seed.mybatis.core.handler.BaseFill;
import org.seed.mybatis.core.handler.EnumTypeHandler;
import org.seed.mybatis.core.handler.FillType;
import org.seed.mybatis.core.util.ClassUtil;
import org.seed.mybatis.core.util.StringUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 字段查询
 */
public class ColumnSelector {

    private static final String SELECT_GET = "%s.getById";
    private static final String FETCH_GET = "%s.forceById";

    private final Class<?> entityClass;
    private final SeedMybatisConfig config;

    public ColumnSelector(Class<?> entityClass, SeedMybatisConfig config) {
        super();
        this.entityClass = entityClass;
        this.config = config;
    }

    /**
     * javaBean字段类型，String，Integer等
     *
     * @param field 字段
     * @return 返回简单类型
     */
    private String getColumnType(Field field) {
        String columnType = field.getType().getSimpleName();
        if ("Object".equals(columnType)) {
            columnType = ClassUtil.getSuperClassGenricType(this.entityClass, 0).getSimpleName();
        }
        return columnType;
    }

    /**
     * 返回javaBean字段完整类型，java.lang.String
     *
     * @param field 字段
     * @return 返回完整的类型
     */
    private String getColumnFullType(Field field) {
        String fullType = field.getType().getName();
        if ("java.lang.Object".equals(fullType)) {
            fullType = ClassUtil.getSuperClassGenricType(this.entityClass, 0).getSimpleName();
        }
        return fullType;
    }

    private boolean isEnum(Field field) {
        Class<?> enumType = field.getType();
        boolean isEnum = enumType.isEnum();
        if (isEnum) {
            this.checkEnum(enumType);
        }
        return isEnum;
    }

    private void checkEnum(Class<?> enumType) {
        boolean isBaseEnum = false;
        Class<?> baseEnumClass = BaseEnum.class;
        Type[] arr = enumType.getInterfaces();

        for (Type type : arr) {
            if (type.equals(baseEnumClass)) {
                isBaseEnum = true;
                break;
            }
        }

        if (!isBaseEnum) {
            throw new GenerateCodeException("枚举类：" + enumType.getName() + "必须实现" + baseEnumClass.getName() + "接口");
        }
    }

    /**
     * 根据java字段获取数据库字段名
     */
    private String getColumnName(Field field) {
        return FieldUtil.getColumnName(field, config);
    }

    /**
     * 构建columnDefinition
     *
     * @return 返回字段定义
     */
    public List<ColumnDefinition> getColumnDefinitions() {
        List<Field> fields = ReflectUtil.getDeclaredFields(entityClass);
        List<ColumnDefinition> columnDefinitionList = new ArrayList<>(fields.size());
        int pkCount = 0;
        for (Field field : fields) {
            ColumnDefinition columnDefinition = buildColumnDefinition(field);
            if (columnDefinition != null) {
                columnDefinitionList.add(columnDefinition);
                if (columnDefinition.getIsPk()) {
                    pkCount++;
                }
            }
        }
        if (pkCount == 0) {
            Optional<ColumnDefinition> id = columnDefinitionList
                    .stream()
                    .filter(columnDefinition -> config.getGlobalIdName().equals(columnDefinition.getColumnName()))
                    .findFirst();
            // 如果找不到主键，字段名为id的作为自增主键
            if (id.isPresent()) {
                ColumnDefinition pk = id.get();
                pk.setIsPk(true);
                pk.setIsIdentity(config.isGlobalIdIncrement());
            } else {
                // 否则取第一个字段当主键
                ColumnDefinition first = columnDefinitionList.get(0);
                first.setIsPk(true);
            }
        }
        return columnDefinitionList;
    }

    /**
     * 返回一对一关联配置
     *
     * @return 返回一对一关联配置
     */
    public List<AssociationDefinition> getAssociationDefinitions() {
        List<Field> fields = ReflectUtil.getDeclaredFields(entityClass);
        List<AssociationDefinition> associations = new ArrayList<>(8);
        for (Field field : fields) {
            AssociationDefinition associationDefinition = buildAssociationDefinition(field);
            if (associationDefinition != null) {
                associations.add(associationDefinition);
            }
        }
        return associations;
    }

    protected AssociationDefinition buildAssociationDefinition(Field field) {
        boolean isTransient = FieldUtil.isTransientField(field);
        if (isTransient) {
            return null;
        }
        if (!FieldUtil.hasTableAnnotation(field)) {
            return null;
        }
        Class<?> clazz = field.getType();
        String column = FieldUtil.getLazyEntityColumnName(field, config);
        if (StringUtil.isEmpty(column)) {
            return null;
        }
        String property = field.getName();
        Class<?> mapperClass = ExtContext.getMapperClass(clazz);
        String namespace = mapperClass.getName();
        String temp = config.getIgnoreLogicDeleteWithAssociation() ? FETCH_GET : SELECT_GET;
        String select = String.format(temp, namespace);

        AssociationDefinition associationDefinition = new AssociationDefinition();
        associationDefinition.setColumn(column);
        associationDefinition.setProperty(property);
        associationDefinition.setSelect(select);

        return associationDefinition;
    }

    /**
     * 构建列信息
     *
     * @param field 字段信息
     * @return 返回构建列信息
     */
    protected ColumnDefinition buildColumnDefinition(Field field) {
        ColumnDefinition columnDefinition = new ColumnDefinition();

        boolean isTransient = FieldUtil.isTransientField(field);
        columnDefinition.setTransient(isTransient);

        String columnName = this.getColumnName(field);
        String columnType = this.getColumnType(field);
        String fullType = this.getColumnFullType(field);
        boolean isEnum = this.isEnum(field);
        // 不是枚举,也不是java类型
        if (!isEnum && !JavaTypeUtil.isJavaType(columnType)) {
            return null;
        }

        columnDefinition.setJavaFieldName(field.getName());
        columnDefinition.setColumnName(columnName);
        columnDefinition.setType(columnType);
        columnDefinition.setFullType(fullType);
        columnDefinition.setEnum(isEnum);
        columnDefinition.setIgnoreUpdate(this.config.getIgnoreUpdateColumns().contains(columnName));

        if (isEnum) {
            columnDefinition.setTypeHandler(EnumTypeHandler.class.getName());
            columnDefinition.setFillType(FillType.UPDATE);
        }

        boolean isPk = FieldUtil.isPk(field, config);
        columnDefinition.setIsPk(isPk);
        if (isPk) {
            String sequenceName = FieldUtil.getSequenceName(field);
            if (StringUtil.hasText(sequenceName)) {
                columnDefinition.setSequenceName(sequenceName);
            } else if (FieldUtil.isIncrement(field)) {
                columnDefinition.setIsIdentity(true);
            } else if (FieldUtil.isUuid(field)) {
                columnDefinition.setIsUuid(true);
                // 是否auto
                columnDefinition.setIsAuto(false);
            }
            this.setEntityInfo(columnDefinition);
        }

        boolean isVersionColumn = FieldUtil.isVersionColumn(field);
        columnDefinition.setIsVersion(isVersionColumn);

        this.bindLogicDeleteColumnInfo(columnDefinition, field);

        if (!isTransient) {
            this.setFills(columnDefinition, field);
        }

        return columnDefinition;
    }


    private void setEntityInfo(ColumnDefinition columnDefinition) {
        EntityInfo entityInfo = new EntityInfo();
        entityInfo.setKeyColumn(columnDefinition.getColumnName());
        entityInfo.setKeyJavaField(columnDefinition.getJavaFieldName());
        SeedMybatisContext.setEntityInfo(entityClass, entityInfo);
    }

    /**
     * 绑定逻辑删除字段信息
     */
    private void bindLogicDeleteColumnInfo(ColumnDefinition columnDefinition, Field field) {
        LogicDeleteDefinition logicDeleteDefinition = FieldUtil.getLogicDeleteDefinition(field);
        // 不是逻辑删除字段
        if (logicDeleteDefinition == null) {
            return;
        }
        columnDefinition.setIsLogicDelete(true);
        Object delVal, notDelVal;
        String deleteValue = logicDeleteDefinition.getDeleteValue();
        String notDeleteValue = logicDeleteDefinition.getNotDeleteValue();
        // 如果没有指定则使用全局配置的值
        if ("".equals(deleteValue)) {
            deleteValue = this.config.getLogicDeleteValue();
        }
        if ("".equals(notDeleteValue)) {
            notDeleteValue = this.config.getLogicNotDeleteValue();
        }
        delVal = StringUtil.isInteger(deleteValue) ? Integer.valueOf(deleteValue) : deleteValue;
        columnDefinition.setLogicDeleteValue(delVal);
        notDelVal = StringUtil.isInteger(notDeleteValue) ? Integer.valueOf(notDeleteValue) : notDeleteValue;
        columnDefinition.setLogicNotDeleteValue(notDelVal);
    }




    private void setFills(ColumnDefinition columnDefinition, Field field) {
        String columnName = columnDefinition.getColumnName();
        BaseFill<?> fill = config.getFill(entityClass, field, columnName);
        if (fill != null) {
            FillType fillType = fill.getFillType();
            columnDefinition.setTypeHandler(fill.getClass().getName());
            columnDefinition.setFillType(fillType);
            columnDefinition.setIsCustomFill(fillType == FillType.INSERT || fillType == FillType.UPDATE);
        }
    }


}
