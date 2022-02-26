package com.br.migrationTool.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class MigrationDto {
    private String tableName;
    private List<String> primaryKeys;
    private TableDataDto tableDataDto;
    private boolean isSearchedReference;
}
