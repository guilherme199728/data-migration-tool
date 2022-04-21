package com.br.migrationTool.dtos.migration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChildrenTableDto {
    private String tableName;
    private String primaryKeyName;
    private String foreingKeyName;
}
