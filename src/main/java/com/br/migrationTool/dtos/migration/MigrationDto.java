package com.br.migrationTool.dtos.migration;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Builder
public class MigrationDto {
    private String tableName;
    private List<String> primaryKeys;
    private BasicTableStructureDto basicTableStructureDto;
    private boolean isSearchedReference;
    private int order;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MigrationDto that = (MigrationDto) o;
        return Objects.equals(tableName, that.tableName);
    }


}


