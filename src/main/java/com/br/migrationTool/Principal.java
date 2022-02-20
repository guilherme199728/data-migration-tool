package com.br.migrationTool;

import com.br.migrationTool.data.connection.ConnetionOracleJDBC;
import com.br.migrationTool.vo.MigrationVo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Principal {

    public static void main(String[] args) throws SQLException {

        ConnetionOracleJDBC.initDatabaseConnectionPool();

        String initialTableNameForMigration = "ACCOUNT";
        List<String> primaryKeysForMigration = new ArrayList<>();
        primaryKeysForMigration.add("1");
        primaryKeysForMigration.add("2");
        primaryKeysForMigration.add("3");
        primaryKeysForMigration.add("3");

        MigrationVo.setListMigration(initialTableNameForMigration, primaryKeysForMigration);

        ConnetionOracleJDBC.closeDataBaseConnectionPool();

    }
}
