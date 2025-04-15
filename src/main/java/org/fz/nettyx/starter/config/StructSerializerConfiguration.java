package org.fz.nettyx.starter.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ClassUtil;
import lombok.RequiredArgsConstructor;
import org.fz.nettyx.serializer.struct.StructSerializerContext;
import org.fz.nettyx.starter.annotation.EnableStructSerializerScan;
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

@RequiredArgsConstructor
public class StructSerializerConfiguration {

    private final ApplicationContext appCtx;

    @Bean
    public StructSerializerContext structSerializer() {
        Collection<?> springBootApplicationMain = appCtx.getBeansWithAnnotation(EnableStructSerializerScan.class).values();

        if (CollUtil.isNotEmpty(springBootApplicationMain)) {
            Class<?> mainClass = springBootApplicationMain.iterator().next().getClass();

            EnableStructSerializerScan structScan =
                    AnnotationUtils.findAnnotation(mainClass, EnableStructSerializerScan.class);

            if (structScan != null) {
                String[] basePackages = defaultIfEmpty(structScan.basePackages(), new String[]{ ClassUtil.getPackage(mainClass) });

                return new StructSerializerContext(basePackages);
            }
        }

        return new StructSerializerContext();
    }
}
