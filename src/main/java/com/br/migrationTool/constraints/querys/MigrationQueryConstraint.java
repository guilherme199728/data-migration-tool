package com.br.migrationTool.constraints.querys;

public final class MigrationQueryConstraint {

    public static final String GET_DATA_TABLE_BY_PRIMARY_KEY = """
            SELECT * FROM %s.%s WHERE %s = ?
        """;
}
