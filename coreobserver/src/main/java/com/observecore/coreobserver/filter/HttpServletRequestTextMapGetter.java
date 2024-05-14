package com.observecore.coreobserver.filter;

import java.util.Collections;

import org.springframework.lang.Nullable;

import io.opentelemetry.context.propagation.TextMapGetter;
import jakarta.servlet.http.HttpServletRequest;

public class HttpServletRequestTextMapGetter implements TextMapGetter<HttpServletRequest> {

    public static final HttpServletRequestTextMapGetter INSTANCE = new HttpServletRequestTextMapGetter();

    @Override
    public Iterable<String> keys(HttpServletRequest carrier) {
        return Collections.list(carrier.getHeaderNames());
    }

    @Nullable
    @Override
    public String get(@Nullable HttpServletRequest carrier, String key) {
        return carrier.getHeader(key);
    }
}
