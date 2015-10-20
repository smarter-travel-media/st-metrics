package com.smartertravel.metrics.aop.backend;

import org.springframework.boot.actuate.metrics.GaugeService;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of a {@link MetricSink} that writes to a Spring Boot Actuator
 * {@link GaugeService} instance.
 */
public class MetricSinkSpringBoot implements MetricSink {

    private final GaugeService gaugeService;

    public MetricSinkSpringBoot(GaugeService gaugeService) {
        this.gaugeService = Objects.requireNonNull(gaugeService);
    }

    @Override
    public void time(String name, long duration, TimeUnit unit) {
        gaugeService.submit(name, unit.toMillis(duration));
    }
}
