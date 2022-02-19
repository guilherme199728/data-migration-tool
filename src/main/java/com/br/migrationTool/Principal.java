package com.br.migrationTool;

import com.br.migrationTool.data.connection.ConnetionOracleJDBC;
import com.br.migrationTool.propertie.PropertiesLoaderImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Principal {
    public static void main(String[] args) throws SQLException {

        PropertiesLoaderImpl.getValue("database.prod.user");

        ConnetionOracleJDBC.initDatabaseConnectionPool();
        Connection con = ConnetionOracleJDBC.getConnectionProd();

        PreparedStatement ps = con.prepareStatement("SELECT * FROM ACCOUNT");
        ResultSet resultSet = ps.executeQuery();
        while (resultSet.next()) {
            System.out.println(resultSet.getString("AVAIL_BALANCE"));
        }

        Connection con1 = ConnetionOracleJDBC.getConnectionHomolog();
        PreparedStatement ps1 = con1.prepareStatement("SELECT * FROM DEPARTMENT");
        ResultSet resultSet1 = ps1.executeQuery();
        while (resultSet1.next()) {
            System.out.println(resultSet1.getString("NAME"));
        }

        ConnetionOracleJDBC.closeDataBaseConnectionPool();

    }
}
