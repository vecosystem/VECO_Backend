package com.example.Veco;

import com.example.Veco.domain.external.config.GitHubConfig;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
@EnableConfigurationProperties(GitHubConfig.class)
public class VecoApplication {

	public static void main(String[] args) {
		SpringApplication.run(VecoApplication.class, args);
	}

}
