package com.br.migrationTool.builders;

import com.br.migrationTool.vo.MigrationVo;

public class MigrationBuilder {
    private MigrationVo migrationVo;

    public static MigrationBuilder oneMigration() {
        MigrationBuilder builder = new MigrationBuilder();
        builder.migrationVo = MigrationVo.builder()
                .primaryKeys(PrimaryKeysBuilder.oneListPrimaryKeysBuilder().build())
                .tableName("Test")
                .build();

        return builder;
    }

    public MigrationVo build() {
        return migrationVo;
    }
}
