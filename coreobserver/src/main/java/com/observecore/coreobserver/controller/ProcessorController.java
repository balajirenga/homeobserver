package com.observecore.coreobserver.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProcessorController {
    
    @GetMapping("/justReceiveData")
    @ResponseStatus(HttpStatus.OK)
    public String doReceiveData(){
        System.out.println("at the receive data method..");
        return "Done";
    }
}
