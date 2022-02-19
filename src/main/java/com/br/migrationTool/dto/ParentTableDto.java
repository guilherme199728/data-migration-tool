package com.br.migrationTool.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParentTableDto {
    String tableName;
    String primaryKey;
    String foreingKey;
}
