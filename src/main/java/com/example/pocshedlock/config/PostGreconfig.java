package com.example.pocshedlock.config;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;

/**
 * Created by DIPU on 3/9/21
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "postgresEntityManagerFactory",
        transactionManagerRef = "postgresTransactionManager",
        basePackages = {"com.example.*"})
public class PostGreconfig {

        @Value("${spring.postgres.persistence.unit}")
        public String persistenceUnit;

        @Value("${spring.jpa.properties.hibernate.dialect}")
        public String hibernateDialect;

        @Value("${spring.datasource.url}")
        private String dataSourceUrl;
        @Value("${spring.datasource.username}")
        private String dataSourceUsername;
        @Value("${spring.datasource.password}")
        private String dataSourcePassword;

        @Primary
        @Bean(value = "primaryDataSource")
        public DataSource primaryDataSource() {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName(hibernateDialect);
            dataSource.setUrl(dataSourceUrl);
            dataSource.setUsername(dataSourceUsername);
            dataSource.setPassword(dataSourcePassword);
            return dataSource;
        }

        @Primary
        @Bean(name = "postgresEntityManagerFactory")
        public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("primaryDataSource") DataSource dataSource) {
            LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
            em.setDataSource(dataSource);
            em.setPackagesToScan("com.example.*");
            em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
            HashMap<String, Object> properties = new HashMap<>();
            properties.put("hibernate.dialect", hibernateDialect);
            em.setJpaPropertyMap(properties);
            em.setPersistenceUnitName(persistenceUnit);

            return em;
        }

        @Primary
        @Bean(name = "postgresTransactionManager")
        public PlatformTransactionManager transactionManager(@Qualifier("postgresEntityManagerFactory")
                                                                     EntityManagerFactory entityManagerFactory) {
            return new JpaTransactionManager(entityManagerFactory);
        }

        @Bean(value = "postgresqlJdbcTemplate")
        public JdbcTemplate jdbcTemplate(@Qualifier("primaryDataSource") DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }

        @Bean
        public LockProvider lockProvider(DataSource dataSource)
        {
            return new JdbcTemplateLockProvider(
                    JdbcTemplateLockProvider.Configuration.builder()
                            .withJdbcTemplate(new JdbcTemplate(dataSource))
                            .usingDbTime() // Works on Postgres, MySQL, MariaDb, MS SQL, Oracle, DB2, HSQL and H2
                            .build()
            );
        }
    }
