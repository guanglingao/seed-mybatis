package org.seed.mybatis.core.ext.spi.impl;


import org.seed.mybatis.core.ext.code.util.FieldUtil;
import org.seed.mybatis.core.ext.spi.BeanExecutor;
import org.seed.mybatis.core.util.ClassUtil;
import org.seed.mybatis.core.util.MyBeanUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


public class DefaultBeanExecutor implements BeanExecutor {

    @Override
    public Map<String, Object> pojoToMap(Object pojo) {
        return MyBeanUtil.pojoToMap(pojo);
    }

    @Override
    public <T> T pojoToValue(Object pojo, Class<T> valClass, String column) {
        if (pojo == null) {
            return null;
        }
        final String fieldName = FieldUtil.formatField(column);
        Map<String, Object> row = pojoToMap(pojo);
        return row.entrySet().stream()
                .filter(entry -> {
                    String key = entry.getKey();
                    return key.equalsIgnoreCase(fieldName) || FieldUtil.camelToUnderline(key).equalsIgnoreCase(fieldName);
                })
                .findFirst()
                .map(entry -> parseValue(entry.getValue(), valClass))
                .orElse(null);
    }

    @Override
    public <T> T parseValue(Object value, Class<T> valClass) {
        return MyBeanUtil.parseValue(value, valClass);
    }

    @Override
    public <T> List<T> copyBean(List<?> from, Class<T> toClass) {
        if (from == null || from.isEmpty()) {
            return new ArrayList<>();
        }
        return from.stream()
                .filter(Objects::nonNull)
                .map(source -> copyBean(source, toClass))
                .collect(Collectors.toList());
    }

    @Override
    public <T> T copyBean(Object from, Class<T> toClass) {
        if (from == null) {
            return null;
        }
        T target = ClassUtil.newInstance(toClass);
        copyProperties(from, target);
        return target;
    }

    @Override
    public void copyProperties(Object source, Object target) {
        MyBeanUtil.copyProperties(source, target);
    }

    @Override
    public void copyPropertiesIgnoreNull(Object source, Object target) {
        MyBeanUtil.copyPropertiesIgnoreNull(source, target);
    }
}
