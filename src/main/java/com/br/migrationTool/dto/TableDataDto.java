package com.br.migrationTool.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TableDataDto {
    String fieldName;
    String filedData;
    String filedType;
}
