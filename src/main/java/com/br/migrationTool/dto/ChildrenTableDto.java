package com.br.migrationTool.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChildrenTableDto {
    String tableName;
    String primaryKeyName;
    String foreingKeyName;
}
