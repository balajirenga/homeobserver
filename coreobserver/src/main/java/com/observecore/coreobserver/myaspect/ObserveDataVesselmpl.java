package com.observecore.coreobserver.myaspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.checkerframework.checker.units.qual.C;
import org.checkerframework.checker.units.qual.s;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import org.springframework.web.bind.annotation.GetMapping;

import com.observecore.coreobserver.config.HomeObserveConfiguration;

import io.micrometer.observation.Observation;
import io.micrometer.observation.Observation.Context;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.ContextKey;

import java.lang.System; // Import the System class

@Aspect
@Component
public class ObserveDataVesselmpl {

    @Autowired
    private Tracer tracer;

    @PostConstruct
    public void init() {
        System.out.println("[CoreObserver-Boot] | ObserveDataVesselmpl | BalajiAspectImpl: PostConstruct tracer is " + tracer);
    }

    @Autowired
    HomeObserveConfiguration homeObserveConfiguration;

    @Around("@annotation(observeDataVessel)")
    public Object observeCurrentMethod(ProceedingJoinPoint jointPoint, ObserveDataVessel observeDataVessel) throws Throwable {
        Span span = createNewSpanAndClose(null, jointPoint);

        observeDataVessel.contextName();
        observeDataVessel.transactionIdString();

        //Context.current().get(observeDataVessel.transactionIdString());
        String value = io.opentelemetry.context.Context.current().get(ContextKey.named(observeDataVessel.transactionIdString()));
        System.out.println("Retrived the context key in someother place : " + value);

        Object result = jointPoint.proceed();
        createNewSpanAndClose(span, jointPoint);
        return result;
    }

    public Span createNewSpanAndClose(Span span, ProceedingJoinPoint jointPoint) {
        System.out.println("BalajiAspectImpl: doSpider");
        String methodName = jointPoint.getSignature().getName();
        Span newSpan = null;
        if (span == null) {
            String url = myURLDetails(jointPoint);

            newSpan = tracer.spanBuilder(methodName + "[" + url + "]").startSpan();
            newSpan.setAttribute("ConqueredSpan", true);

            newSpan.setAttribute("URL", methodName + "[" + url + "]");

            newSpan.makeCurrent();
            

            String spanId = newSpan.getSpanContext().getSpanId();
            String traceId = newSpan.getSpanContext().getTraceId();

            System.out.println("===> BalajiAspectImpl On Start: [" + methodName
                    + "] TraceId : " + traceId + " Span ID:" + newSpan.getSpanContext().getSpanId() + "URL " + "");

        } else {
            span.end();
            System.out.println("===> BalajiAspectImpl On Start: [" + methodName
                    + "] Span ended ");
        }
        return newSpan;
    }

    public String myURLDetails(JoinPoint joinPoint) {
        // Get method signature
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        // Get method
        Method method = signature.getMethod();

        // Check if the method has @GetMapping annotation
        if (method.isAnnotationPresent(GetMapping.class)) {
            GetMapping mappingAnnotation = method.getAnnotation(GetMapping.class);

            // Retrieve the path value from @GetMapping annotation
            String[] path = mappingAnnotation.value();
            System.out.println("Path: " + path[0]); // Assuming only one path is defined

            return path[0];

        }
        return "";

    }

}
