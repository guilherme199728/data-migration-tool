package com.br.migrationTool.data.dao;

import com.br.migrationTool.utils.StringUtils;
import com.br.migrationTool.data.connection.ConnectionOracleJDBC;
import com.br.migrationTool.dto.MigrationDto;
import com.br.migrationTool.vo.MigrationVo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MigrationDao {

    public static void executeMigration() throws SQLException {
        List<MigrationDto> allMigration = MigrationVo.getListMigration();
        Collections.reverse(allMigration);

        Connection connection = ConnectionOracleJDBC.getConnection(false);

        String sqlInsertData = "";

        for (MigrationDto migrationDto : allMigration) {
            for (String primaryKey : migrationDto.getPrimaryKeys()) {
                sqlInsertData = getSqlInsertData(migrationDto.getTableName(), migrationDto.getTableDataDto().getPrimaryKeyName(), primaryKey);
            }

            PreparedStatement ps = connection.prepareStatement(sqlInsertData);
            ps.executeQuery();
        }

    }

    private static String getSqlInsertData(String tableName, String primaryKeyName, String primaryKey) throws SQLException {
        HashMap<String, String> dataTable = getDataTableByPrimaryKey(tableName, primaryKeyName, primaryKey);

        String allTableFields = StringUtils.arrangeStringSeparatedByCommaAndInsideParentheses(
                new ArrayList<>(dataTable.keySet())
        );

        String allDataTable = StringUtils.arrangeStringSeparatedByCommaAndInsideParentheses(
                new ArrayList<>(dataTable.values())
        );

        return "INSERT INTO " + tableName + " " + allTableFields + " VALUES " + allDataTable;
    }

    private static HashMap<String, String> getDataTableByPrimaryKey(String tableName, String primaryKeyName, String primaryKey) throws SQLException {

        String sql = String.format("select * from %s where %s = ?", tableName, primaryKeyName);

        PreparedStatement ps = ConnectionOracleJDBC.getConnection(true).prepareStatement(sql);
        ps.setString(1, primaryKey);
        ResultSet rs = ps.executeQuery();

        HashMap<String, String> dataTable = new HashMap<>();

        List<String> allColumnsTable = TableReferencesDao.getAllNamesColumnsTableFromTableName(tableName, false);

        while (rs.next()) {
            for (String column : allColumnsTable) {
                dataTable.put(column, rs.getString(column));
            }
        }

        return dataTable;
    }

}
