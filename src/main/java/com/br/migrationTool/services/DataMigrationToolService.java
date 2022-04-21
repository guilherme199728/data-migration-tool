package com.br.migrationTool.services;

import com.br.migrationTool.dtos.rest.DataMigrationToolDto;
import com.br.migrationTool.useCases.MigrationUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class DataMigrationToolService {

    @Autowired
    MigrationUseCase migrationUseCase;

    public void migrate(DataMigrationToolDto dataMigrationToolDto) throws SQLException {
        migrationUseCase.start(dataMigrationToolDto);
    }

}
