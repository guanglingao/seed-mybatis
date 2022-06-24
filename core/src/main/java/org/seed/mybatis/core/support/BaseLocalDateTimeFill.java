package org.seed.mybatis.core.support;


import org.seed.mybatis.core.handler.BaseFill;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public abstract class BaseLocalDateTimeFill extends BaseFill<LocalDateTime> {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    protected LocalDateTime convertValue(Object columnValue) {
        if (columnValue == null) {
            return null;
        }
        if (columnValue instanceof Timestamp) {
            Timestamp timestamp = (Timestamp) columnValue;
            return timestamp.toLocalDateTime();
        } else {
            return LocalDateTime.parse(columnValue.toString(), DATE_TIME_FORMATTER);
        }
    }
}
