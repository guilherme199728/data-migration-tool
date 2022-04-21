package com.br.migrationTool.service;

import com.br.migrationTool.dto.rest.DataMigrationToolDto;
import com.br.migrationTool.useCase.MigrationUseCase;
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
