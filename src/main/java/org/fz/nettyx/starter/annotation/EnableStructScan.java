package org.fz.nettyx.starter.annotation;

import org.fz.nettyx.starter.config.StructSerializerConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author fengbinbin
 * @version 1.0
 * @since 2025/4/14 23:00
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(StructSerializerConfiguration.class)
public @interface EnableStructScan {
    String[] basePackages() default {};
}
