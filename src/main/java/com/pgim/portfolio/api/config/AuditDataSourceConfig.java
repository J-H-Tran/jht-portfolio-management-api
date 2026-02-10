package com.pgim.portfolio.api.config;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * SecondaryDataSourceConfig demonstrates how to configure a second database connection in Spring Boot.
 * This is useful for scenarios like audit logging, reporting, or integrating with legacy systems.
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
        basePackages = "com.pgim.portfolio.repository.audit", // package for secondary repos
        entityManagerFactoryRef = "auditEntityManagerFactory",
        transactionManagerRef = "auditTransactionManager"
)
@EntityScan(basePackages = "com.pgim.portfolio.domain.entity.audit")
public class AuditDataSourceConfig {
    /**
     * Defines the secondary DataSource bean.
     * Properties are loaded from application.yml (spring.audit-datasource).
     */
    @Bean(name = "auditDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.audit")
    public DataSource auditDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * Defines the EntityManagerFactory for the secondary DB.
     * Points to the entity package for audit entities.
     */
    @Bean(name = "auditEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean auditEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("auditDataSource") DataSource auditDataSource
    ) {
        return builder
                .dataSource(auditDataSource)
                .packages("com.pgim.portfolio.domain.entity.audit")
                .persistenceUnit("audit")
                .build();
    }

    /**
     * Defines the TransactionManager for the secondary DB.
     * Ensures transactions are managed separately from the primary DB.
     */
    @Bean(name = "auditTransactionManager")
    public PlatformTransactionManager auditTransactionManager(
            @Qualifier("auditEntityManagerFactory")
            EntityManagerFactory auditEntityManagerFactory
    ) {
        return new JpaTransactionManager(auditEntityManagerFactory);
    }

}