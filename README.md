# ST-Metrics

[![Build Status](https://travis-ci.org/smarter-travel-media/st-metrics.svg?branch=master)](https://travis-ci.org/smarter-travel-media/st-metrics)
[![Maven Central](https://img.shields.io/maven-central/v/com.smartertravel.metrics.aop/st-metrics.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.smartertravel.metrics.aop%22%20AND%20a%3A%22st-metrics%22)

ST-Metrics is a collection of aspects and annotations for recording method timing information in your
Spring Boot application.

## Deprecated

Note that this library is **deprecated**. Any users are encouraged to migrate to the [Micrometer](http://micrometer.io/) library.

## Downloads

Release binaries can be found on [Maven Central](http://search.maven.org/#search|ga|1|g%3A%22com.smartertravel.metrics.aop%22%20AND%20a%3A%22st-metrics%22)

Example of including ST-Metrics in your Maven project:

``` xml
<dependency>
    <groupId>com.smartertravel.metrics.aop</groupId>
    <artifactId>st-metrics</artifactId>
    <version>x.y.z</version>
</dependency>
```

ST-Metrics can make use to several different backends to send timing metrics to: DropWizard Metrics, Spring
Boot Actuator, or a Statsd client. Depending on which of these you'd like to use, you may have to add another
include to pull in the relevant package.

DropWizard Metrics

``` xml
<dependency>
    <groupId>io.dropwizard.metrics</groupId>
    <artifactId>metrics-core</artifactId>
    <version>x.y.z</version>
</dependency>
```

... or Spring Boot Actuator:

``` xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
    <version>x.y.z</version>
</dependency>
```

... or Statsd Client:

``` xml
<dependency>
    <groupId>com.timgroup</groupId>
    <artifactId>java-statsd-client</artifactId>
    <version>x.y.z</version>
</dependency>
```

## Usage

Configuring st-metrics should be as simple as adding a new `@Bean` to your existing application `@Configuration`.

An example of using the DropWizard Metrics backend.

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

An example of using the Spring Boot Actuator backend.

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

An example of using the Statsd Client backend.

``` java
package com.example.myapp;

import com.timgroup.statsd.StatsDClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.smartertravel.metrics.aop.TimingAspect;
import com.smartertravel.metrics.aop.backend.MetricSinkStatsdClient;

@Configuration
public class MyApplicationConfig {

    // Your other configuration would be here.

    @Autowired
    private StatsDClient; client;

    @Bean
    public TimingAspect timingAspect() {
        return new TimingAspect(new MetricSinkStatsdClient(client));
    }
}
```

Then, all you have to do is annotate any method you want to record the timing of.

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


For more advanced usage, check out the [docs](http://eng.smartertravel.com/st-metrics/).
