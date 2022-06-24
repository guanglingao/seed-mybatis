package org.seed.mybatis.core.query.expression.builder;


import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.seed.mybatis.core.SeedMybatisConfig;
import org.seed.mybatis.core.exception.QueryException;
import org.seed.mybatis.core.ext.code.util.FieldUtil;
import org.seed.mybatis.core.query.ConditionValueHandler;
import org.seed.mybatis.core.query.annotation.Condition;
import org.seed.mybatis.core.query.annotation.ConditionConfig;
import org.seed.mybatis.core.query.expression.BetweenValue;
import org.seed.mybatis.core.query.expression.Expression;
import org.seed.mybatis.core.query.expression.Expressions;
import org.seed.mybatis.core.util.ClassUtil;
import org.seed.mybatis.core.util.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 条件生成
 */
public class ConditionBuilder {
    private static final Log LOG = LogFactory.getLog(ConditionBuilder.class);

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private static final String PREFIX_GET = "get";
    private static final String GET_CLASS_NAME = "getClass";

    private static final ConditionBuilder underlineFieldBuilder = new ConditionBuilder(true);
    private static final ConditionBuilder camelFieldBuilder = new ConditionBuilder(false);

    private static final Map<String, String> fieldToColumnNameMap = new HashMap<>(16);
    private static final Map<String, ConditionValueHandler> conditionValueHandlerMap = new HashMap<>(16);
    private static final Map<String, Condition> methodConditionCache = new HashMap<>(16);

    private boolean camel2underline = Boolean.TRUE;

    public ConditionBuilder() {
    }

    public ConditionBuilder(boolean camel2underline) {
        this.camel2underline = camel2underline;
    }

    public static ConditionBuilder getUnderlineFieldBuilder() {
        return underlineFieldBuilder;
    }

    public static ConditionBuilder getCamelFieldBuilder() {
        return camelFieldBuilder;
    }


    /**
     * 获取条件表达式
     *
     * @param pojo pojo对象
     * @return 返回表达式结合
     */
    public List<Expression> buildExpressions(Object pojo) {
        Objects.requireNonNull(pojo, "buildExpressions(Object pojo) pojo can't be null.");
        List<Expression> expList = new ArrayList<Expression>();
        Class<?> clazz = pojo.getClass();
        ConditionConfig conditionConfig = clazz.getAnnotation(ConditionConfig.class);
        String[] ignoreFields = conditionConfig == null ? EMPTY_STRING_ARRAY : conditionConfig.ignoreFields();
        Method[] methods = clazz.getMethods();
        String fieldName = null;
        for (Method method : methods) {
            try {
                if (couldBuildExpression(method)) {
                    // 类字段名
                    fieldName = this.buildFieldName(method);
                    Condition condition = this.findCondition(method, fieldName);
                    Object value = this.getMethodValue(method, fieldName, condition, pojo);
                    if (this.isIgnore(ignoreFields, condition, fieldName, value)) {
                        continue;
                    }
                    // 数据库字段名
                    String columnName = this.getColumnName(method, condition, conditionConfig);
                    Expression expression = buildExpression(condition, columnName, value);
                    if (expression != null) {
                        expList.add(expression);
                    }
                }
            } catch (Exception e) {
                LOG.error("Build SQL expression failed, field:" + pojo.getClass().getName() + "." + fieldName, e);
                throw new QueryException(e);
            }
        }
        return expList;
    }

    /**
     * 是否忽略构建条件
     *
     * @param ignoreFields 忽略的字段
     * @param condition    条件
     * @param fieldName    属性
     * @param value        值
     * @return true：忽略空字符串
     */
    private boolean isIgnore(String[] ignoreFields, Condition condition, String fieldName, Object value) {
        if (containsArray(ignoreFields, fieldName)) {
            return true;
        }
        if (value == null) {
            return true;
        }
        if (condition != null && condition.ignore()) {
            return true;
        }
        // 是否忽略空字符串
        if (isEmptyString(condition, value)) {
            boolean scopeIgnore = condition != null && condition.ignoreEmptyString();
            // 如果为false，不忽略
            if (!scopeIgnore) {
                return false;
            }
            return SeedMybatisConfig.ignoreEmptyString;
        } else {
            // 根据指定值忽略
            String[] ignoreValues = condition == null ? EMPTY_STRING_ARRAY : condition.ignoreValue();
            return containsArray(ignoreValues, String.valueOf(value));
        }
    }

