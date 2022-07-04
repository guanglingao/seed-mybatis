package org.seed.mybatis.springboot.scatter;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class DataScatterBeanRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder dataSourceRoutingAdvisorBuilder = BeanDefinitionBuilder.genericBeanDefinition(DataSourceRoutingAdvisor.class);
        registry.registerBeanDefinition("SeedScatterDataSourceRoutingAdvisor", dataSourceRoutingAdvisorBuilder.getBeanDefinition());
        BeanDefinitionBuilder redirectToTableInterceptorBuilder = BeanDefinitionBuilder.genericBeanDefinition(RedirectToTableInterceptor.class);
        registry.registerBeanDefinition("SeedScatterRedirectToTableInterceptor", redirectToTableInterceptorBuilder.getBeanDefinition());
    }

}
