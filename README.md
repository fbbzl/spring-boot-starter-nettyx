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

## Schema usage

Place the schema files under `src/main/resources/nettyx`.

`device.json`:

```json
{
  "namespace": "device",
  "structs": [
    {
      "name": "Device",
      "fields": [
        { "name": "id", "type": "cint" }
      ]
    }
  ]
}
```

`geo.yml`:

```yaml
namespace: geo
structs:
  - name: GpsPoint
    fields:
      - name: latitude
        type: cdouble
      - name: longitude
        type: cdouble
```

`message.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<structs namespace="message">
    <struct name="Message">
        <field name="code" type="cint"/>
    </struct>
</structs>
```

Inject the registered `SchemaRegistry` and use the fully qualified schema name to serialize
and deserialize values:

```java
@Service
public class DeviceSerializer {

    private final SchemaRegistry registry;

    public DeviceSerializer(SchemaRegistry registry) {
        this.registry = registry;
    }

    public Map<String, Object> roundTrip(int id) {
        ByteBuf buffer = Unpooled.buffer();
        try {
            SchemaSerializer.toByteBuf(
                    registry,
                    "device.Device",
                    Map.of("id", id),
                    buffer
            );
            return SchemaSerializer.toStruct(registry, "device.Device", buffer);
        } finally {
            buffer.release();
        }
    }
}
```
