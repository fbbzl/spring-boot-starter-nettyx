package org.fz.nettyx.starter.config;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.fz.nettyx.serializer.basic.c.signed.cint;
import org.fz.nettyx.serializer.struct.StructContext;
import org.fz.nettyx.serializer.struct.StructSerializer;
import org.fz.nettyx.serializer.struct.annotation.Struct;
import org.fz.nettyx.starter.annotation.EnableStructScan;
import org.junit.Test;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotationMetadata;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class StructSerializerConfigurationTest {

    @Test
    public void usesConfiguredScanBasePackages() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(ExplicitPackagesApplication.class)) {
            StructContext structContext = context.getBean(StructContext.class);

            assertArrayEquals(new String[]{ "org.fz.nettyx.starter.annotation" },
                              structContext.getBasePackages());
            assertTrue(context.containsBean("structSerializer"));
        }
    }

    @Test
    public void defaultsToImportingClassPackage() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(DefaultPackageApplication.class)) {
            StructContext structContext = context.getBean(StructContext.class);

            assertArrayEquals(new String[]{ "org.fz.nettyx.starter.config" },
                              structContext.getBasePackages());
        }
    }

    @Test
    public void serializesAndDeserializesStructValues() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(DefaultPackageApplication.class)) {
            context.getBean(StructContext.class);
            Packet packet = new Packet();
            packet.setId(new cint(42));
            ByteBuf buffer = Unpooled.buffer();
            try {
                StructSerializer.toByteBuf(packet, buffer);
                Packet decoded = StructSerializer.toStruct(Packet.class, buffer);

                assertEquals(Integer.valueOf(42), decoded.getId().value());
                assertEquals(0, buffer.readableBytes());
            } finally {
                buffer.release();
            }
        }
    }

    @Test
    public void rejectsImportWithoutEnableStructScan() {
        StructSerializerConfiguration configuration = new StructSerializerConfiguration();

        assertThrows(BeanDefinitionStoreException.class,
                     () -> configuration.setImportMetadata(AnnotationMetadata.introspect(WithoutAnnotation.class)));
    }

    @Configuration(proxyBeanMethods = false)
    @EnableStructScan(scanBasePackages = "org.fz.nettyx.starter.annotation")
    static class ExplicitPackagesApplication {
    }

    @Configuration(proxyBeanMethods = false)
    @EnableStructScan
    static class DefaultPackageApplication {
    }

    static class WithoutAnnotation {
    }

    @Struct(endian = Struct.Endian.BE)
    public static class Packet {
        private cint id;

        public Packet() {
        }

        public cint getId() {
            return id;
        }

        public void setId(cint id) {
            this.id = id;
        }
    }
}
