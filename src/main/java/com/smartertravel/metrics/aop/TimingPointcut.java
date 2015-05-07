package com.smartertravel.metrics.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Class containing a pointcut that is used to identify methods that should
 * have execution time recorded by the {@link TimingAspect}.
 */
@Aspect
public class TimingPointcut {

    /**
     * Pointcut describing methods that have a {@link Timed} annotation
     *
     * @param timed Method level performance tracking
     */
    @Pointcut(value = "@annotation(timed)", argNames = "timed")
    public void performanceLog(Timed timed) {
    }
}
