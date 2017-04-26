package org.csource.quickstart.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * @author SongJian email:1738042258@QQ.COM
 * 自定义processor的处理顺序
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ProcessorOrder {
    int order() default Integer.MAX_VALUE;
}