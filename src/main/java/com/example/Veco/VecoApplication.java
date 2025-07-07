package com.example.Veco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class VecoApplication {

	public static void main(String[] args) {
		SpringApplication.run(VecoApplication.class, args);
	}

}
