package org.seed.mybatis.core.ext.spi.impl;


import org.apache.ibatis.io.ResolverUtil;
import org.seed.mybatis.core.ext.spi.ClassSearch;
import org.seed.mybatis.core.mapper.Mapper;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 默认Mapper扫描实现
 */
public class MapperClassSearch implements ClassSearch {

    private static final String DEFAULT_MAPPER_PACKAGE = "org.seed.mybatis.core.mapper";

    private static final String[] FILE_STUFF = {"Mapper", "Dao", "DAO", "Repository"};


    @Override
    public Set<Class<?>> search(Class<?> targetClass, String... packageName) throws Exception {
        ResolverUtil<?> resolver = new ResolverUtil<>();
        resolver.findImplementations(targetClass, packageName);
        return resolver.getClasses()
                .stream()
                .filter(this::match)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean match(Class<?> clazz) {
        if (!clazz.isInterface()) {
            return false;
        }
        boolean isMapper = Mapper.class.isAssignableFrom(clazz);
        if (isMapper) {
            return true;
        }
        org.apache.ibatis.annotations.Mapper annotation = clazz.getAnnotation(org.apache.ibatis.annotations.Mapper.class);
        if (annotation != null) {
            return true;
        }
        // 如果是普通接口判断文件名后缀
        String name = clazz.getName();
        boolean isRightPackage = !name.startsWith(DEFAULT_MAPPER_PACKAGE);
        return isRightPackage && hasFileStuff(name);
    }

    public boolean hasFileStuff(String name) {
        for (String stuff : FILE_STUFF) {
            if (name.endsWith(stuff)) {
                return true;
            }
        }
        return false;
    }
}
