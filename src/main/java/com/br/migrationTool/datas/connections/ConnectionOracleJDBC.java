package com.br.migrationTool.datas.connections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.*;

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

    public void close(PreparedStatement ps, ResultSet rs) throws SQLException {
        if (ps != null) {
            ps.close();
        }
        if (rs != null) {
            rs.close();
        }
    }
}
