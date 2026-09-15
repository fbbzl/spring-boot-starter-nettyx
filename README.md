# Spring Boot Starter NettyX

Registers NettyX struct and schema serialization contexts as Spring beans.

```java
@EnableStructScan(scanBasePackages = "org.nettyx.test")
@EnableSchemaScan(locations = {
        "nettyx/device.json",
        "nettyx/geo.yml",
        "nettyx/message.xml"
})
@SpringBootApplication
public class NettyxDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(NettyxDemoApplication.class, args);
    }

}
```

`@EnableStructScan` registers a `StructContext` bean. `@EnableSchemaScan` registers a
`SchemaRegistry` bean and accepts JSON, YAML/YML, and XML schema locations supported by
NettyX. Locations may be classpath resources, `classpath:` prefixed resources, or file paths.
