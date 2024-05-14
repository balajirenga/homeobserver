package com.observecore.coreobserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import jakarta.annotation.PostConstruct;

@Configuration
public class CoreObserverConfiguration {

    @Autowired
	BaseConfig basicConfig;

    @PostConstruct
    public void initialize() {
       setup();
    }

    public void setup() {
	    String otLPEndPoint = basicConfig.getOtlpEndpoint();
        String secretToken = basicConfig.getSecretToken();
        String ServiceName = basicConfig.getServiceName();

        System.out.println("[CoreObserver-Boot] | CoreObserverConfiguration  otLPEndPoint " + otLPEndPoint + " secretToken " + secretToken + " ServiceName " + ServiceName);

       // set service name on all OTel signals
		Resource resource = Resource.getDefault().merge(Resource.create(
            Attributes.of(ResourceAttributes.SERVICE_NAME, ServiceName,ResourceAttributes.SERVICE_VERSION,"1.0",ResourceAttributes.DEPLOYMENT_ENVIRONMENT,"BalajiHome")));

        SdkTracerProvider sdkTracerProvider2 = extractedHTTPTracerProvider2(otLPEndPoint,secretToken,resource);
        
            // create sdk object and set it as global
        OpenTelemetrySdk sdk = OpenTelemetrySdk.builder()
            .setTracerProvider(sdkTracerProvider2)
            .setPropagators(ContextPropagators
                    .create(W3CTraceContextPropagator.getInstance()))
            .build();

        GlobalOpenTelemetry.set(sdk);

        // Add hook to close SDK, which flushes logs
        Runtime.getRuntime().addShutdownHook(new Thread(sdk::close));
        System.err.println("[CoreObserver-Boot] | CoreObserverConfiguration  - Basic Open Telemetry Setup..........");
    }

    private static SdkTracerProvider extractedHTTPTracerProvider2(String otLPEndPoint, String secretToken, Resource resource) {
		// init OTel trace provider with export to OTLP
		SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
				.setResource(resource).setSampler(Sampler.alwaysOn())
				// add span processor to add baggage as span attributes
				.addSpanProcessor(BatchSpanProcessor.builder(OtlpHttpSpanExporter
						.builder()
						.setEndpoint(otLPEndPoint).addHeader("Authorization", "Bearer " + secretToken)
						.build()).build())
				.build();
        System.err.println("[CoreObserver-Boot] | CoreObserverConfiguration  - Succesfully built the http tracer provider..........");
		return sdkTracerProvider;
	}

}