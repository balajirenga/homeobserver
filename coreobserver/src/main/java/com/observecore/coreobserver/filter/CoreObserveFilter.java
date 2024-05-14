package com.observecore.coreobserver.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.observecore.coreobserver.config.HomeObserveConfiguration;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.ContextKey;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapPropagator;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class CoreObserveFilter implements Filter {

    @Autowired
    HomeObserveConfiguration homeObserveConfiguration;

    // Second option on Filter..
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("CoreObserveFilter: doFilter Method.... " + homeObserveConfiguration.tracer());
        // Tracer tracer = GlobalOpenTelemetry.getTracer("ROOT_TRACER");
        Tracer tracer = homeObserveConfiguration.tracer();
        TextMapPropagator propagator = GlobalOpenTelemetry.getPropagators().getTextMapPropagator();

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String url = httpServletRequest.getRequestURI();

        httpServletRequest.getHeaderNames().asIterator().forEachRemaining(header -> {
            System.out.println("Header Name: " + header);
            System.out.println("Header Value: " + httpServletRequest.getHeader(header));
        });

        Context extractedContext = propagator.extract(Context.current(),
                httpServletRequest, HttpServletRequestTextMapGetter.INSTANCE);
        System.out.println("Extracted Content : " + extractedContext.toString());

        System.out.println("At the Filter Method : " + url);
        Span span = tracer.spanBuilder("HTTP Incoming Request RootDefaultFilter[" + url + "]")
                .setSpanKind(SpanKind.SERVER)
                .setParent(extractedContext)
                .startSpan();

        //setContext(httpServletRequest, extractedContext);        

        try (Scope scope = span.makeCurrent()) {
            System.out.println("Filter ---> Span created...." + span.getSpanContext().getSpanId() + " Trace ID: "
                    + span.getSpanContext().getTraceId());

            // Add attributes to the span
            span.setAttribute("URL", url);
            span.setAttribute("Location", "CoreObserveFilter");

            chain.doFilter(request, response);
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR);
            throw e;
        } finally {
            span.end();
            System.out.println("End the Filter Method : " + url);
        }
    }

    private Map<String, String> extractHeaders(HttpServletRequest httpServletRequest) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, httpServletRequest.getHeader(headerName));
        }
        return headers;
    }

    // private void setContext(HttpServletRequest httpServletRequest, Context extractedContext) {
    //     Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
    //     while (headerNames.hasMoreElements()) {
    //         String headerName = headerNames.nextElement();
    //         extractedContext.with(ContextKey.named(headerName),httpServletRequest.getHeader(headerName));
    //         extractedContext.put(ContextKey.named(headerName),httpServletRequest.getHeader(headerName));
    //     }
    // }
    // other methods
}