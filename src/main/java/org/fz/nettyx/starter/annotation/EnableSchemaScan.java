package org.fz.nettyx.starter.annotation;

import org.fz.nettyx.starter.config.SchemaSerializerConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables a NettyX schema registry backed by JSON, YAML/YML, or XML schema files.
 *
 * @author fengbinbin
 * @version 1.0
 * @since 2026/9/15
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SchemaSerializerConfiguration.class)
public @interface EnableSchemaScan {

    /**
     * Schema locations accepted by NettyX, including classpath resources and file paths.
     *
     * @return schema locations to load
     */
    String[] locations();
}
