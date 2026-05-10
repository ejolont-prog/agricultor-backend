package com.example.agricultor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class AgricultorApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgricultorApplication.class, args);
	}

	// Añade esto aquí abajo:
	@Bean
	public WebClient.Builder webClientBuilder() {
		return WebClient.builder();
	}

}