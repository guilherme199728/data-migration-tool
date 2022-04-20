package com.br.migrationTool.data.connection;

import com.br.migrationTool.propertie.PropertiesLoaderImpl;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionOracleJDBC {

    private static HikariDataSource dataSourceProd;
    private static HikariDataSource dataSourceHomolog;
    private static Connection connectionProd;
    private static Connection connectionHomolog;

    public static Connection getConnection(boolean isProd) throws SQLException {
        return isProd ? getConnectionProd() : getConnectionHomolog();
    }

    private static Connection getConnectionProd() throws SQLException {
        if (connectionProd == null) {
            connectionProd = dataSourceProd.getConnection();
        }

        return connectionProd;
    }

    private static Connection getConnectionHomolog() throws SQLException {
        if (connectionHomolog == null) {
            connectionHomolog = dataSourceHomolog.getConnection();
        }

        return connectionHomolog;
    }

    public static void initDatabaseConnectionPool() {

        // String urlJdbcProd = "jdbc:oracle:thin:@" +
        //     PropertiesLoaderImpl.getValue("database.prod.host") + ":" +
        //     PropertiesLoaderImpl.getValue("database.prod.port") + ":" +
        //     PropertiesLoaderImpl.getValue("database.prod.database");

        String urlJdbcProd = PropertiesLoaderImpl.getValue("database.prod.host");

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
