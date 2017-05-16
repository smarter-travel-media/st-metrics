package com.smartertravel.metrics.aop.util;

/**
 * Interface for isolating us from the system clock for easier testing.
 */
public interface Time {
    /**
     * @return Current value of JVM nanosecond time source.
     */
    long nanoseconds();
}
