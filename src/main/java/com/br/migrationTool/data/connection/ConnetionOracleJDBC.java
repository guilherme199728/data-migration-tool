package com.br.migrationTool.data.connection;

import com.br.migrationTool.propertie.PropertiesLoaderImpl;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnetionOracleJDBC {

    private static HikariDataSource dataSourceProd;
    private static HikariDataSource dataSourceHomolog;

    public static Connection getConnectionProd() throws SQLException {
        return dataSourceProd.getConnection();
    }

    public static Connection getConnectionHomolog() throws SQLException {
        return dataSourceHomolog.getConnection();
    }

    public static void initDatabaseConnectionPool() {

        String urlJdbcProd = "jdbc:oracle:thin:@" +
            PropertiesLoaderImpl.getValue("database.prod.host") + ":" +
            PropertiesLoaderImpl.getValue("database.prod.port") + ":" +
            PropertiesLoaderImpl.getValue("database.prod.database");

        dataSourceProd = new HikariDataSource();
        dataSourceProd.setJdbcUrl(urlJdbcProd);
        dataSourceProd.setUsername(PropertiesLoaderImpl.getValue("database.prod.user"));
        dataSourceProd.setPassword(PropertiesLoaderImpl.getValue("database.prod.password"));

        String urlJdbcHomolog = "jdbc:oracle:thin:@" +
            PropertiesLoaderImpl.getValue("database.homolog.host") + ":" +
            PropertiesLoaderImpl.getValue("database.homolog.port") + ":" +
            PropertiesLoaderImpl.getValue("database.homolog.database");

        dataSourceHomolog = new HikariDataSource();
        dataSourceHomolog.setJdbcUrl(urlJdbcHomolog);
        dataSourceHomolog.setUsername(PropertiesLoaderImpl.getValue("database.homolog.user"));
        dataSourceHomolog.setPassword(PropertiesLoaderImpl.getValue("database.homolog.password"));
    }

    public static void closeDataBaseConnectionPool() {
        dataSourceProd.close();
        dataSourceHomolog.close();
    }
}
