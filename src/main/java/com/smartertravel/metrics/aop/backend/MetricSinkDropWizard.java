package com.smartertravel.metrics.aop.backend;

import com.codahale.metrics.MetricRegistry;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of a {@link MetricSink} that writes to a DropWizard Metrics
 * {@link MetricRegistry} instance.
 */
public class MetricSinkDropWizard implements MetricSink {

    private final MetricRegistry registry;

    public MetricSinkDropWizard(MetricRegistry registry) {
        this.registry = Objects.requireNonNull(registry);
    }

    @Override
    public void time(String name, long duration, TimeUnit unit) {
        registry.timer(name).update(duration, unit);
    }
}
