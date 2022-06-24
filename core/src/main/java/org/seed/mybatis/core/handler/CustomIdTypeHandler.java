package org.seed.mybatis.core.handler;


public class CustomIdTypeHandler extends AbstractTypeHandlerAdapter<Object> {

    @Override
    protected Object convertValue(Object columnValue) {
        return null;
    }

    @Override
    protected Object getFillValue(Object defaultValue) {
        try {
            return Identities.get();
        } finally {
            Identities.remove();
        }
    }
}
