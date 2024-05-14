package com.observecore.coreobserver.rest;

import java.io.IOException;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapPropagator;
import jakarta.annotation.PostConstruct;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "com.homeobserver.framework.*")
public class OpenTelemetryInterceptor implements ClientHttpRequestInterceptor {

    private final RestTemplateBuilder restTemplateBuilder;

    public OpenTelemetryInterceptor() {
        System.out.println("[CoreObserver-Boot] | OpenTelemetryInterceptor is created");
        this.restTemplateBuilder = new RestTemplateBuilder();
    }

    // public OpenTelemetryInterceptor(RestTemplateBuilder restTemplateBuilder) {
    //     System.out.println("OpenTelemetryInterceptor is created");
    //     this.restTemplateBuilder = restTemplateBuilder;
    // }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        System.out.println("Inside the OpenTelemetryInterceptor");

        Span currentSpan = Span.current();
        // Context context = Context.current().with(currentSpan);

        currentSpan.setAttribute("TYPE", "HTTP REST CLIENT CALL");
        currentSpan.setAttribute("HTTP Call - URL", request.getURI().toString());
        currentSpan.setAttribute("HTTP Call-Method", request.getMethod().toString());

        HttpHeaders headers = request.getHeaders();

        String traceParent = "00" + "-" + currentSpan.getSpanContext().getTraceId() + "-"
                + currentSpan.getSpanContext().getSpanId() + "-01";
        headers.add("traceparent", traceParent);

        System.out.println("About to send from the Rest client traceParent : [" + traceParent + " ]");

        return execution.execute(request, body);
    }
}
