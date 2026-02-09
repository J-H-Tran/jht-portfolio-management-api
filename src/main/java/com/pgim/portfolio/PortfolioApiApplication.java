package com.pgim.portfolio;

import com.pgim.portfolio.service.TradeService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

//@Configuration
//@EnableAutoConfiguration
//@ComponentScan
@SpringBootApplication(scanBasePackages = "com.pgim.portfolio")
@EnableJpaRepositories(basePackages = "com.pgim.portfolio.repository")
@EnableSpringDataWebSupport//(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class PortfolioApiApplication /*implements CommandLineRunner*/ {
    private final TradeService tradeService;

    public PortfolioApiApplication(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    public static void main(String[] args) {
        SpringApplication.run(PortfolioApiApplication.class, args);
    }
}