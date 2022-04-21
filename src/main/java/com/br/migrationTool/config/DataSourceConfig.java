package com.br.migrationTool.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "megastore.hml.datasource")
    public DataSource megaStoreDataSourceHml() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "megastore.prod.datasource")
    public DataSource megaStoreDataSourceProd() {
        return DataSourceBuilder.create().build();
    }
}
