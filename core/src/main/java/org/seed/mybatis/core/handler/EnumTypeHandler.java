package org.seed.mybatis.core.handler;

/**
 * 枚举相关的TypeHandler
 */
public class EnumTypeHandler<E extends Enum<?> & BaseEnum<?>> extends AbstractTypeHandlerAdapter<E> {

    private Class<E> clazz;

    public EnumTypeHandler(Class<E> enumType) {
        if (enumType == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }

        this.clazz = enumType;
    }

    @Override
    protected Object getFillValue(E defaultValue) {
        if (defaultValue == null) {
            return null;
        }
        return defaultValue.getValue();
    }

    @Override
    protected E convertValue(Object columnValue) {
        if (columnValue == null) {
            return null;
        }
        E[] enumConstants = clazz.getEnumConstants();
        for (E e : enumConstants) {
            if (e.getValue().toString().equals(columnValue.toString())) {
                return e;
            }
        }
        return null;
    }

}