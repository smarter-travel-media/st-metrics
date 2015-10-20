package com.smartertravel.metrics.aop.backend;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.actuate.metrics.GaugeService;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MetricSinkSpringBootTest {

    @Mock
    private GaugeService gaugeService;

    @InjectMocks
    private MetricSinkSpringBoot sink;

    @Test
    public void testTimeNativeUnit() {
        this.sink.time("timer.someMetric", 4, TimeUnit.MILLISECONDS);
        verify(gaugeService).submit("timer.someMetric", 4);
    }

    @Test
    public void testTimeNanosUnit() {
        this.sink.time("timer.someMetric", 2000000, TimeUnit.NANOSECONDS);
        verify(gaugeService).submit("timer.someMetric", 2);
    }
}
