a spring boot starter for nettyx


```java
// please use as the following code
@EnableStructScan(basePackages = { "org.nettyx.test" })
@SpringBootApplication
public class NettyxDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(NettyxTestApplication.class, args);
    }

}
```
