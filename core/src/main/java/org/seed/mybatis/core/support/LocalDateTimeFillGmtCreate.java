package org.seed.mybatis.core.support;


import org.seed.mybatis.core.handler.FillType;

import java.time.LocalDateTime;

/**
 *
 */
public class LocalDateTimeFillGmtCreate extends BaseLocalDateTimeFill {


    private String columnName = "gmt_create";

    public LocalDateTimeFillGmtCreate() {
        super();
    }

    public LocalDateTimeFillGmtCreate(String columnName) {
        super();
        this.columnName = columnName;
    }

    @Override
    public FillType getFillType() {
        return FillType.INSERT;
    }

    @Override
    public LocalDateTime getFillValue(LocalDateTime defaultValue) {
        if (defaultValue == null) {
            defaultValue = LocalDateTime.now();
        }
        return defaultValue;
    }


    @Override
    public String getColumnName() {
        return columnName;
    }
}
