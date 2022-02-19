package com.br.migrationTool.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ConectionDataBaseDto {
    String user;
    String password;
    String host;
    String port;
    String database;
}
