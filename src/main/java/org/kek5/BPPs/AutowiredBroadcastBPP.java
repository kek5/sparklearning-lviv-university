package org.kek5.BPPs;

import lombok.SneakyThrows;
import org.apache.spark.api.java.JavaSparkContext;
import org.kek5.Annotations.AutowiredBroadcast;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

/**
 * Created by kek5 on 4/16/17.
 */
@Component
@ComponentScan(basePackages = {"org.kek5"})
public class AutowiredBroadcastBPP implements BeanPostProcessor {
    @Autowired
    private ApplicationContext context;

    @Autowired
    private JavaSparkContext sc;



    @Override
    @SneakyThrows
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(AutowiredBroadcast.class)) {
                ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                Class<?> typeOfBeanToInject = (Class<?>) genericType.getActualTypeArguments()[0];
                field.setAccessible(true);
                Object beanToInject = context.getBean(typeOfBeanToInject);
                field.set(bean,sc.broadcast(beanToInject));
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
