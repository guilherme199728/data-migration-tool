package com.br.migrationTool.dtos.rest;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestRangeIdsMigrationDto {
    String tableName;
    Integer startId;
    Integer endId;
}
