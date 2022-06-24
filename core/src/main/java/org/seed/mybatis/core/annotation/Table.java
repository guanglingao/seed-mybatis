package org.seed.mybatis.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * 表信息<br>
 * <pre>
 * {@literal
 * @Table(name = "t_user", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
 * public class TUser {
 * }
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(TYPE)
public @interface Table {

    /**
     * 指定表名称
     *
     * @return 返回表名
     */
    String name() default "";

    /**
     * 主键配置
     *
     * @return 返回注解配置
     */
    PrimaryKey key() default @PrimaryKey;


}
