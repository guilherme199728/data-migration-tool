package com.br.migrationTool.data.dao;

import com.br.migrationTool.dto.ChildrenTableDto;
import com.br.migrationTool.dto.ParentTableDto;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TableReferencesDao {

    public static List<ParentTableDto> getParentTablesFromTableName(String owner, String tableName, Connection connection) throws SQLException {

        String sql = "SELECT C_PK.TABLE_NAME AS TABLENAME, B.COLUMN_NAME AS PRIMARYKEY, A.COLUMN_NAME AS FOREINGKEY " +
        "FROM ALL_CONS_COLUMNS A " +
        "JOIN ALL_CONSTRAINTS C ON A.OWNER = C.OWNER " +
        "AND A.CONSTRAINT_NAME = C.CONSTRAINT_NAME " +
        "JOIN ALL_CONSTRAINTS C_PK ON C.R_OWNER = C_PK.OWNER " +
        "AND C.R_CONSTRAINT_NAME = C_PK.CONSTRAINT_NAME " +
        "JOIN ALL_CONS_COLUMNS B ON B.TABLE_NAME = C_PK.TABLE_NAME " +
        "AND B.CONSTRAINT_NAME = C_PK.CONSTRAINT_NAME " +
        "WHERE C.CONSTRAINT_TYPE = 'R' " +
        "AND A.OWNER = ? " +
        "AND B.OWNER = ? " +
        "AND A.TABLE_NAME = ? ";

        QueryRunner runner = new QueryRunner();
        ResultSetHandler<List<ParentTableDto>> rsh = new BeanListHandler<>(ParentTableDto.class);
        Object [] params = new Object[]{owner, owner, tableName};

        return runner.query(connection, sql, rsh, params);
    }

    public static List<ChildrenTableDto> getChildrenTablesFromTableName(String owner, String tableName, Connection connection) throws SQLException {

        String sql = "SELECT C.TABLE_NAME AS TABLENAME, B.COLUMN_NAME AS PRIMARYKEY, B2.COLUMN_NAME AS FOREINGKEY " +
        "FROM ALL_CONS_COLUMNS A " +
        "JOIN ALL_CONSTRAINTS C ON A.OWNER = C.OWNER " +
        "AND A.CONSTRAINT_NAME = C.R_CONSTRAINT_NAME " +
        "JOIN ALL_CONSTRAINTS C_PK ON C.R_OWNER = C_PK.OWNER " +
        "AND C.R_CONSTRAINT_NAME = C_PK.CONSTRAINT_NAME " +
        "JOIN ALL_CONS_COLUMNS B ON B.TABLE_NAME = C_PK.TABLE_NAME " +
        "AND B.CONSTRAINT_NAME = C_PK.CONSTRAINT_NAME " +
        "JOIN ALL_CONS_COLUMNS B2 ON B2.TABLE_NAME = C.TABLE_NAME " +
        "AND B2.CONSTRAINT_NAME = C.CONSTRAINT_NAME " +
        "WHERE C.CONSTRAINT_TYPE = 'R' " +
        "AND A.OWNER = ? " +
        "AND B.OWNER = ? " +
        "AND A.TABLE_NAME = ? ";

        QueryRunner runner = new QueryRunner();
        ResultSetHandler<List<ChildrenTableDto>> rsh = new BeanListHandler<>(ChildrenTableDto.class);
        Object [] params = new Object[]{owner, owner, tableName};

        return runner.query(connection, sql, rsh, params);
    }

    public static List<String> getPrimaryKeyNamesFromTableName(String owner, String tableName, Connection connection) throws SQLException {

        String sql = "SELECT A.COLUMN_NAME " +
        "FROM ALL_CONS_COLUMNS A " +
        "JOIN ALL_CONSTRAINTS C ON A.OWNER = C.OWNER " +
        "AND A.CONSTRAINT_NAME = C.CONSTRAINT_NAME " +
        "WHERE C.OWNER = ? " +
        "AND C.CONSTRAINT_TYPE = ? " +
        "AND C.TABLE_NAME = ? ";

        QueryRunner runner = new QueryRunner();
        ResultSetHandler<List<String>> rsh = new BeanListHandler<>(String.class);
        Object [] params = new Object[]{owner, owner, tableName};

        return runner.query(connection, sql, rsh, params);
    }

    public static List<String> getAllColunsTableFromTableName(String tableName, Connection connection) throws SQLException {

        String sql = "SELECT COLUMN_NAME, DATA_TYPE " +
        "FROM USER_TAB_COLUMNS WHERE " +
        "TABLE_NAME = ? ";

        QueryRunner runner = new QueryRunner();
        ResultSetHandler<List<String>> rsh = new BeanListHandler<>(String.class);
        Object [] params = new Object[]{tableName};

        return runner.query(connection, sql, rsh, params);
    }
}
