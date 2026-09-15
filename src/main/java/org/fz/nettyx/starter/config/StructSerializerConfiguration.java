package org.fz.nettyx.starter.config;

import org.fz.nettyx.serializer.struct.StructContext;
import org.fz.nettyx.starter.annotation.EnableStructScan;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;


/**
 * @author fengbinbin
 * @version 1.0
 * @since 2025/4/14 23:00
 */

public class StructSerializerConfiguration implements ImportAware {

    private String[] scanBasePackages;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                importMetadata.getAnnotationAttributes(EnableStructScan.class.getName()));
        if (attributes == null) {
            throw new BeanDefinitionStoreException(
                    "annotation " + EnableStructScan.class.getName() + " is not found");
        }

        scanBasePackages = attributes.getStringArray("scanBasePackages");
        if (scanBasePackages.length == 0) {
            scanBasePackages = new String[]{ ClassUtils.getPackageName(importMetadata.getClassName()) };
        }
    }

    @Bean
    public StructContext structSerializer() {
        return new StructContext(scanBasePackages);
    }
}
