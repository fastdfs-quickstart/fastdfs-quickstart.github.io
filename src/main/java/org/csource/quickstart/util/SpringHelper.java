package org.csource.quickstart.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringHelper implements ApplicationContextAware {

    private static ApplicationContext ac;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringHelper.ac = applicationContext;
    }

    public static <T> T getBean(Class<T> requiredType) {
        return SpringHelper.ac.getBean(requiredType);
    }

    public static <T> T getBean(Class<T> requiredType, String beanName) {
        return SpringHelper.ac.getBean(beanName, requiredType);
    }

    public static <T> List<T> getBeansOfType(Class<T> requiredType) {
        List<T> result = new ArrayList<T>();
        Map<String, T> beans = SpringHelper.ac.getBeansOfType(requiredType);
        if (beans != null && beans.size() > 0) {
            for (Map.Entry<String,T> entry : beans.entrySet()){
                result.add(entry.getValue());
            }
        }
        return result;
    }

}
