package org.fz.nettyx.starter.config;

import cn.hutool.core.util.ClassUtil;
import org.fz.nettyx.serializer.struct.StructSerializerContext;
import org.fz.nettyx.starter.annotation.EnableStructScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Collection;

import static cn.hutool.core.util.ArrayUtil.defaultIfEmpty;


/**
 * @author fengbinbin
 * @version 1.0
 * @since 2025/4/14 23:00
 */

public class StructSerializerConfiguration {

    @Bean
    public StructSerializerContext structSerializer(ApplicationContext appCtx) {
        Collection<?> springBootApplicationMain =
                appCtx.getBeansWithAnnotation(EnableStructScan.class).values();

        Class<?> mainClass = springBootApplicationMain.iterator().next().getClass();

        EnableStructScan structScan =
                AnnotationUtils.findAnnotation(mainClass, EnableStructScan.class);

        if (structScan != null) {
            String[] basePackages = defaultIfEmpty(structScan.basePackages(),
                                                   new String[]{ ClassUtil.getPackage(mainClass) });
            return new StructSerializerContext(basePackages);
        }

        throw new IllegalArgumentException("annotation " + EnableStructScan.class + " is not found, application is: [" + appCtx + "]");
    }
}
