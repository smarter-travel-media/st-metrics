# ST-Metrics

ST-Metrics is a collection of aspects and annotations for recording method timing information in your
Spring Boot application.

**WARNING** This is not production-ready software (it's not even on Maven Central yet!)

## How to use st-metrics

### Dependency

First, you'll need to include it in your project.

``` xml
<dependency>
    <groupId>com.smartertravel.metrics.aop</groupId>
    <artifactId>st-metrics</artifactId>
    <version>x.y.z</version>
</dependency>
```

### Simple usage

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

### More advanced usage

If the default settings of st-metrics work for you, great! If you'd like to customize the names that
timings are recorded under, you can do that too. We'll do that below.

#### Fixed timing keys

If you'd always like to record timings for a particular method under the same key, you can do that just
by specifying the key as an argument to the `@Timed` annotation. 

``` java
package com.example.myapp;

import java.util.List;
import java.util.ArrayList;

import com.smartertravel.metrics.aop.Timed;

public class UserDaoMysql implements UserDao {

    @Timed("UserDao.allUserBootstrap")
    public List<User> getAllUsers() {
        return new ArrayList<>();
    }
}
```

Whenever the `.getAllUsers()` method is executed, the timing will be recorded under the key 
`timer.UserDao.allUserBootstrap`, instead of the key it would otherwise be recorded under,
`timer.UserDaoMysql.getAllUsers`.

#### Dynamic timing keys

What if you want record timings under keys dynamically, but in a different way than it's done
by default? For this, we'll implement our own version of a `KeyGenerator`. The `KeyGenerator`
interface is how the `TimingAspect` determines what key to record timings under. The interface
is pretty simple: all it has to do is return a `String` based on the join point that is annotated
by the `@Timed` annotation.

``` java
package com.example.myapp;

import org.aspectj.lang.JoinPoint;

import com.smartertravel.metrics.aop.Timed;
import com.smartertravel.metrics.aop.TimingAspect.KeyGenerator;

public class MyAppKeyGenerator implements KeyGenerator {

    @Override
    public String getKey(JoinPoint jp, Object bean, Timed timed) {
        return "myAwesomeApp." + bean.getClass().getSimpleName() + "." + jp.getSignature().getName();
    }
}
```
        
Then, you just need to tell the `TimingAspect` to use your `KeyGenerator` in your `@Configuration`.
        
``` java
package com.example.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.smartertravel.metrics.aop.TimingAspect;

@Configuration
public class MyApplicationConfig {

    // Your other configuration would be here...
    
    @Autowired
    private GaugeService gaugeService;
    
    @Bean
    public TimingAspect timingAspect() {
        return new TimingAspect(gaugeService, new MyAppKeyGenerator());
    }
}
```

Then, just use the `@Timed` annotation as usual.

## Who should use st-metrics?

Teams at SmarterTravel are already using st-metrics in internal projects. It should also be useful for
anyone running a Spring Boot application.

If you are *not* running a Spring Boot application, st-metrics probably isn't very useful for you.
