package com.br.migrationTool.dtos.migration;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TableDataDto {
    Long id;
    String fieldName;
    String filedData;
    String filedType;
}
