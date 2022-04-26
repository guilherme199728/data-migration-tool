package com.br.migrationTool.dtos.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasicHttpResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String message;
}
