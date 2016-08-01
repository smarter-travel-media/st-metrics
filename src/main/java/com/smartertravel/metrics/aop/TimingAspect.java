package com.smartertravel.metrics.aop;

import com.smartertravel.metrics.aop.backend.MetricSink;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Aspect that records (successful or error) method execution time in milliseconds
 * using one of several {@link MetricSink} backends and optional {@link KeyGenerator}.
 * <p>
 * This aspect uses the {@link TimingPointcut} pointcut which will track method execution
 * for all methods annotated with the {@link Timed} annotation.
 * <p>
 * This class is thread safe.
 *
 * @see <a href="http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready-metrics">Spring Boot Metrics Docs</a>
 * @see <a href="http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready-code-hale-metrics">Spring Boot Dropwizard Metrics Docs</a>
 */
@Aspect
public class TimingAspect {

    private final MetricSink metricSink;
    private final KeyGenerator keyGenerator;

    /**
     * Construct a new timing aspect that will record method execution time using the given
     * {@link MetricSink} implementation and a default key generator.
     * <p>
     * The default key generator has the following properties:
     * <ul>
     * <li>
     * If a key is specified as the value of the {@link Timed} annotation that key will be used, prefixed
     * with the string "timer.".
     * </li>
     * <li>
     * Otherwise, the key will be of the form "timer.$CLASS.$METHOD" where "$CLASS" is the short class
     * name of the bean that the {@code Timed} method is part of, and "$METHOD" is the name of the
     * {@code Timed} method.
     * </li>
     * </ul>
     *
     * @param metricSink Metric backend to submit timings to
     * @throws NullPointerException If {@code metricSink} is null
     */
    public TimingAspect(MetricSink metricSink) {
        this(metricSink, new DefaultKeyGenerator());
    }

    /**
     * Construct a new timing aspect that will record method execution time using the given
     * {@link MetricSink} implementation and {@link KeyGenerator}.
     *
     * @param metricSink   Metric backend to submit timings to
     * @param keyGenerator {@code KeyGenerator} implementation that will create keys for metrics
     *                     recorded with a {@code Timer} instance based on the join point, object
     *                     being instrumented, and {@code Timed} annotation.
     * @throws NullPointerException If {@code metricSink} or {@code keyGenerator} is null
     */
    public TimingAspect(MetricSink metricSink, KeyGenerator keyGenerator) {
        this.metricSink = Objects.requireNonNull(metricSink);
        this.keyGenerator = Objects.requireNonNull(keyGenerator);
    }

    @Around(value = "target(bean) && TimingPointcut.performanceLog(timed)", argNames = "joinPoint,bean,timed")
    public Object performanceLog(ProceedingJoinPoint joinPoint, Object bean, Timed timed) throws Throwable {
        final String key = keyGenerator.getKey(joinPoint, bean, timed);
        final long start = System.nanoTime();

        try {
            return joinPoint.proceed();
        } finally {
            final long end = System.nanoTime();
            metricSink.time(key, end - start, TimeUnit.NANOSECONDS);
        }
    }

    /**
     * Interface that allows for customizing the way that metric keys are generated
     * for {@code Timed} methods.
     * <p>
     * Implementations will have access to the join point, object that is annotated,
     * and the annotation itself. Implementations are not required to use all or any
     * of this parameters (but obviously you should be using <em>some</em> of them).
     * <p>
     * Metrics keys are expected to be several period '.' separated values. For example
     * "timer.SomeDaoClass.doSomething" or "timer.SomeServiceClient.getThing".
     * <p>
     * Prefixing metrics with the string "timer." may cause the metric to be treated in
     * a special manner by a particular backend. For example, DropWizard treats metrics
     * with keys that start with "timer." as, well, timers instead of counters or
     * gauges. However, it is not required for implementations to do this.
     * <p>
     * Implementations must be thread safe.
     */
    public interface KeyGenerator {
        /**
         * Get an appropriate metric key based on a join point, annotated object, and
         * the annotation itself.
         *
         * @param jp    Join point for the aspect that is being used.
         * @param bean  Object that the annotated method belongs to.
         * @param timed Annotation used for the method that is being timed
         * @return Key to record the metric under.
         */
        String getKey(JoinPoint jp, Object bean, Timed timed);
    }

    /**
     * Default implementation of a {@code KeyGenerator} that generates a key based
     * on the bean class name and annotated method name or, optionally, the key
     * specified as part of the {@code Timed} annotation. In either case, the string
     * "timer." will be prefixed to the key.
     */
    // VisibleForTesting
    static class DefaultKeyGenerator implements KeyGenerator {
        @Override
        public String getKey(JoinPoint jp, Object bean, Timed timed) {
            final String annotationValue = timed.value();
            if (annotationValue.length() != 0) {
                return "timer." + annotationValue;
            }

            return "timer." + bean.getClass().getSimpleName() + "." + jp.getSignature().getName();
        }
    }

}
