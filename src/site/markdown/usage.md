## Usage

The sections below will walk you through how to use the ST-Metrics library in your Spring Boot
application.

### Dependency

First, if you're using Maven, you'll need to include it in your project.

``` xml
<dependency>
    <groupId>com.smartertravel.metrics.aop</groupId>
    <artifactId>st-metrics</artifactId>
    <version>x.y.z</version>
</dependency>
```

The ST-Metrics library in turn, depends on The AspectJ package (`org.aspectj:aspectjweaver`), which
should be pulled into your Spring Boot application by the `org.springframework.boot:spring-boot-starter-aop`
package, and on the DropWizard Metrics library. You can pull this in with the
`io.dropwizard.metrics:metrics-core` package.

For other dependency management systems, see [Dependency Information](dependency-info.html).

### Basic usage

Configuring ST-Metrics should be as simple as adding a new `@Bean` to your existing application
`@Configuration`. As previously mentioned, ST-Metrics can use either DropWizard Metrics or Spring
Boot Actuator as a backend. Depending on which library you've chosen to include, configuration
of ST-Metrics will be a little different.

#### Configuring DropWizard Metrics Backend

First, you'll want the DropWizard Metrics library included in your project.

``` xml
<dependency>
    <groupId>io.dropwizard.metrics</groupId>
    <artifactId>metrics-core</artifactId>
    <version>x.y.z</version>
</dependency>
```

Next, create a ``TimingAspect`` instance that will use a DropWizard Metrics backend.

``` java
package com.example.myapp;

import com.codahale.metrics.MetricRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.smartertravel.metrics.aop.TimingAspect;
import com.smartertravel.metrics.aop.backend.MetricSinkDropWizard;

@Configuration
public class MyApplicationConfig {

    // Your other configuration would be here.
    
    @Autowired
    private MetricRegistry metricRegistry;
    
    @Bean
    public TimingAspect timingAspect() {
        return new TimingAspect(new MetricSinkDropWizard(metricRegistry));
    }
}
```

#### Configuring Spring Boot Actuator Backend

If you're using Spring Boot for your project, you'll probably just want to include the [Spring Boot
Starter Actuator](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready) library.

``` xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
    <version>x.y.z</version>
</dependency>
```

Next, create a ``TimingAspect`` instance that will use a Spring Boot Actuator backend.

``` java
package com.example.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.smartertravel.metrics.aop.TimingAspect;
import com.smartertravel.metrics.aop.backend.MetricSinkSpringBoot;

@Configuration
public class MyApplicationConfig {

    // Your other configuration would be here.
    
    @Autowired
    private GaugeService gaugeService;
    
    @Bean
    public TimingAspect timingAspect() {
        return new TimingAspect(new MetricSinkSpringBoot(gaugeService));
    }
}
```

#### Usage In Your Application Code

After you've configured the aspect appropriately for your desired metrics backend, all you have to do is
annotate any public method you want to record the timing of.

**NOTE** - Only `public` methods can be annotated and timed.
    
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

### Advanced usage

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

import com.codahale.metrics.MetricRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.smartertravel.metrics.aop.TimingAspect;
import com.smartertravel.metrics.aop.backend.MetricSinkDropWizard;

@Configuration
public class MyApplicationConfig {

    // Your other configuration would be here...
    
    @Autowired
    private MetricRegistry metricRegistry;
    
    @Bean
    public TimingAspect timingAspect() {
        return new TimingAspect(new MetricSinkDropWizard(metricRegistry), new MyAppKeyGenerator());
    }
}
```

Then, just use the `@Timed` annotation as usual.
