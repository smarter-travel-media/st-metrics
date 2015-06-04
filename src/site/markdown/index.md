## ST-Metrics

ST-Metrics is a collection of aspects and annotations for recording method timing information in your
Spring Boot application.

First, you'll need to include it in your project.

``` xml
<dependency>
    <groupId>com.smartertravel.metrics.aop</groupId>
    <artifactId>st-metrics</artifactId>
    <version>x.y.z</version>
</dependency>
```

Configuring st-metrics should be as simple as adding a new `@Bean` to your existing application `@Configuration`.

``` java
package com.example.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.smartertravel.metrics.aop.TimingAspect;

@Configuration
public class MyApplicationConfig {

    // Your other configuration would be here.
    
    @Autowired
    private GaugeService gaugeService;
    
    @Bean
    public TimingAspect timingAspect() {
        return new TimingAspect(gaugeService);
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

