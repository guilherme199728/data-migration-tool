package com.br.migrationTool.services;

import com.br.migrationTool.dtos.rest.RequestRangeIdsMigrationDto;
import com.br.migrationTool.dtos.rest.RequestSeparateIdsMigrationDto;
import com.br.migrationTool.useCases.MigrationUseCase;
import com.br.migrationTool.validations.MigrationValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class MigrationService {

    @Autowired
    MigrationUseCase migrationUseCase;

    @Autowired
    MigrationValidation migrationValidation;

    public void migrateBySeparateIds(RequestSeparateIdsMigrationDto requestSeparateIdsMigrationDto) throws SQLException {
        migrationValidation.isIdsLimitValid(requestSeparateIdsMigrationDto.getIds());
        migrationUseCase.process(requestSeparateIdsMigrationDto.getTableName(), requestSeparateIdsMigrationDto.getIds());
    }

    public void migrateByRangeIds(RequestRangeIdsMigrationDto requestRangeIdsMigrationDto) throws SQLException {

        List<String> ids = IntStream
            .range(requestRangeIdsMigrationDto.getStartId(), requestRangeIdsMigrationDto.getEndId())
            .mapToObj(String::valueOf).toList();

        migrationValidation.isIdsLimitValid(ids);
        migrationUseCase.process(requestRangeIdsMigrationDto.getTableName(), ids);
    }
}
