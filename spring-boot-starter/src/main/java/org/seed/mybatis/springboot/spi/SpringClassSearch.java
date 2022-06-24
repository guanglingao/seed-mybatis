package org.seed.mybatis.springboot.spi;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.seed.mybatis.core.ext.spi.ClassSearch;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


@Slf4j
public class SpringClassSearch implements ClassSearch {

    private static final String RESOURCE_PATTERN = "/**/*.class";

    private final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    @Override
    public Set<Class<?>> search(Class<?> targetClass, String... packageName) throws Exception {
        List<TypeFilter> typeIncludes = new LinkedList<>();
        // scanClass是注解类型
        // AnnotationTypeFilter 有scanClass注解的类将被过滤出来,不能过滤接口
        typeIncludes.add(new AnnotationTypeFilter(Mapper.class, false));
        // AssignableTypeFilter 继承或实现superClass的类将被过滤出来
        // superClass可以是接口
        typeIncludes.add(new AssignableTypeFilter(org.seed.mybatis.core.mapper.Mapper.class));
        Set<Class<?>> classSet = new HashSet<>();
        if (packageName != null) {
            for (String pkg : packageName) {
                // classpath*:com/xx/dao/**/*.class
                String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(pkg) + RESOURCE_PATTERN;
                Resource[] resources = this.resourcePatternResolver.getResources(pattern);
                MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);
                for (Resource resource : resources) {
                    if (resource.isReadable()) {
                        MetadataReader reader = readerFactory.getMetadataReader(resource);
                        if (matchesEntityTypeFilter(reader, readerFactory, typeIncludes)) {
                            String className = reader.getClassMetadata().getClassName();
                            classSet.add(Class.forName(className));
                            log.debug("SeedMyBatis scanned class: {}", className);
                        }
                    }
                }
            }
        }
        return classSet;
    }


    /**
     * 检查当前扫描到的Bean含有任何一个指定的注解标记
     *
     * @param reader
     * @param readerFactory
     * @return 返回true表示它是一个Mapper
     * @throws IOException
     */
    private boolean matchesEntityTypeFilter(MetadataReader reader, MetadataReaderFactory readerFactory, List<TypeFilter> typeIncludes) throws IOException {
        for (TypeFilter filter : typeIncludes) {
            if (filter.match(reader, readerFactory)) {
                return true;
            }
        }
        return false;
    }
}
