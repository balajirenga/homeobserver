package com.observecore.coreobserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
public class BaseConfig {

    @Value("${sdk.observe.service.name}")
	@Getter @Setter private String serviceName;

	@Value("${sdk.observe.service.exporter.url}")
	@Getter @Setter private String otlpEndpoint;

	@Value("${sdk.observe.service.exporter.url.token}")
	@Getter @Setter private String secretToken;

}
