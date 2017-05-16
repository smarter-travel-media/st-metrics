package com.smartertravel.metrics.aop.util;

/**
 * Implementation of {@link Time} that defaults to using {@link System} based time.
 */
public class SystemTime implements Time {
    @Override
    public long nanoseconds() {
        return System.nanoTime();
    }
}
