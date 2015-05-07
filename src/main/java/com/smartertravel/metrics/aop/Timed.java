package com.smartertravel.metrics.aop;

import com.smartertravel.metrics.aop.TimingAspect.KeyGenerator;

import java.lang.annotation.*;

/**
 * Indicates that the execution time of a method should be recorded.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Timed {

    /**
     * Return optional metric name override
     * <p>
     * NOTE: This value may or may not be used depending on the {@link KeyGenerator}
     * being used by the {@link TimingAspect}.
     *
     * @return Optional metric name override
     */
    String value() default "";
}
