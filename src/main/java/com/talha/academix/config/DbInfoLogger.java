package com.talha.academix.config;

import java.sql.Connection;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class DbInfoLogger implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DbInfoLogger.class);

    private final DataSource dataSource;
    private final Environment environment;

    public DbInfoLogger(DataSource dataSource, Environment environment) {
        this.dataSource = dataSource;
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        try (Connection c = dataSource.getConnection();
             var stmt = c.createStatement();
             var rs = stmt.executeQuery("select database()")) {

            rs.next();
            String jdbcUrl = c.getMetaData().getURL();
            String jdbcUser = c.getMetaData().getUserName();
            String database = rs.getString(1);

            log.info("Active profiles: {}", String.join(",", environment.getActiveProfiles()));
            log.info("DB JDBC URL: {}", jdbcUrl);
            log.info("DB JDBC USER: {}", jdbcUser);
            log.info("DB SQL DATABASE(): {}", database);
        } catch (Exception e) {
            log.warn("Failed to log database connection info", e);
        }
    }
}
