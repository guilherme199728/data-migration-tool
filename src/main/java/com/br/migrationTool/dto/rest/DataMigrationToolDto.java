package com.br.migrationTool.dto.rest;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DataMigrationToolDto {
    String tableName;
    List<String> ids;
}
