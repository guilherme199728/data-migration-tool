package com.br.migrationTool.exceptions;

public class ItemLimitMigrationExceededException extends ValidationException {
    public ItemLimitMigrationExceededException(String message) {
        super(message);
    }
}
