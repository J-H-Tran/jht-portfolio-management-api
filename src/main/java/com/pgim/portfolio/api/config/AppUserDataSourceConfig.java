package com.pgim.portfolio.api.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import javax.sql.DataSource;
import static com.pgim.portfolio.api.constant.CommonConstants.APPUSER_DATASOURCE;
import static com.pgim.portfolio.api.constant.CommonConstants.APPUSER_ENTITY_MANAGER;
import static com.pgim.portfolio.api.constant.CommonConstants.APPUSER_ENTITY_PACKAGE;
import static com.pgim.portfolio.api.constant.CommonConstants.APPUSER_REPOSITORY_PACKAGE;
import static com.pgim.portfolio.api.constant.CommonConstants.APPUSER_SPRING_DATASOURCE;
import static com.pgim.portfolio.api.constant.CommonConstants.APPUSER_TRANSACTION_MANAGER;
import static com.pgim.portfolio.api.util.SqlScriptExecutor.executeSqlScript;

@Configuration
@EnableJpaRepositories(
    basePackages = APPUSER_REPOSITORY_PACKAGE,
    entityManagerFactoryRef = APPUSER_ENTITY_MANAGER,
    transactionManagerRef = APPUSER_TRANSACTION_MANAGER
)
@EntityScan(basePackages = APPUSER_ENTITY_PACKAGE) // Marks AppUser entity as belonging to AppUserDataSource
public class AppUserDataSourceConfig {
    // Configuration for user_db (AppUser and AppUserRole entities)
    @Bean(name = APPUSER_DATASOURCE)
    @ConfigurationProperties(prefix = APPUSER_SPRING_DATASOURCE)
    public DataSource appUserDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = APPUSER_ENTITY_MANAGER)
    public LocalContainerEntityManagerFactoryBean appUserEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier(APPUSER_DATASOURCE) DataSource appUserDataSource
    ) {
        return builder
                .dataSource(appUserDataSource)
                .packages(APPUSER_ENTITY_PACKAGE)
                .persistenceUnit("appuser")
                .build();
    }

    @Bean(name = APPUSER_TRANSACTION_MANAGER)
    public PlatformTransactionManager appUserTransactionManager(
            @Qualifier(APPUSER_ENTITY_MANAGER)
            EntityManagerFactory appUserEntityManagerFactory
    ) {
        return new JpaTransactionManager(appUserEntityManagerFactory);
    }

    @Bean
    public ApplicationRunner appUserDbInitializer(
            @Qualifier(APPUSER_DATASOURCE) DataSource appUserDataSource
    )  {
        return args -> {
            JdbcTemplate template = new JdbcTemplate(appUserDataSource);
            executeSqlScript(template, "schema-appuser.sql");
            executeSqlScript(template, "data-appuser.sql");
        };
    }
}