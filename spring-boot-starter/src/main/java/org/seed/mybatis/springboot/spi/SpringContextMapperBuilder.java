package org.seed.mybatis.springboot.spi;

import lombok.extern.slf4j.Slf4j;
import org.seed.mybatis.core.ext.MapperRunner;
import org.seed.mybatis.core.ext.spi.MapperBuilder;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;


@Slf4j
public class SpringContextMapperBuilder implements MapperBuilder {

    private static final Map<String, MapperRunner> runnerMap = new HashMap<>();

    @Override
    public <T> MapperRunner<T> getMapperRunner(Class<T> mapperClass, Object applicationContext) {
        String simpleName = mapperClass.getSimpleName();
        simpleName = Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
        MapperRunner<T> cached = runnerMap.get(simpleName);
        if (cached != null) {
            log.debug("Using cached bean object: {}",simpleName);
            return cached;
        }
        ApplicationContext context = (ApplicationContext) applicationContext;
        T mapper = (T) context.getBean(simpleName);
        MapperRunner<T> rst = new MapperRunner<T>(mapper, null);
        runnerMap.put(simpleName, rst);
        return rst;
    }
}