package com.picko.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PickoApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PickoApiApplication.class, args);
	}

}

