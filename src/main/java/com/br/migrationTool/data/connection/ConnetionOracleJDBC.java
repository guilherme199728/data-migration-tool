package com.br.migrationTool.data.connection;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnetionOracleJDBC {

    private static HikariDataSource dataSourceProd;
    private static HikariDataSource dataSourceHomolog;

    public static Connection getConnectionProd() {
        try {
            return dataSourceProd.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Connection getConnectionHomolog() {
        try {
            return dataSourceHomolog.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void initDatabaseConnectionPoolProd() {
        // TODO: trocar por properties
        dataSourceProd = new HikariDataSource();
        dataSourceProd.setJdbcUrl("jdbc:oracle:thin:@localhost:1521:ORCLCDB");
        dataSourceProd.setUsername("PROD");
        dataSourceProd.setPassword("PROD");
    }

    public static void closeDataBaseConnectionPoolProd() {
        dataSourceProd.close();
    }

    public static void initDatabaseConnectionPoolHomolog() {
        // TODO: trocar por properties
        dataSourceHomolog = new HikariDataSource();
        dataSourceHomolog.setJdbcUrl("jdbc:oracle:thin:@localhost:1521:ORCLCDB");
        dataSourceHomolog.setUsername("HOMOLOG");
        dataSourceHomolog.setPassword("HOMOLOG");
    }

    public static void closeDataBaseConnectionPoolhomolog() {
        dataSourceHomolog.close();
    }
}
