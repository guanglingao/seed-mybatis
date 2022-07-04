package org.seed.mybatis.springboot.scatter;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 动态切换数据源
 */
@Slf4j
@Aspect
@Component
// 指定优先级高于@Transactional的默认优先级
// 从而保证先切换数据源再进行事务操作
@Order(Ordered.LOWEST_PRECEDENCE - 1)
public class DataSourceRoutingAdvisor {


    private Map<String, ShardingStrategy> strategyMap = new HashMap<>();


    @Around("@annotation(scatter)")
    public Object switchDataSource(ProceedingJoinPoint pjp, Scatter scatter) throws Throwable {

        // ---- 存在声明式分库分表
        // 1. 获取 @Scatter 注解中的分库分表策略
        Class<? extends ShardingStrategy> strategyClass = scatter.strategy();
        // 2. 设置数据源上下文
        ShardingStrategy strategy = strategyMap.get(strategyClass.getSimpleName());
        if (strategy == null) {
            strategy = strategyClass.newInstance();
            strategyMap.put(strategyClass.getSimpleName(), strategy);
        }
        log.debug("==> Using Scatter Strategy: {}",strategy.getClass().getName());
        // 3. 获取分库分表字段名称
        String byField = scatter.by();
        if (!StringUtils.hasText(byField)) {
            setRoutingDataSourceContext(strategy);
            try {
                return pjp.proceed();
            } finally {
                clearRoutingDataSourceContext();
            }
        }
        Object[] parameters = pjp.getArgs();
        // 4. 无参数
        if (parameters == null || parameters.length == 0) {
            setRoutingDataSourceContext(strategy);
            try {
                return pjp.proceed();
            } finally {
                clearRoutingDataSourceContext();
            }
        }
        // 5. 获取第一个符合名称的分库分表参数值
        Object scatterBasis = breadthFirstParameterValue(pjp, byField);
        log.debug("==> Match Scatter Key: {}, with Value: {}",byField,scatterBasis);
        strategy.setBasis(scatterBasis);
        setRoutingDataSourceContext(strategy);
        try {
            return pjp.proceed();
        } finally {
            clearRoutingDataSourceContext();
        }
    }

    private void setRoutingDataSourceContext(ShardingStrategy strategy) {
        RoutingDataSourceContext.setDataSourceKey(strategy.getDataSourceId());
        RoutingDataSourceContext.setTablePrefix(strategy.getTablePrefix());
        RoutingDataSourceContext.setTableSuffix(strategy.getTableSuffix());
        RedirectToTableInterceptor.setEnableTableSharding(true);
    }

    private void clearRoutingDataSourceContext() {
        RoutingDataSourceContext.clearDataSourceKey();
        RoutingDataSourceContext.clearTablePrefix();
        RoutingDataSourceContext.clearTableSuffix();
        RedirectToTableInterceptor.setEnableTableSharding(false);
    }


    private Object breadthFirstParameterValue(ProceedingJoinPoint pjp, String targetFieldName) {
        Object[] args = pjp.getArgs();
        if (args == null || args.length == 0) {
            return null;
        }
        MethodSignature sign = (MethodSignature) pjp.getSignature();
        Method exeMethod = sign.getMethod();
        Parameter[] parameters = exeMethod.getParameters();
        // 1. 当前层匹配名称一致
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (targetFieldName.equals(parameter.getName())) {
                return args[i];
            }
        }
        // 2. 当前层，无匹配参数名称
        for (Object object : args) {
            // 对Map类型对象特殊处理
            if (Map.class.isAssignableFrom(object.getClass())) {
                Map map = (Map) object;
                Object item = map.get(targetFieldName);
                if (item != null) {
                    return item;
                }
            }
            // 对基础数据类型和String特殊处理
            if (ClassUtils.isPrimitiveOrWrapper(object.getClass()) || object.getClass().getName().equals("java.lang.String")) {
                continue;
            }
        }
        // 第一层，已检查完毕; 尝试匹配每个参数内部的Field
        //===========================================
        // 检查参数内部的字段
        for (Object object : args) {
            Class<?> objClass = object.getClass();
            if (ClassUtils.isPrimitiveOrWrapper(objClass)
                    || objClass.getName().equals("java.lang.String")
                    || Map.class.isAssignableFrom(objClass)
                    || objClass.isArray()
                    || List.class.isAssignableFrom(objClass)
            ) {
                continue;
            }

            if (hasFieldMatch(object, targetFieldName)) {
                return getFieldValueByGet(object, targetFieldName);
            }
        }
        log.error("Scatter Data 未找到匹配的字段名称，请检查Strategy设置和参数名称");
        return null;
    }


    private Map<String, Integer> fieldExistMap = new HashMap<>();

    private boolean hasFieldMatch(Object object, String targetFieldName) {
        Class<?> clazz = object.getClass();
        String className = clazz.getName();

        Integer i = fieldExistMap.get(className);
        if (i != null) {
            return i.intValue() == 1;
        }
        Field field = ReflectionUtils.findField(clazz, targetFieldName);
        if (field != null) {
            fieldExistMap.put(className, 1);
            return true;
        }
        String methodGet = "get" + Character.toUpperCase(targetFieldName.charAt(0)) + targetFieldName.substring(1);
        Method method = ReflectionUtils.findMethod(clazz, methodGet);
        if (method != null) {
            fieldExistMap.put(className, 1);
            return true;
        }
        fieldExistMap.put(className, 0);
        return false;
    }

    private Object getFieldValueByGet(Object object, String fieldName) {
        String methodGet = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        try {
            Method method = ReflectionUtils.findMethod(object.getClass(), methodGet);
            return method.invoke(object);
        } catch (Exception e) {
            log.error("Method [{}] Not Found, {}", methodGet, e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


}
