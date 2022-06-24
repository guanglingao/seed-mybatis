package org.seed.mybatis.core.support;


import org.seed.mybatis.core.handler.FillType;

import java.time.LocalDateTime;

/**
 *
 */
public class LocalDateTimeFillGmtModified extends BaseLocalDateTimeFill {

    private String columnName = "gmt_modified";

    public LocalDateTimeFillGmtModified() {
        super();
    }

    public LocalDateTimeFillGmtModified(String columnName) {
        super();
        this.columnName = columnName;
    }

    @Override
    public FillType getFillType() {
        return FillType.UPDATE;
    }

    @Override
    public LocalDateTime getFillValue(LocalDateTime defaultValue) {
        return LocalDateTime.now();
    }


    @Override
    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }


}
