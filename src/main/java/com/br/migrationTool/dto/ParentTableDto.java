package com.br.migrationTool.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParentTableDto {
    private String tableName;
    private String primaryKeyName;
    private String foreingKeyName;
}
