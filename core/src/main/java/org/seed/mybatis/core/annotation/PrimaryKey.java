package org.seed.mybatis.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;

/**
 * 主键配置<br>
 * <pre>
 * {@literal
 *  @Table(name = "t_user", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
 *  public class TUser {
 *  }
 * }
 * </pre>
 * @since 2.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface PrimaryKey {
    /**
     * 指定数据库中主键字段名称
     *
     * @return 返回主键数据库字段名
     */
    String name() default "";

    /**
     * 主键自增策略，默认<code>INCREMENT</code>
     *
     * @return 主键自增策略，默认<code>INCREMENT</code>
     */
    KeyStrategy strategy() default KeyStrategy.AUTO;

    /**
     * sequenceName
     *
     * @return sequenceName
     */
    String sequenceName() default "";
}
