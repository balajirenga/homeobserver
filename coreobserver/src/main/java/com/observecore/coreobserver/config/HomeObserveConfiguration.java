package com.observecore.coreobserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.ObservationTextPublisher;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.TracerProvider;
import io.opentelemetry.context.propagation.TextMapPropagator;


@Configuration
@EnableAspectJAutoProxy
// @ComponentScan(basePackages = "com.homeobserver.framework.*")
public class HomeObserveConfiguration {
	@Bean
    public Tracer tracer() {
        // Assuming you have already configured OpenTelemetry with a TracerProvider
        TracerProvider tracerProvider = GlobalOpenTelemetry.getTracerProvider();
		Tracer tracer = tracerProvider.get("your-instrumentation-name", "1.0.0");
         
		System.out.println("[CoreObserver-Boot] | Tracer created  " + tracer.toString());
		return tracer;
    }

}
