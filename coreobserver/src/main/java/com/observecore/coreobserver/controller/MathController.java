package com.observecore.coreobserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.observecore.coreobserver.myaspect.ObserveDataVessel;
import com.observecore.coreobserver.service.MathsService;

@RestController
public class MathController {
    
    @Autowired
    MathsService mathsService;

    @GetMapping("/math/random-prime")
    @ResponseStatus(HttpStatus.OK)
    @ObserveDataVessel(contextName = "Math | Random Number", transactionIdString = "x-transaction-id")
    public String randomPrime() {
        System.out.println("Math | Random Number | Random Prime Number is generated....");
        double randomNum =  Math.random();
        mathsService.add(5, 6);

        return String.valueOf(randomNum);
    }
}
