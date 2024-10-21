package com.udacity.pricing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Creates a Spring Boot Application to run the Pricing Service.
 * TODO: Convert the application from a REST API to a microservice.
 */
@SpringBootApplication
//@EnableEurekaClient
//Upon some investigation, unlike the practice from the course, this EnableEurekaClient annotation is not required anymore
//Simply including spring-cloud-starter-netflix-eureka-client in the dependencies will enable the client
//TODO come back to this later and figure out if this comes back to bite me
public class PricingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PricingServiceApplication.class, args);
    }

}
