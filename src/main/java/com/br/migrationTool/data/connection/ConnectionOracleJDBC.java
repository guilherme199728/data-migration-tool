package com.br.migrationTool.data.connection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
public class ConnectionOracleJDBC {

    @Autowired
    @Qualifier("megaStoreDataSourceHml")
    DataSource dataSourceHomolog;

    @Autowired
    @Qualifier("megaStoreDataSourceProd")
    DataSource dataSourceProd;

    Connection connectionProd;
    Connection connectionHomolog;

    public Connection getConnection(boolean isProd) throws SQLException {
        return isProd ? getConnectionProd() : getConnectionHomolog();
    }

    private Connection getConnectionProd() throws SQLException {
        if (connectionProd == null) {
            connectionProd = dataSourceProd.getConnection();
        }

        return connectionProd;
    }

    private Connection getConnectionHomolog() throws SQLException {
        if (connectionHomolog == null) {
            connectionHomolog = dataSourceHomolog.getConnection();
        }

        return connectionHomolog;
    }
}
