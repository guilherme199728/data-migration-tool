package com.br.migrationTool.dtos.migration;

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
    private BasicTableStructureDto basicTableStructureDto;
    private boolean isSearchedReference;
}
