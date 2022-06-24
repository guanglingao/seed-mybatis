package org.seed.mybatis.core.handler;


import org.seed.mybatis.core.ext.code.util.FieldUtil;

import java.lang.reflect.Field;

/**
 * 自定义ID填充器，实现自定义ID生成策略，需要继承这个类。
 */
public abstract class CustomIdFill<T> extends BaseFill<T> {

    @Override
    public boolean match(Class<?> entityClass, Field field, String columnName) {
        return FieldUtil.isPkStrategyNone(field, getSeedMybatisConfig());
    }

    @Override
    public FillType getFillType() {
        return FillType.INSERT; // INSERT时触发
    }

    @Override
    protected Object buildFillValue(T parameter) {
        Object val = super.buildFillValue(parameter);
        Identities.set(val);
        return val;
    }
}
