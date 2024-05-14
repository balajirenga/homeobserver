package com.observecore.coreobserver.myaspect;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ObserveDataVessel {
    String contextName() default "";
    String transactionIdString() default "";
    String transactionIdLocation() default "Header";
}