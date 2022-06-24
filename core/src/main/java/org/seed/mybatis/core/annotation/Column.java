package org.seed.mybatis.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段信息<br>
 * 如果数据库命名规范遵循小写字母+下划线格式，name属性可以不指定，自动驼峰转下划线
 *
 * <p><code>
 * @Column private String username;
 * // 指定数据库字段
 * @Column(name = "age")
 * private Integer userAage;
 * </code></p>
 * <p><code>
 * // 逻辑删除字段
 * @Column(logicDelete = true)
 * private Byte isDeleted;
 * </code></p>
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    /**
     * 表字段名
     *
     * @return 表字段名
     */
    String name() default "";

    /**
     * 懒加载配置，作用在实体类的字段上。<br>
     * <p>
     * {@literal
     * public class UserInfo {
     * <p>
     * // 一对一配置，这里的user_id对应TUser主键
     * // 触发懒加载时，会拿user_id的值去查询t_user表
     * // 即：SELECT * FROM t_user WHERE id={user_id}
     *
     * @return true：是
     *
     * </p>
     * @LazyFetch(column = "user_id")
     * private TUser user;
     * <p>
     * }
     * }
     */
    boolean lazyFetch() default false;

    /**
     * 是否乐观锁字段
     *
     * @return true：是
     */
    boolean version() default false;

    /**
     * 是否逻辑删除字段
     *
     * @return 该字段是否逻辑删除字段
     */
    boolean logicDelete() default false;

    /**
     * 未删除数据库保存的值,不指定默认为0
     *
     * @return 未删除数据库保存的值
     */
    String notDeleteValue() default "";

    /**
     * 删除后数据库保存的值,不指定默认为1
     *
     * @return 删除后数据库保存的值
     */
    String deleteValue() default "";
}