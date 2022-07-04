package org.seed.mybatis.springboot.util;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;


/**
 * Spring容器工具类
 */
public class SpringContainerSupport {

    public static void putBean(ApplicationContext applicationContext,String beanName,Object object){
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getAutowireCapableBeanFactory();
        Class<?> beanClass = object.getClass();
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
        String className = beanClass.getName();
        if(!StringUtils.hasText(beanName)){
            beanName = className.substring(className.lastIndexOf(".") + 1);
            beanName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1);
        }
        defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
    }

}
