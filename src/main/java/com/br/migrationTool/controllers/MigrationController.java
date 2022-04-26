package com.br.migrationTool.controllers;

import com.br.migrationTool.dtos.rest.RequestRangeIdsMigrationDto;
import com.br.migrationTool.dtos.rest.RequestSeparateIdsMigrationDto;
import com.br.migrationTool.services.MigrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.SQLException;

@Controller
@RequestMapping(value = "/dataMigrationTool")
public class MigrationController {

    @Autowired
    MigrationService migrationService;

    @PostMapping("/migrateBySeparateIds")
    public ResponseEntity<String> migrateBySeparateIds(@RequestBody RequestSeparateIdsMigrationDto body) throws SQLException {
        migrationService.migrateBySeparateIds(body);

        return new ResponseEntity<>(body.getTableName() + " Migrado com sucesso!", HttpStatus.OK);
    }

    @PostMapping("/migrateByRangeIds")
    public ResponseEntity<String> migrateByRangeIds(@RequestBody RequestRangeIdsMigrationDto body) throws SQLException {
        migrationService.migrateByRangeIds(body);

        return new ResponseEntity<>(body.getTableName() + " Migrado com sucesso!", HttpStatus.OK);
    }
}
