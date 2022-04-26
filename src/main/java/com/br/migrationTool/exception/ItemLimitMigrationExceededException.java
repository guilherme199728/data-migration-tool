package com.br.migrationTool.exception;

public class ItemLimitMigrationExceededException extends ValidationException {
    public ItemLimitMigrationExceededException(String message) {
        super(message);
    }
}
