package com.br.migrationTool.datas.daos;

import com.br.migrationTool.dtos.migration.TableDataDto;
import com.br.migrationTool.utils.OwnerUtils;
import com.br.migrationTool.utils.SqlUtils;
import com.br.migrationTool.datas.connections.ConnectionOracleJDBC;
import com.br.migrationTool.dtos.migration.MigrationDto;
import com.br.migrationTool.vos.MigrationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;

@Component
public class MigrationDao {

    @Autowired
    TableReferencesDao tableReferencesDao;

    @Autowired
    ConnectionOracleJDBC connectionOracleJDBC;

    @Autowired
    OwnerUtils ownerUtils;

    public void executeMigration() throws SQLException {
        List<MigrationDto> allMigration = MigrationVo.getListMigration();
        Collections.reverse(allMigration);

        Connection connection = connectionOracleJDBC.getConnection(false);
        PreparedStatement ps;

        for (MigrationDto migrationDto : allMigration) {
            for (String primaryKey : migrationDto.getPrimaryKeys()) {

                List<TableDataDto> allTableDataDto = getDataTableByPrimaryKey(
                        migrationDto.getTableName(),
                        migrationDto.getTableStructureDto().getPrimaryKeyName(), primaryKey
                );

                String sqlUpdateData = SqlUtils.getStringSqlUpdateData(
                        migrationDto.getTableName(),
                        migrationDto.getTableStructureDto().getPrimaryKeyName(),
                        allTableDataDto
                );
                System.out.println(sqlUpdateData);

                ps = connection.prepareStatement(sqlUpdateData);

                if (!ps.executeQuery().next()) {

                    String sqlInsertData = SqlUtils.getStringSqlInsertData(migrationDto.getTableName(), allTableDataDto);
                    System.out.println(sqlInsertData);

                    ps = connection.prepareStatement(sqlInsertData);
                    ps.executeQuery();

                }
            }
        }
    }

    private List<TableDataDto> getDataTableByPrimaryKey(String tableName, String primaryKeyName, String primaryKey) throws SQLException {

        String sql = String.format("select * from %s.%s where %s = ?", ownerUtils.getOwner(true), tableName, primaryKeyName);

        PreparedStatement ps = connectionOracleJDBC.getConnection(true).prepareStatement(sql);
        ps.setString(1, primaryKey);
        ResultSet rs = ps.executeQuery();

        HashMap<String, String> allColumnsTable = tableReferencesDao.getAllNamesAndTypeColumnsTableFromTableName(tableName, false);

        List<TableDataDto> allTableDataDto = new ArrayList<>();

        while (rs.next()) {
            for (Map.Entry<String, String> column : allColumnsTable.entrySet()) {
                TableDataDto tableDataDto = TableDataDto.builder()
                        .fieldName(column.getKey())
                        .filedData(column.getValue().equals("BLOB") ? null : rs.getString(column.getKey()))
                        .filedType(column.getValue())
                        .build();

                allTableDataDto.add(tableDataDto);
            }
        }

        return allTableDataDto;
    }
}
