## Migration

Notable changes required to upgrade to newer versions of ST-Metrics are detailed below.

### v0.1.0 to v0.2.0

* To continue to make use of the DropWizard Metrics backend, the declaration of the ``TimingAspect``
  bean should be changed to match the way the aspect below is instantiated.
  
```java
@Configuration
public class MyApplicationConfig {

    // Your other configuration would be here.
    
    @Autowired
    private MetricRegistry metricRegistry;

    @Bean
    public TimingAspect myTimingAspect() {
        return new TimingAspect(new MetricSinkDropWizard(metricRegistry));
    }
}
```

* To switch to using the Spring Boot Actuator backend, the declaration of the ``TimingAspect``
  bean to should be changed to match the way the aspect below is instantiated.

```java
@Configuration
public class MyApplicationConfig {

    // Your other configuration would be here.
    
    @Autowired
    private GaugeService gaugeService;

    @Bean
    public TimingAspect myTimingAspect() {
        return new TimingAspect(new MetricSinkSpringBoot(gaugeService));
    }
}
```
