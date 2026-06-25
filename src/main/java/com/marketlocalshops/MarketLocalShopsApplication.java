package com.marketlocalshops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MarketLocalShopsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketLocalShopsApplication.class, args);
    }
}
