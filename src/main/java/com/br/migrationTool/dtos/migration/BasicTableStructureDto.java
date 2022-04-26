package com.br.migrationTool.dtos.migration;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class BasicTableStructureDto {
    String tableName;
    String primaryKeyName;
}
