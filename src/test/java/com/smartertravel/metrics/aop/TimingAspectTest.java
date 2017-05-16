package com.smartertravel.metrics.aop;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.smartertravel.metrics.aop.TimingAspect.DefaultKeyGenerator;
import com.smartertravel.metrics.aop.backend.MetricSinkDropWizard;
import com.smartertravel.metrics.aop.backend.MetricSinkSpringBoot;
import com.smartertravel.metrics.aop.util.Time;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.actuate.metrics.GaugeService;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class TimingAspectTest {

    interface UserDao {
        @SuppressWarnings("unused")
        boolean userExists(String id);
    }

    static class UserDaoMysql implements UserDao {

        @Timed("mysqlDao.userExists")
        @Override
        public boolean userExists(String id) {
            return false;
        }
    }

    static class UserDaoHystrix implements UserDao {

        @Timed
        @Override
        public boolean userExists(String id) {
            return false;
        }
    }

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private Signature signature;

    @Mock
    private MetricRegistry metricRegistry;

    @Mock
    private GaugeService gaugeService;

    @Mock
    private Timer timer;

    @Mock
    private Time time;

    @Test
    public void testDefaultKeyGeneratorGetKeyAnnotationValue() throws NoSuchMethodException {
        final UserDaoMysql dao = new UserDaoMysql();
        final Method method = dao.getClass().getMethod("userExists", String.class);
        final Timed[] annotations = method.getAnnotationsByType(Timed.class);

        final DefaultKeyGenerator keyGenerator = new DefaultKeyGenerator();
        assertEquals("timer.mysqlDao.userExists", keyGenerator.getKey(joinPoint, dao, annotations[0]));
    }

    @Test
    public void testDefaultKeyGeneratorGetKeyDerivedValue() throws NoSuchMethodException {
        final UserDaoHystrix dao = new UserDaoHystrix();
        final Method method = dao.getClass().getMethod("userExists", String.class);
        final Timed[] annotations = method.getAnnotationsByType(Timed.class);

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("userExists");

        final DefaultKeyGenerator keyGenerator = new DefaultKeyGenerator();
        assertEquals("timer.UserDaoHystrix.userExists", keyGenerator.getKey(joinPoint, dao, annotations[0]));
    }

    @Test
    public void testPerformanceLogDropWizard() throws Throwable {
        final UserDaoHystrix dao = new UserDaoHystrix();
        final Method method = dao.getClass().getMethod("userExists", String.class);
        final Timed[] annotations = method.getAnnotationsByType(Timed.class);

        when(joinPoint.proceed()).thenReturn(true);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("userExists");
        when(metricRegistry.timer(eq("timer.UserDaoHystrix.userExists"))).thenReturn(timer);
        when(time.nanoseconds()).thenReturn(1_000_000L).thenReturn(2_500_000L);

        final TimingAspect aspect = new TimingAspect(new MetricSinkDropWizard(metricRegistry), new DefaultKeyGenerator(), time);
        final Object result = aspect.performanceLog(joinPoint, dao, annotations[0]);

        assertNotNull("Expected non-null result type", result);
        assertTrue("Expected boolean return type from aspect, was " + result.getClass().getName(), result instanceof Boolean);
        verify(timer).update(eq(1_500_000L), eq(TimeUnit.NANOSECONDS));
    }

    @Test
    public void testPerformanceLogDropWizardWithException() throws Throwable {
        final UserDaoHystrix dao = new UserDaoHystrix();
        final Method method = dao.getClass().getMethod("userExists", String.class);
        final Timed[] annotations = method.getAnnotationsByType(Timed.class);

        when(joinPoint.proceed()).thenThrow(IOException.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("userExists");
        when(metricRegistry.timer(eq("timer.UserDaoHystrix.userExists"))).thenReturn(timer);
        when(time.nanoseconds()).thenReturn(1_000_000L).thenReturn(2_500_000L);

        final TimingAspect aspect = new TimingAspect(new MetricSinkDropWizard(metricRegistry), new DefaultKeyGenerator(), time);
        IOException err = null;

        try {
            aspect.performanceLog(joinPoint, dao, annotations[0]);
        } catch (IOException e) {
            err = e;
        }

        assertNotNull("Expected exception to be raised while calling join point", err);
        verify(timer).update(eq(1_500_000L), eq(TimeUnit.NANOSECONDS));
    }

    @Test
    public void testPerformanceLogSpringBoot() throws Throwable {
        final UserDaoHystrix dao = new UserDaoHystrix();
        final Method method = dao.getClass().getMethod("userExists", String.class);
        final Timed[] annotations = method.getAnnotationsByType(Timed.class);

        when(joinPoint.proceed()).thenReturn(true);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("userExists");
        when(time.nanoseconds()).thenReturn(1_000_000L).thenReturn(4_000_000L);

        final TimingAspect aspect = new TimingAspect(new MetricSinkSpringBoot(gaugeService), new DefaultKeyGenerator(), time);
        final Object result = aspect.performanceLog(joinPoint, dao, annotations[0]);

        assertNotNull("Expected non-null result type", result);
        assertTrue("Expected boolean return type from aspect, was " + result.getClass().getName(), result instanceof Boolean);
        verify(gaugeService).submit(eq("timer.UserDaoHystrix.userExists"), eq(3D));
    }

    @Test
    public void testPerformanceLogSpringBootWithException() throws Throwable {
        final UserDaoHystrix dao = new UserDaoHystrix();
        final Method method = dao.getClass().getMethod("userExists", String.class);
        final Timed[] annotations = method.getAnnotationsByType(Timed.class);

        when(joinPoint.proceed()).thenThrow(IOException.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("userExists");
        when(time.nanoseconds()).thenReturn(1_000_000L).thenReturn(4_000_000L);

        final TimingAspect aspect = new TimingAspect(new MetricSinkSpringBoot(gaugeService), new DefaultKeyGenerator(), time);
        IOException err = null;

        try {
            aspect.performanceLog(joinPoint, dao, annotations[0]);
        } catch (IOException e) {
            err = e;
        }

        assertNotNull("Expected exception to be raised while calling join point", err);
        verify(gaugeService).submit(eq("timer.UserDaoHystrix.userExists"), eq(3D));
    }
}
