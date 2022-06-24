package org.seed.mybatis.core;

import java.util.Map;

/**
 * 记录处理
 *
 * @param <E> 实体类
 */
public interface EntityProcessor<E> {
    /**
     * 处理记录
     *
     * @param entity    记录对象
     * @param entityMap 记录对象中的属性名/值所对应的map
     */
    void process(E entity, Map<String, Object> entityMap);
}
