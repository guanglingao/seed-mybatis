package org.seed.mybatis.maven.dbparser;


import org.seed.mybatis.maven.converter.CsharpColumnTypeConverter;
import org.seed.mybatis.maven.util.ClassField;

/**
 * 提供C# Velocity变量
 */
public class CsharpColumnDefinition extends ColumnDefinition {

    private static final ColumnTypeConverter COLUMN_TYPE_CONVERTER = new CsharpColumnTypeConverter();

    public String getField() {
        return ClassField.underlineFilter(getColumnName());
    }

    public String getProperty() {
        return ClassField.upperFirstLetter(getField());
    }

    @Override
    public ColumnTypeConverter getColumnTypeConverter() {
        return COLUMN_TYPE_CONVERTER;
    }
}
