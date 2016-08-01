package com.smartertravel.metrics.aop.backend;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MetricSinkDropWizardTest {

    @Mock
    private MetricRegistry metricRegistry;

    @Mock
    private Timer timer;

    @InjectMocks
    private MetricSinkDropWizard sink;

    @Test
    public void testTimeDurationAndUnitPropagated() {
        when(metricRegistry.timer(eq("timer.someKey"))).thenReturn(timer);

        sink.time("timer.someKey", 100, TimeUnit.MILLISECONDS);

        verify(timer).update(100, TimeUnit.MILLISECONDS);
    }
}
