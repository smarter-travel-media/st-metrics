package com.smartertravel.metrics.aop;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Aspect that records (successful or error) method execution time in milliseconds
 * using a {@link Timer} and optional {@link KeyGenerator}.
 * <p>
 * This aspect uses the {@link TimingPointcut} pointcut which will track method execution
 * for all methods annotated with the {@link Timed} annotation.
 * <p>
 * Note that his aspect will prefix all metric keys created by the {@code KeyGenerator} with
 * "timer." since we make use of Dropwizard metrics library. The Spring Boot documentation
 * indicates that all we need to do to make use of Dropwizard metric types is to add the
 * appropriate prefix.
 * <p>
 * This class is thread safe.
 *
 * @see <a href="http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready-metrics">Spring Boot Metrics Docs</a>
 * @see <a href="http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready-code-hale-metrics">Spring Boot Dropwizard Metrics Docs</a>
 */
@Aspect
public class TimingAspect {

    private final MetricRegistry metricRegistry;
    private final KeyGenerator keyGenerator;

    /**
     * Construct a new timing aspect that will record method execution time using the given
     * {@link MetricRegistry} and a default key generator.
     * <p>
     * The default key generator has the following properties:
     * <ul>
     * <li>
     * If a key is specified as the value of the {@link Timed} annotation that key will be used.
     * </li>
     * <li>
     * Otherwise, the key will be of the form "$CLASS.$METHOD" where "$CLASS" is the short class
     * name of the bean that the {@code Timed} method is part of, and "$METHOD" is the name of the
     * {@code Timed} method.
     * </li>
     * </ul>
     *
     * @param metricRegistry Service for constructing new {@code Timer} instances
     * @throws NullPointerException If {@code gaugeService} is null
     */
    public TimingAspect(MetricRegistry metricRegistry) {
        this(metricRegistry, new DefaultKeyGenerator());
    }

    /**
     * Construct a new timing aspect that will record method execution time using the given
     * {@link MetricRegistry} and {@link KeyGenerator}.
     *
     * @param metricRegistry Service for constructing new {@code Timer} instances
     * @param keyGenerator   {@code KeyGenerator} implementation that will create keys for metrics
     *                       recorded with a {@code Timer} instance based on the join point, object
     *                       being instrumented, and {@code Timed} annotation.
     * @throws NullPointerException If {@code metricRegistry} or {@code keyGenerator} is null
     */
    public TimingAspect(MetricRegistry metricRegistry, KeyGenerator keyGenerator) {
        this.metricRegistry = Objects.requireNonNull(metricRegistry);
        this.keyGenerator = Objects.requireNonNull(keyGenerator);
    }


    @Around(value = "target(bean) && TimingPointcut.performanceLog(timed)", argNames = "joinPoint,bean,timed")
    public Object performanceLog(ProceedingJoinPoint joinPoint, Object bean, Timed timed) throws Throwable {
        final String key = "timer." + keyGenerator.getKey(joinPoint, bean, timed);
        final Timer timer = metricRegistry.timer(key);
        final long start = System.nanoTime();

        try {
            return joinPoint.proceed();
        } finally {
            final long end = System.nanoTime();
            timer.update(end - start, TimeUnit.NANOSECONDS);
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
     * "SomeDaoClass.doSomething" or "SomeServiceClient.getThing".
     * <p>
     * Note that the {@code TimingAspect} will add a "timer." prefix to the metric key
     * no matter what. This is done to to make use of Dropwizard timing histograms.
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
     * specified as part of the {@code Timed} annotation.
     */
    // VisibleForTesting
    static class DefaultKeyGenerator implements KeyGenerator {
        @Override
        public String getKey(JoinPoint jp, Object bean, Timed timed) {
            final String annotationValue = timed.value();
            if (annotationValue.length() != 0) {
                return annotationValue;
            }

            return bean.getClass().getSimpleName() + "." + jp.getSignature().getName();
        }
    }

}
