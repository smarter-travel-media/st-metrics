package com.smartertravel.metrics.aop.backend;

import com.smartertravel.metrics.aop.TimingAspect;

import java.util.concurrent.TimeUnit;

/**
 * Interface for recording method timing information under a particular name.
 * <p>
 * This interface exists to separate method timing done by {@link TimingAspect} from
 * the underlying system or library actually used to record the timing. For example,
 * in a Spring Boot 1.3+ project, we would use {@code GaugeService} for recording timings
 * and have Spring push these timings to various backends (Statsd, JMX, Redis, etc.). In
 * a non-Spring Boot project, we could use the DropWizard Metrics {@code MetricRegistry}
 * library to push timings to various backends that it supports (Graphite, JMX, logs, etc.).
 */
public interface MetricSink {

    /**
     * Submit execution time of a method to some metrics aggregator backend.
     *
     * @param name     Full key for the metrics to be recorded under
     * @param duration Total method execution time
     * @param unit     Unit for method execution time
     */
    void time(String name, long duration, TimeUnit unit);

}
