package com.br.migrationTool.builders;

import com.br.migrationTool.dtos.migration.MigrationDto;

import java.util.List;

public class MigrationDtoBuilder {
    private MigrationDto migrationDto;

    public static MigrationDtoBuilder oneMigration() {
        MigrationDtoBuilder builder = new MigrationDtoBuilder();
        builder.migrationDto = MigrationDto.builder()
                .primaryKeys(PrimaryKeysBuilder.oneListPrimaryKeysBuilder().build())
                .tableName("TEST")
                .build();

        return builder;
    }

    public MigrationDtoBuilder withPrimaryKeys(List<String> primaryKeys) {
        migrationDto.setPrimaryKeys(primaryKeys);

        return this;
    }

    public MigrationDtoBuilder withTableName(String tableName) {
        migrationDto.setTableName(tableName);

        return this;
    }

    public MigrationDto build() {
        return migrationDto;
    }
}
