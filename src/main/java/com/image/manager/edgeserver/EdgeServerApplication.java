package com.image.manager.edgeserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.blockhound.BlockHound;
import reactor.core.scheduler.ReactorBlockHoundIntegration;

@SpringBootApplication
public class EdgeServerApplication {

    public static void main(String[] args) {
//        BlockHound.install();
        SpringApplication.run(EdgeServerApplication.class, args);
    }

}
