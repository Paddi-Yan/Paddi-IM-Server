package com.paddi.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月27日 16:05:51
 */
@Component
public class SpringBeanUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(SpringBeanUtil.applicationContext == null) {
            SpringBeanUtil.applicationContext = applicationContext;
        }
    }

    private static ApplicationContext getApplicationContext() {
        return SpringBeanUtil.applicationContext;
    }

    public static Object getBean(Class<?> clazz) {
        return SpringBeanUtil.getApplicationContext().getBean(clazz);
    }

    public static Object getBean(String name) {
        return SpringBeanUtil.getApplicationContext().getBean(name);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return SpringBeanUtil.getApplicationContext().getBean(name, clazz);
    }
}
