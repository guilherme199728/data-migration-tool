package com.br.migrationTool.handles;

import com.br.migrationTool.dtos.rest.BasicHttpResponse;
import com.br.migrationTool.exceptions.ItemNotFoundException;
import com.br.migrationTool.exceptions.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class DataMigrationToolExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = ItemNotFoundException.class)
    protected ResponseEntity<BasicHttpResponse> itemNotFound(RuntimeException ex, WebRequest request) {
        BasicHttpResponse responseBody = new BasicHttpResponse();
        responseBody.setMessage(ex.getMessage());

        return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ValidationException.class)
    protected ResponseEntity<BasicHttpResponse> validation(RuntimeException ex, WebRequest request) {
        BasicHttpResponse responseBody = new BasicHttpResponse();
        responseBody.setMessage(ex.getMessage());

        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }
}
