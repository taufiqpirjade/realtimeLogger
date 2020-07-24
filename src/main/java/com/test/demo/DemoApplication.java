package com.test.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DemoApplication {
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(DemoApplication.class, args);
	}

}
