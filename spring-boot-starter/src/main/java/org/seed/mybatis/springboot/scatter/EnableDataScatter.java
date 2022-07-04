package org.seed.mybatis.springboot.scatter;


import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

/**
 * 启用分库分表实现
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(value = DataScatterBeanRegistrar.class)
public @interface EnableDataScatter {


}
