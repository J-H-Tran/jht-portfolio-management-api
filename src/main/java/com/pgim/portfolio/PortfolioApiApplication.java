package com.pgim.portfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.pgim.portfolio")
@EnableJpaRepositories(basePackages = "com.pgim.portfolio.repository")
public class PortfolioApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(PortfolioApiApplication.class, args);
	}
}