package com.br.migrationTool;

import com.br.migrationTool.data.connection.ConnectionOracleJDBC;
import com.br.migrationTool.useCase.MigrationUseCase;

import java.sql.SQLException;

public class Principal {

    public static void main(String[] args) throws SQLException {

        ConnectionOracleJDBC.initDatabaseConnectionPool();

        MigrationUseCase migrationUseCase = new MigrationUseCase();
        migrationUseCase.start();

        ConnectionOracleJDBC.closeDataBaseConnectionPool();

    }
}
