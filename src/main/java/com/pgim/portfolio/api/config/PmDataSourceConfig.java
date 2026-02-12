package com.pgim.portfolio.api.config;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import static com.pgim.portfolio.api.util.SqlScriptExecutor.executeSqlScript;

/**
 * PrimaryDataSourceConfig demonstrates how to configure a second database connection in Spring Boot.
 * This is useful for scenarios like pm logging, reporting, or integrating with legacy systems.
 *
 * Step-by-step walkthrough:
 * 1. Add secondary datasource properties in application.yml under spring.datasource.secondary.
 * 2. Define a DataSource bean for the secondary DB using @ConfigurationProperties.
 * 3. Create an EntityManagerFactory bean for the secondary DB, specifying the entity package.
 * 4. Create a TransactionManager bean for the secondary DB.
 * 5. Use @EnableJpaRepositories to point to the repository package for the secondary DB.
 * 6. Use @EntityScan to specify the entity package for the secondary DB.
 *
 * Each bean is named uniquely to avoid conflicts with the primary datasource beans.
 * This config allows you to use two separate databases in one Spring Boot application.
 */
@Configuration
@EnableJpaRepositories(
        basePackages = "com.pgim.portfolio.repository.pm", // package for secondary repos
        entityManagerFactoryRef = "pmEntityManagerFactory",
        transactionManagerRef = "pmTransactionManager"
)
@EntityScan(basePackages = "com.pgim.portfolio.domain.entity.pm")
public class PmDataSourceConfig {
    /**
     * Defines the secondary DataSource bean.
     * Properties are loaded from application.yml (spring.datasource).
     */
    @Bean(name = "pmDataSource")
    @Primary // Mark this as the default
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource pmDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * Defines the EntityManagerFactory for the secondary DB.
     * Points to the entity package for pm entities.
     */
    @Primary
    @Bean(name = "pmEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean pmEntityManagerFactory(
            EntityManagerFactoryBuilder builder
    ) {
        return builder
                .dataSource(pmDataSource())
                .packages("com.pgim.portfolio.domain.entity.pm")
                .persistenceUnit("pm")
                .build();
    }

    /**
     * Defines the TransactionManager for the secondary DB.
     * Ensures transactions are managed separately from the primary DB.
     */
    @Primary
    @Bean(name = "pmTransactionManager")
    public PlatformTransactionManager pmTransactionManager(
            @Qualifier("pmEntityManagerFactory")
            EntityManagerFactory pmEntityManagerFactory
    ) {
        return new JpaTransactionManager(pmEntityManagerFactory);
    }

    @Bean
    public ApplicationRunner pmDataInitializer(@Qualifier("pmDataSource") DataSource pmDataSource) {
        return args -> {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(pmDataSource);
            executeSqlScript(jdbcTemplate, "schema-pm.sql");
            executeSqlScript(jdbcTemplate, "data-pm.sql");
        };
    }
}