package com.avra.qa.common.util.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
@ComponentScan(basePackages = {"com.avra.qa"})
@PropertySource("classpath:test.properties")
public class AppConfig {

    @Bean
    public DataSource postgresDataSource(@Value("${postgresUrl}") String postgresUrl,
                                         @Value("${postgresUser}") String postgresUser,
                                         @Value("${postgresPass}") String postgresPass) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(postgresUrl);
        hikariConfig.setUsername(postgresUser);
        hikariConfig.setPassword(postgresPass);
        hikariConfig.setAutoCommit(true);
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        return new HikariDataSource(hikariConfig);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource postgresDataSource) {
        return new JdbcTemplate(postgresDataSource);
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    public ConnectionFactory connectionFactory(@Value("${rabbitmqUrl}") String rabbitmqUrl,
                                               @Value("${rabbitmqPort}") Integer rabbitmqPort,
                                               @Value("${rabbitmqUser}") String rabbitmqUser,
                                               @Value("${rabbitmqPass}") String rabbitmqPass) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(rabbitmqUrl);
        connectionFactory.setPort(rabbitmqPort);
        connectionFactory.setUsername(rabbitmqUser);
        connectionFactory.setPassword(rabbitmqPass);
        return connectionFactory;
    }

    @Bean
    public ConnectionFactory bsConnectionFactory(@Value("${rabbitmqUrl}") String rabbitmqUrl,
                                                 @Value("${rabbitmqPort}") Integer rabbitmqPort,
                                                 @Value("${rabbitmqUser}") String rabbitmqUser,
                                                 @Value("${rabbitmqPass}") String rabbitmqPass) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setVirtualHost("<virtual-host-name>");
        connectionFactory.setAddresses(rabbitmqUrl);
        connectionFactory.setPort(rabbitmqPort);
        connectionFactory.setUsername(rabbitmqUser);
        connectionFactory.setPassword(rabbitmqPass);
        return connectionFactory;
    }

    @Configuration
    @PropertySource("classpath:env-local.properties")
    @Profile("local")
    public static class LocalConfig {

    }

    @Configuration
    @PropertySource("classpath:env-gitlab-ci.properties")
    @Profile("gitlab-ci")
    public static class GitLabCiConfig {
    }
}
