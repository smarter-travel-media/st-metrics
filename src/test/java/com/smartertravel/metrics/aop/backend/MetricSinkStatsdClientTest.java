package com.smartertravel.metrics.aop.backend;

import com.timgroup.statsd.StatsDClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MetricSinkStatsdClientTest {

    @Mock
    private StatsDClient client;

    @InjectMocks
    private MetricSinkStatsdClient sink;

    @Test
    public void testTimeNativeUnit() {
        this.sink.time("timer.someMetric", 4, TimeUnit.MILLISECONDS);
        verify(client).recordExecutionTime("timer.someMetric", 4);
    }

    @Test
    public void testTimeNanosUnit() {
        this.sink.time("timer.someMetric", 2000000, TimeUnit.NANOSECONDS);
        verify(client).recordExecutionTime("timer.someMetric", 2);
    }
}
