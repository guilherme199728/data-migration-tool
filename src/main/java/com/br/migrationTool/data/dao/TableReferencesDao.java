package com.br.migrationTool.data.dao;

import com.br.migrationTool.dto.ChildrenTableDto;
import com.br.migrationTool.dto.ParentTableDto;
import com.br.migrationTool.dto.TableDataDto;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TableReferencesDao {

    public static TableDataDto getTableData(String owner, String tableName, Connection connection) throws SQLException {
        String sql = "SELECT TABLE_NAME AS TABLENAME, COLUMN_NAME AS PRIMARYKEY, COLUMN_NAME AS FOREINGKEY " +
        "FROM ALL_CONS_COLUMNS " +
        "WHERE OWNER = ? " +
        "AND OWNER = ? " +
        "AND TABLE_NAME = ? " +
        "AND COLUMN_NAME LIKE %" + "?" + "% " +
        "AND ROWNUM <= 1 ";

        QueryRunner runner = new QueryRunner();
        ResultSetHandler<TableDataDto> rsh = new BeanHandler<>(TableDataDto.class);
        Object [] params = new Object[]{owner, owner, tableName, tableName};

        return runner.query(connection, sql, rsh, params);
    }

    public static List<ParentTableDto> getParentTablesFromConstrant(String owner, String tableName, Connection connection) throws SQLException {

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

    public static List<ChildrenTableDto> getChildrenTablesFromConstrant(String owner, String tableName, Connection connection) throws SQLException {

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

    public static String getPrimaryKeyNameFromConstrant(String owner, String tableName, Connection connection) throws SQLException {

        String sql = "SELECT A.COLUMN_NAME " +
        "FROM ALL_CONS_COLUMNS A " +
        "JOIN ALL_CONSTRAINTS C ON A.OWNER = C.OWNER " +
        "AND A.CONSTRAINT_NAME = C.CONSTRAINT_NAME " +
        "WHERE C.OWNER = ? " +
        "AND C.CONSTRAINT_TYPE = 'P' " +
        "AND C.TABLE_NAME = ? ";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, owner);
        ps.setString(2, tableName);
        ResultSet rs = ps.executeQuery();

        String primaryKeyNames = null;
        while (rs.next()) {
            primaryKeyNames = rs.getString("COLUMN_NAME");
        }

        return primaryKeyNames;
    }

    public static List<String> getAllNamesColumnsTableFromTableName(String tableName, Connection connection) throws SQLException {

        String sql = "SELECT COLUMN_NAME, DATA_TYPE " +
        "FROM USER_TAB_COLUMNS WHERE " +
        "TABLE_NAME = ? ";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, tableName);
        ResultSet rs = ps.executeQuery();

        List<String> allNamesColunsTable = new ArrayList<>();
        while (rs.next()) {
            allNamesColunsTable.add(rs.getString("COLUMN_NAME"));
        }

        return allNamesColunsTable;
    }

    public static List<String> getPrimaryKeys(String tableName, String primaryKeyName, String whereColum, String starRange, String endRange, Connection connection) throws SQLException {

        String sql = String.format("SELECT %s FROM %s WHERE %s BETWEEN %s AND %s", primaryKeyName, tableName, whereColum, starRange, endRange);

        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        List<String> allNamesColunsTable = new ArrayList<>();
        while (rs.next()) {
            allNamesColunsTable.add(rs.getString(primaryKeyName));
        }

        return allNamesColunsTable;
    }

    private static String getPrimaryKeys(String tableName, String primaryKeyName, String whereColum) {
        // TODO : Terminar implementação
        List<String> primaryKeys = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 50; i++) {
            primaryKeys.add(String.valueOf(random.nextInt(50)));
        }

        int offSet = 10;

        return String.format("SELECT %s FROM %s WHERE %s IN ", primaryKeyName, tableName, whereColum) + getPrimaryKeysConcatenatedByOffSet(primaryKeyName, primaryKeys, offSet);
    }

    private static String getPrimaryKeysConcatenatedByOffSet(String primaryKeyName, List<String> primaryKeys, int offSet) {

        List<String> listPrimaryKeysByOffSet = getListPrimaryKeysSeparatedByBarByOffset(offSet, primaryKeys);

        int index = 1;
        StringBuilder offSetQueryConcat = new StringBuilder();
        for (String primaryKeyByOffSet : listPrimaryKeysByOffSet) {
            if (index == 1) {
                offSetQueryConcat.append("(").append(primaryKeyByOffSet).append(")");
            } else {
                offSetQueryConcat.append(" OR ").append(primaryKeyName).append(" IN (").append(primaryKeyByOffSet).append(")");
            }

            index++;
        }

        return offSetQueryConcat.toString();
    }

    private static List<String> getListPrimaryKeysSeparatedByBarByOffset(int offSet, List<String> primaryKeys) {
        StringBuilder primaryKeysConcat = new StringBuilder();
        int nextOffSet = offSet;
        int index = 1;

        for (String primaryKey : primaryKeys) {
            primaryKeysConcat.append(primaryKey);
            if(nextOffSet == index){
                primaryKeysConcat.append("/");
                nextOffSet = nextOffSet + offSet;
            } else if (index != primaryKeys.size()){
                primaryKeysConcat.append(",");
            }

            index++;
        }

        return List.of(primaryKeysConcat.toString().split("/"));
    }
}
