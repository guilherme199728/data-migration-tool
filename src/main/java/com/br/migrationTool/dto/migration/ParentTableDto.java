package com.br.migrationTool.dto.migration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParentTableDto {
    private String tableName;
    private String primaryKeyName;
    private String foreingKeyName;
}
