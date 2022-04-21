package com.br.migrationTool.controlers;

import com.br.migrationTool.dto.rest.DataMigrationToolDto;
import com.br.migrationTool.service.DataMigrationToolService;
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
public class DataMigrationToolController {

    @Autowired
    DataMigrationToolService dataMigrationToolService;

    @PostMapping()
    public ResponseEntity<String> migrate(@RequestBody DataMigrationToolDto body) throws SQLException {
        dataMigrationToolService.migrate(body);

        return new ResponseEntity<>(body.getTableName() + " Migrado!", HttpStatus.OK);
    }

}
