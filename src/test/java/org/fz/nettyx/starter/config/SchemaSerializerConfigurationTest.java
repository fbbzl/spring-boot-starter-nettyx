package org.fz.nettyx.starter.config;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.fz.nettyx.serializer.schema.SchemaRegistry;
import org.fz.nettyx.serializer.schema.SchemaSerializer;
import org.fz.nettyx.starter.annotation.EnableSchemaScan;
import org.junit.Test;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

public class SchemaSerializerConfigurationTest {

    @Test
    public void loadsJsonYamlAndXmlSchemasIntoSpringContext() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(TestApplication.class)) {
            SchemaRegistry registry = context.getBean(SchemaRegistry.class);

            assertNotNull(registry.require("json.JsonPacket"));
            assertNotNull(registry.require("yaml.YamlPacket"));
            assertNotNull(registry.require("xml.XmlPacket"));
        }
    }

    @Test
    public void combinesLocationsDeclaredByMultipleConfigurationClasses() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(JsonSchemaApplication.class,
                                                            XmlSchemaApplication.class)) {
            SchemaRegistry registry = context.getBean(SchemaRegistry.class);

            assertNotNull(registry.require("json.JsonPacket"));
            assertNotNull(registry.require("xml.XmlPacket"));
        }
    }

    @Test
    public void roundTripsJsonYamlAndXmlPayloads() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(TestApplication.class)) {
            SchemaRegistry registry = context.getBean(SchemaRegistry.class);

            assertRoundTrip(registry, "json.JsonPacket");
            assertRoundTrip(registry, "yaml.YamlPacket");
            assertRoundTrip(registry, "xml.XmlPacket");
        }
    }

    @Test
    public void ignoresDuplicateSchemaLocations() {
        DefaultListableBeanFactory registry = new DefaultListableBeanFactory();
        SchemaSerializerConfiguration registrar = new SchemaSerializerConfiguration();
        AnnotationMetadata metadata = AnnotationMetadata.introspect(JsonSchemaApplication.class);

        registrar.registerBeanDefinitions(metadata, registry);
        registrar.registerBeanDefinitions(metadata, registry);

        assertEquals(1, registry.getBeanDefinitionCount());
    }

    @Test
    public void rejectsSchemaRegistryNameCollision() {
        DefaultListableBeanFactory registry = new DefaultListableBeanFactory();
        registry.registerBeanDefinition("schemaRegistry", new RootBeanDefinition(Object.class));

        assertThrows(BeanDefinitionStoreException.class,
                     () -> new SchemaSerializerConfiguration().registerBeanDefinitions(
                             AnnotationMetadata.introspect(JsonSchemaApplication.class), registry));
    }

    @Test
    public void rejectsImportWithoutEnableSchemaScan() {
        assertThrows(BeanDefinitionStoreException.class,
                     () -> new SchemaSerializerConfiguration().registerBeanDefinitions(
                             AnnotationMetadata.introspect(WithoutAnnotation.class),
                             new DefaultListableBeanFactory()));
    }

    private static void assertRoundTrip(SchemaRegistry registry, String schemaName) {
        ByteBuf buffer = Unpooled.buffer();
        try {
            SchemaSerializer.toByteBuf(registry, schemaName, Map.of("id", 42), buffer);
            assertEquals(Map.of("id", 42), SchemaSerializer.toStruct(registry, schemaName, buffer));
            assertEquals(0, buffer.readableBytes());
        } finally {
            buffer.release();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @EnableSchemaScan(locations = {
            "schemas/device.json",
            "schemas/device.yml",
            "schemas/device.xml"
    })
    static class TestApplication {
    }

    @Configuration(proxyBeanMethods = false)
    @EnableSchemaScan(locations = "schemas/device.json")
    static class JsonSchemaApplication {
    }

    @Configuration(proxyBeanMethods = false)
    @EnableSchemaScan(locations = "schemas/device.xml")
    static class XmlSchemaApplication {
    }

    static class WithoutAnnotation {
    }
}
