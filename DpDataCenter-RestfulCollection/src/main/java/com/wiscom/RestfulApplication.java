package com.wiscom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class RestfulApplication {
    private static Logger log = LoggerFactory.getLogger(RestfulApplication.class);

    public static void main( String[] args ) {
        SpringApplication.run(RestfulApplication.class,args);
        log.info("RestfulApplication started!");
    }
}
