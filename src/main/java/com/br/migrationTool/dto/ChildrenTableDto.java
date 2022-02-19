package com.br.migrationTool.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChildrenTableDto {
    String tableName;
    String primaryKey;
    String foreingKey;
}
