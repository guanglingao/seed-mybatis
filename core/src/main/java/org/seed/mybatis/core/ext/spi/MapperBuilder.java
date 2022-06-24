package org.seed.mybatis.core.ext.spi;


import org.seed.mybatis.core.ext.MapperRunner;


public interface MapperBuilder {

    <T> MapperRunner<T> getMapperRunner(Class<T> mapperClass, Object applicationContext);

}
