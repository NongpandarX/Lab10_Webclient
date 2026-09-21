package com.example.lab10;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Lab 10 — Spring WebFlux + WebClient
 * CP353002 Principles of Software Design
 *
 * @SpringBootApplication รวม 3 annotation:
 *   - @Configuration          : Spring config class
 *   - @EnableAutoConfiguration : เปิด Auto-config (รวม WebFlux)
 *   - @ComponentScan          : สแกนหา @Component, @RestController ฯลฯ
 *
 * Framework จะรันบน Reactor Netty (non-blocking) แทน Tomcat
 */
@SpringBootApplication
public class Lab10Application {

    public static void main(String[] args) {
        SpringApplication.run(Lab10Application.class, args);
    }
}
