package com.mrs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MovieReservationSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(MovieReservationSystemApplication.class, args);
    }
}
