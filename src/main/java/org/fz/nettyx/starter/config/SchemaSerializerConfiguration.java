package org.fz.nettyx.starter.config;

import org.fz.nettyx.serializer.schema.SchemaRegistry;
import org.fz.nettyx.starter.annotation.EnableSchemaScan;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates the shared NettyX schema registry declared by {@link EnableSchemaScan}.
 *
 * @author fengbinbin
 * @version 1.0
 * @since 2026/9/15
 */
public class SchemaSerializerConfiguration implements ImportBeanDefinitionRegistrar {

    private static final String SCHEMA_REGISTRY_BEAN_NAME = "schemaRegistry";
    private static final String LOCATIONS_ATTRIBUTE =
            SchemaSerializerConfiguration.class.getName() + ".locations";

    @Override
    public void registerBeanDefinitions(
            AnnotationMetadata importingClassMetadata,
            @NonNull BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                importingClassMetadata.getAnnotationAttributes(EnableSchemaScan.class.getName()));
        if (attributes == null) {
            throw new BeanDefinitionStoreException(
                    "annotation " + EnableSchemaScan.class.getName() + " is not found");
        }

        List<String> locations = schemaLocations(registry);
        for (String location : attributes.getStringArray("locations")) {
            if (!locations.contains(location)) {
                locations.add(location);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> schemaLocations(BeanDefinitionRegistry registry) {
        if (registry.containsBeanDefinition(SCHEMA_REGISTRY_BEAN_NAME)) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(SCHEMA_REGISTRY_BEAN_NAME);
            Object locations = beanDefinition.getAttribute(LOCATIONS_ATTRIBUTE);
            if (locations instanceof List<?>) {
                return (List<String>) locations;
            }
            throw new BeanDefinitionStoreException(
                    "bean name " + SCHEMA_REGISTRY_BEAN_NAME + " is already in use");
        }

        List<String> locations = new ArrayList<>();
        RootBeanDefinition beanDefinition = new RootBeanDefinition(SchemaRegistry.class);
        beanDefinition.setAttribute(LOCATIONS_ATTRIBUTE, locations);
        beanDefinition.setRole(BeanDefinition.ROLE_APPLICATION);
        beanDefinition.setInstanceSupplier(
                () -> SchemaRegistry.load(locations.toArray(String[]::new)));
        registry.registerBeanDefinition(SCHEMA_REGISTRY_BEAN_NAME, beanDefinition);
        return locations;
    }
}
