package com.br.migrationTool.data.dao;

import com.br.migrationTool.dto.TableDataDto;
import com.br.migrationTool.utils.StringUtils;
import com.br.migrationTool.data.connection.ConnectionOracleJDBC;
import com.br.migrationTool.dto.MigrationDto;
import com.br.migrationTool.vo.MigrationVo;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class MigrationDao {

    public static void executeMigration() throws SQLException {
        List<MigrationDto> allMigration = MigrationVo.getListMigration();
        Collections.reverse(allMigration);

        Connection connection = ConnectionOracleJDBC.getConnection(false);

        for (MigrationDto migrationDto : allMigration) {
            for (String primaryKey : migrationDto.getPrimaryKeys()) {
                String sqlInsertData = getSqlInsertData(migrationDto.getTableName(), migrationDto.getTableStructureDto().getPrimaryKeyName(), primaryKey);

                System.out.println(sqlInsertData);

                PreparedStatement ps = connection.prepareStatement(sqlInsertData);
                ps.executeQuery();
            }
        }

    }

    private static String getSqlInsertData(String tableName, String primaryKeyName, String primaryKey) throws SQLException {
        List<TableDataDto> allTableDataDto = getDataTableByPrimaryKey(tableName, primaryKeyName, primaryKey);

        String allTableFields = StringUtils.arrangeStringSeparatedByCommaAndInsideParenthesesByListString(
            allTableDataDto
                .stream()
                .map(TableDataDto::getFieldName)
                .collect(Collectors.toList())
        );

        String allDataTable = StringUtils.arrangeStringSeparatedByCommaAndInsideParenthesesByListTableDataDto(
                allTableDataDto
        );

        return "INSERT INTO " + tableName + " " + allTableFields + " VALUES " + allDataTable;
    }

    private static List<TableDataDto> getDataTableByPrimaryKey(String tableName, String primaryKeyName, String primaryKey) throws SQLException {

        String sql = String.format("select * from %s where %s = ?", tableName, primaryKeyName);

        PreparedStatement ps = ConnectionOracleJDBC.getConnection(true).prepareStatement(sql);
        ps.setString(1, primaryKey);
        ResultSet rs = ps.executeQuery();

        HashMap<String, String> allColumnsTable = TableReferencesDao.getAllNamesAndTypeColumnsTableFromTableName(tableName, false);

        List<TableDataDto> allTableDataDto = new ArrayList<>();

        while (rs.next()) {
            for (Map.Entry<String, String> column : allColumnsTable.entrySet()) {
                TableDataDto tableDataDto = TableDataDto.builder()
                        .fieldName(column.getKey())
                        .filedData(rs.getString(column.getKey()))
                        .filedType(column.getValue())
                        .build();

                allTableDataDto.add(tableDataDto);
            }
        }

        return allTableDataDto;
    }

}
