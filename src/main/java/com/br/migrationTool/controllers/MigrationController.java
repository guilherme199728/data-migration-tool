package com.br.migrationTool.controllers;

import com.br.migrationTool.configs.MessagePropertiesReader;
import com.br.migrationTool.dtos.rest.BasicHttpResponse;
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
import java.text.MessageFormat;

@Controller
@RequestMapping(value = "/dataMigrationTool")
public class MigrationController {

    @Autowired
    MigrationService migrationService;

    @Autowired
    MessagePropertiesReader messagePropertiesReader;

    @PostMapping("/migrateBySeparateIds")
    public ResponseEntity<BasicHttpResponse> migrateBySeparateIds(
        @RequestBody RequestSeparateIdsMigrationDto body
    ) throws SQLException {
        migrationService.migrateBySeparateIds(body);

        BasicHttpResponse basicHttpResponse = new BasicHttpResponse();
        basicHttpResponse.setMessage(
            MessageFormat.format(messagePropertiesReader.getMessage("migrate.success"), body.getTableName())
        );

        return new ResponseEntity<>(basicHttpResponse, HttpStatus.OK);
    }

    @PostMapping("/migrateByRangeIds")
    public ResponseEntity<BasicHttpResponse> migrateByRangeIds(
        @RequestBody RequestRangeIdsMigrationDto body
    ) throws SQLException {
        migrationService.migrateByRangeIds(body);

        BasicHttpResponse basicHttpResponse = new BasicHttpResponse();
        basicHttpResponse.setMessage(
            MessageFormat.format(messagePropertiesReader.getMessage("migrate.success"), body.getTableName())
        );

        return new ResponseEntity<>(basicHttpResponse, HttpStatus.OK);
    }
}
