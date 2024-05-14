package com.observecore.coreobserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.observecore.coreobserver.myaspect.ObserveDataVessel;
import com.observecore.coreobserver.rest.CoreObserveRestClient;

@Component
public class MathsService {

    @Autowired
    CoreObserveRestClient restClient;
    
    @ObserveDataVessel(contextName = "Math | Addition", transactionIdString = "x-transaction-id")
    public void add(int a, int b) {
        System.out.println("Addition of " + a + " and " + b + " is " + (a + b));
        restClient.makeAuthenticatedRequest("HelloWorld".getBytes(), null);
    }

}
