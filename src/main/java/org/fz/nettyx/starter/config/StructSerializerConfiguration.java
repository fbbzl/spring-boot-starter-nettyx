package org.fz.nettyx.starter.config;

import lombok.RequiredArgsConstructor;
import org.fz.nettyx.serializer.struct.StructSerializerContext;
import org.fz.nettyx.starter.annotation.EnableStructScan;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AnnotationUtils;


/**
 * @author fengbinbin
 * @version 1.0
 * @since 2025/4/14 23:00
 */

@RequiredArgsConstructor
public class StructSerializerConfiguration {

    private final ApplicationContext appCtx;

    @Bean
    public StructSerializerContext structSerializer() {
        EnableStructScan enableStructScan =
                AnnotationUtils.getAnnotation(AopUtils.getTargetClass(appCtx), EnableStructScan.class);

        return enableStructScan != null ?
               new StructSerializerContext(enableStructScan.basePackages()) :
               new StructSerializerContext();
    }
}
