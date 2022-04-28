package com.br.migrationTool.constraints.querys;

public final class TableReferenceQueryConstraint {

    public static final String GET_PARENT_TABLE = """
            SELECT C_PK.TABLE_NAME AS TABLENAME, B.COLUMN_NAME AS PRIMARYKEYNAME, A.COLUMN_NAME AS FOREINGKEYNAME
            FROM ALL_CONS_COLUMNS A
            JOIN ALL_CONSTRAINTS C ON A.OWNER = C.OWNER
            AND A.CONSTRAINT_NAME = C.CONSTRAINT_NAME
            JOIN ALL_CONSTRAINTS C_PK ON C.R_OWNER = C_PK.OWNER
            AND C.R_CONSTRAINT_NAME = C_PK.CONSTRAINT_NAME
            JOIN ALL_CONS_COLUMNS B ON B.TABLE_NAME = C_PK.TABLE_NAME
            AND B.CONSTRAINT_NAME = C_PK.CONSTRAINT_NAME
            WHERE C.CONSTRAINT_TYPE = 'R'
            AND A.OWNER = ?
            AND B.OWNER = ?
            AND A.TABLE_NAME = ?
            AND B.TABLE_NAME <> ?
        """;

    public static final String GET_BASIC_TABLE_STRUCTURE = """
            SELECT a.column_name AS PRIMARYKEYNAME, a.table_name AS TABLENAME
            FROM all_cons_columns a
            JOIN all_constraints c ON a.owner = c.owner
            AND a.constraint_name = c.constraint_name
            WHERE c.owner = ?
            AND c.constraint_type = 'P'
            AND LOWER(c.table_name) = LOWER(?)
        """;

    public static final String GET_CHILDREN_TABLES = """
            SELECT C.TABLE_NAME AS TABLENAME, B.COLUMN_NAME AS PRIMARYKEYNAME, B2.COLUMN_NAME AS FOREINGKEYNAME
            FROM ALL_CONS_COLUMNS A
            JOIN ALL_CONSTRAINTS C ON A.OWNER = C.OWNER
            AND A.CONSTRAINT_NAME = C.R_CONSTRAINT_NAME
            JOIN ALL_CONSTRAINTS C_PK ON C.R_OWNER = C_PK.OWNER
            AND C.R_CONSTRAINT_NAME = C_PK.CONSTRAINT_NAME
            JOIN ALL_CONS_COLUMNS B ON B.TABLE_NAME = C_PK.TABLE_NAME
            AND B.CONSTRAINT_NAME = C_PK.CONSTRAINT_NAME
            JOIN ALL_CONS_COLUMNS B2 ON B2.TABLE_NAME = C.TABLE_NAME
            AND B2.CONSTRAINT_NAME = C.CONSTRAINT_NAME
            WHERE C.CONSTRAINT_TYPE = 'R'
            AND A.OWNER = ?
            AND B.OWNER = ?
            AND A.TABLE_NAME = ?
        """;

    public static final String GET_PRIMARY_KEY_NAME = """
            SELECT A.COLUMN_NAME
            FROM ALL_CONS_COLUMNS A
            JOIN ALL_CONSTRAINTS C ON A.OWNER = C.OWNER
            AND A.CONSTRAINT_NAME = C.CONSTRAINT_NAME
            WHERE C.OWNER = ?
            AND C.CONSTRAINT_TYPE = 'P'
            AND C.TABLE_NAME = ?
        """;

    public static final String GET_ALL_NAMES_AND_TYPE_COLUMNS_TABLE = """
            SELECT COLUMN_NAME AS FIELDNAME, DATA_TYPE AS FIELDTYPE
            FROM USER_TAB_COLUMNS WHERE
            TABLE_NAME = ?
        """;

    public static final String GET_PRIMARY_KEYS_BY_PARENT_TABLE = """
            SELECT A.%s FROM %s.%s A
            JOIN %s.%s B on
            A.%s = B.%s WHERE A.%s IN
        """;

    public static final String GET_PRIMARY_KEYS = """
            SELECT %s FROM %s.%s WHERE %s IN (%s)
        """;
}
