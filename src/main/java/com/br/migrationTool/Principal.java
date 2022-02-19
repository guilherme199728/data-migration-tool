package com.br.migrationTool;

import com.br.migrationTool.data.connection.ConnetionOracleJDBC;
import com.br.migrationTool.data.dao.TableReferencesDao;

import java.sql.Connection;
import java.sql.SQLException;

public class Principal {
    public static void main(String[] args) throws SQLException {

        ConnetionOracleJDBC.initDatabaseConnectionPool();
        Connection con = ConnetionOracleJDBC.getConnectionProd();

        TableReferencesDao.getParentTablesFromTableName("PROD", "ACCOUNT", con);

        ConnetionOracleJDBC.closeDataBaseConnectionPool();

    }
}
