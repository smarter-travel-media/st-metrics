package com.smartertravel.metrics.aop.backend;

import com.timgroup.statsd.StatsDClient;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of a {@link MetricSink} that writes to {@link StatsDClient}
 * instance.
 */
public class MetricSinkStatsdClient implements MetricSink {

    private final StatsDClient client;

    public MetricSinkStatsdClient(StatsDClient client) {
        this.client = Objects.requireNonNull(client);
    }

    @Override
    public void time(String name, long duration, TimeUnit unit) {
        client.recordExecutionTime(name, unit.toMillis(duration));
    }
}
