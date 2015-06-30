## ST-Metrics

ST-Metrics is a collection of aspects and annotations for recording method timing information in your
Spring Boot application.

### Downloads

Release binaries can be found on [Maven Central](http://search.maven.org/#search|ga|1|g%3A%22com.smartertravel.metrics.aop%22%20AND%20a%3A%22st-metrics%22)

Example of including ST-Metrics in your Maven project:

``` xml
<dependency>
    <groupId>com.smartertravel.metrics.aop</groupId>
    <artifactId>st-metrics</artifactId>
    <version>x.y.z</version>
</dependency>
```

### Usage

Configuring st-metrics should be as simple as adding a new `@Bean` to your existing application `@Configuration`.

``` java
package com.example.myapp;

import com.codahale.metrics.MetricRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.smartertravel.metrics.aop.TimingAspect;

@Configuration
public class MyApplicationConfig {

    // Your other configuration would be here.
    
    @Autowired
    private MetricRegistry metricRegistry;
    
    @Bean
    public TimingAspect timingAspect() {
        return new TimingAspect(metricRegistry);
    }
}
```
    
Then, all you have to do is annotate any method you want to record the timing of.
    
``` java
package com.example.myapp;

import java.util.Optional;

import com.smartertravel.metrics.aop.Timed;

public class UserDaoMysql implements UserDao {

    @Timed
    @Override
    public Optional<User> getUserById(UserId id) {
        return Optional.empty();
    }
}    
```

Now, on every call of `UserDaoMysql.getUserById()` you should see timing results available as part
of the metrics for your application, available at http://localhost:8080/metrics by default.
