package org.fz.nettyx.starter.config;

import org.fz.nettyx.serializer.schema.SchemaRegistry;
import org.fz.nettyx.starter.annotation.EnableSchemaScan;
import org.junit.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SchemaScanSpringBootIntegrationTest {

    @Test
    public void exposesOneRegistryContainingEverySupportedSchemaFormat() {
        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(TestApplication.class)
                .web(WebApplicationType.NONE)
                .properties("spring.main.banner-mode=off")
                .run()) {
            assertEquals(1, context.getBeansOfType(SchemaRegistry.class).size());

            SchemaRegistry registry = context.getBean(SchemaRegistry.class);
            assertNotNull(registry.require("json.JsonPacket"));
            assertNotNull(registry.require("yaml.YamlPacket"));
            assertNotNull(registry.require("xml.XmlPacket"));
        }
    }

    @SpringBootConfiguration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    @EnableSchemaScan(locations = {
            "schemas/device.json",
            "schemas/device.yml",
            "schemas/device.xml"
    })
    static class TestApplication {
    }
}