    private static boolean containsArray(Object[] array, Object objectToFind) {
        if (array == null || objectToFind == null) {
            return false;
        }
        for (Object o : array) {
            if (objectToFind.equals(o)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isEmptyString(Condition condition, Object value) {
        if (!(value instanceof String)) {
            return false;
        }
        if (condition != null && !condition.emptyStringWithTrim()) {
            return false;
        }
        String val = (String) value;
        if (SeedMybatisConfig.emptyStringWithTrim) {
            val = StringUtil.trimWhitespace(val);
        }
        return "".equals(val);
    }

    private Expression buildExpression(Condition condition, String columnName, Object value) {
        Expression expression;
        if (condition == null) {
            if (value.getClass().isArray()) {
                expression = Expressions.in(columnName, (Object[]) value);
            } else if (value instanceof Collection) {
                expression = Expressions.in(columnName, (Collection<?>) value);
            } else if (value instanceof BetweenValue) {
                expression = Expressions.between(columnName, value);
            } else {
                expression = Expressions.eq(columnName, value);
            }
        } else {
            expression = ExpressionBuilder.buildExpression(condition, columnName, value);
        }
        return expression;
    }

    private Condition findCondition(Method method, String fieldName) {
        String key = method.toString();
        Condition annotation = methodConditionCache.get(key);
        if (annotation == null) {
            // 先找get方法上的注解
            annotation = method.getAnnotation(Condition.class);
            if (annotation == null) {
                // 找不到再找字段上的注解
                Class<?> clazz = method.getDeclaringClass();
                Field field = ClassUtil.findField(clazz, fieldName);
                if (field != null) {
                    annotation = field.getAnnotation(Condition.class);
                }
                if (annotation != null) {
                    methodConditionCache.put(key, annotation);
                }
            }
        }
        return annotation;
    }

    private String getColumnName(Method method, Condition condition, ConditionConfig conditionConfig) {
        String key = method.toString();
        String columnName = fieldToColumnNameMap.get(key);
        if (columnName != null) {
            return columnName;
        }
        columnName = buildColumnNameByCondition(condition);
        if (columnName == null || "".equals(columnName)) {
            columnName = this.buildColumnNameByMethod(method, conditionConfig);
        }
        fieldToColumnNameMap.put(key, columnName);
        return columnName;
    }

    private String buildColumnNameByMethod(Method method, ConditionConfig conditionConfig) {
        String columnName = this.buildColumnName(method);
        boolean camel2underline = conditionConfig == null ? this.camel2underline : conditionConfig.camel2underline();
        if (camel2underline) {
            columnName = FieldUtil.camelToUnderline(columnName);
        }
        return columnName;
    }

    private String buildColumnNameByCondition(Condition condition) {
        String columnName = null;
        if (condition != null) {
            String column = condition.column();
            // 如果注解里面直接申明了字段名，则返回
            if (!"".equals(column)) {
                columnName = column;
            }
        }
        return columnName;
    }

    private Object getMethodValue(Method method, String fieldName, Condition condition, Object pojo) throws InvocationTargetException, IllegalAccessException {
        Object fieldValue = method.invoke(pojo);
        Class<? extends ConditionValueHandler> handlerClass = condition == null ? null : condition.handlerClass();
        if (handlerClass != null && handlerClass != ConditionValueHandler.DefaultConditionValueHandler.class) {
            try {
                ConditionValueHandler conditionValueHandler = this.getValueHandler(handlerClass);
                // 格式化返回内容，做一些特殊处理
                fieldValue = conditionValueHandler.getConditionValue(fieldValue, fieldName, pojo);
            } catch (Exception e) {
                LOG.error("handlerClass.newInstance出错，class:" + handlerClass.getName(), e);
                throw new QueryException("实例化ConditionValueHandler出错，field:" + method);
            }
        }
        return fieldValue;
    }

    private ConditionValueHandler getValueHandler(Class<? extends ConditionValueHandler> handlerClass) throws IllegalAccessException, InstantiationException {
        String key = handlerClass.getName();
        ConditionValueHandler conditionValueHandler = conditionValueHandlerMap.get(key);
        if (conditionValueHandler == null) {
            conditionValueHandler = handlerClass.newInstance();
            conditionValueHandlerMap.put(key, conditionValueHandler);
        }
        return conditionValueHandler;
    }

    /**
     * 返回数据库字段名
     */
    private String buildColumnName(Method method) {
        String getMethodName = method.getName();
        String columnName = getMethodName.substring(3);
        columnName = FieldUtil.lowerFirstLetter(columnName);
        return columnName;
    }

    /**
     * 返回字段名
     */
    private String buildFieldName(Method method) {
        // getUsername()
        String getMethodName = method.getName();
        // Username
        String columnName = getMethodName.substring(3);
        // username
        columnName = FieldUtil.lowerFirstLetter(columnName);
        return columnName;
    }

    /**
     * 能否构建表达式
     */
    private static boolean couldBuildExpression(Method method) {
        if (method.getReturnType() == Void.TYPE) {
            return false;
        }
        String methodName = method.getName();
        return (!GET_CLASS_NAME.equals(methodName)) && methodName.startsWith(PREFIX_GET);
    }

}
