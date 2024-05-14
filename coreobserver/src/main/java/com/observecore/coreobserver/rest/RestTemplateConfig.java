package com.observecore.coreobserver.rest;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.client.RestTemplate;
import com.observecore.coreobserver.rest.OpenTelemetryInterceptor;

import jakarta.annotation.PostConstruct;


@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "com.observecore.coreobserver.*")
public class RestTemplateConfig {
    @PostConstruct
    public void init() {
        System.out.println("[CoreObserver-Boot] | RestTemplateConfig is created");
    }

    @Bean
    public static RestTemplateConfig getConfig() {
        System.out.println("[CoreObserver-Boot] | RestTemplateConfig is created");
        return new RestTemplateConfig();
    }

    @Bean
    public OpenTelemetryInterceptor getOpenTelemetryInterceptor() {
        return new OpenTelemetryInterceptor();
    }

    // @Bean
    // public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
    //     System.out.println("@ the Configuration of the RestTemplate");
    //     RestTemplate restTemplate = new RestTemplate();
    //     restTemplate.setInterceptors(Collections.singletonList(openTelemetryInterceptor));
    //     return restTemplate;
    // }

   // private RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
   @Autowired 
   OpenTelemetryInterceptor openTelemetryInterceptor;    

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        RestTemplate restTemplate = new RestTemplate();
        // System.out.println("openTelemetryInterceptor  is " + getOpenTelemetryInterceptor());
         restTemplate.setInterceptors(Collections.singletonList(getOpenTelemetryInterceptor()));
        // System.out.println("openTelemetryInterceptor  is " + openTelemetryInterceptor);
        //restTemplate.setInterceptors(Collections.singletonList(openTelemetryInterceptor));

        System.out.println("[CoreObserver-Boot] | @ the Configuration of the RestTemplate - Created");
        return restTemplate;
    }

    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder();
    }
}
