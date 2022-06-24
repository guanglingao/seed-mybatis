package org.seed.mybatis.core.ext.code.generator;



import org.seed.mybatis.core.SeedMybatisConfig;
import org.seed.mybatis.core.annotation.Table;
import org.seed.mybatis.core.ext.code.util.FieldUtil;
import org.seed.mybatis.core.util.StringUtil;

import java.util.Optional;

/**
 * 表选择
 */
public class TableSelector {

    private final ColumnSelector columnSelector;
    private final Class<?> entityClass;
    private final SeedMybatisConfig config;

    public TableSelector(Class<?> entityClass, SeedMybatisConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("SeedMybatisConfig不能为null");
        }
        if (entityClass == null) {
            throw new IllegalArgumentException("entityClass不能为null");
        }
        this.entityClass = entityClass;
        this.config = config;
        this.columnSelector = new ColumnSelector(entityClass, config);
    }

    public TableDefinition getTableDefinition() {
        TableDefinition tableDefinition = new TableDefinition();
        String tableName = getTableName();
        tableDefinition.setTableName(tableName);
        tableDefinition.setColumnDefinitions(columnSelector.getColumnDefinitions());
        tableDefinition.setAssociationDefinitions(columnSelector.getAssociationDefinitions());
        return tableDefinition;
    }

    private String getTableName() {
        String tableName = null;
        Optional<Table> tableAnnotation = FieldUtil.getTableAnnotation(entityClass);
        if (tableAnnotation.isPresent()) {
            tableName = tableAnnotation.get().name();
        }
        if (StringUtil.isEmpty(tableName)) {
            String javaBeanName = entityClass.getSimpleName();
            if (config.isCamel2underline()) {
                tableName = FieldUtil.camelToUnderline(javaBeanName);
            }
        }
        return tableName;
    }


}
