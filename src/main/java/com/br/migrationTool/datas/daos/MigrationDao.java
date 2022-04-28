package com.br.migrationTool.datas.daos;

import com.br.migrationTool.constraints.querys.MigrationQueryConstraint;
import com.br.migrationTool.datas.connections.ConnectionOracleJDBC;
import com.br.migrationTool.dtos.migration.MigrationDto;
import com.br.migrationTool.dtos.migration.TableDataDto;
import com.br.migrationTool.utils.OwnerUtils;
import com.br.migrationTool.utils.SqlUtils;
import com.br.migrationTool.utils.StringUtils;
import com.br.migrationTool.vos.MigrationVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class MigrationDao {

    @Autowired
    TableReferencesDao tableReferencesDao;
    @Autowired
    ConnectionOracleJDBC connectionOracleJDBC;
    @Autowired
    OwnerUtils ownerUtils;
    private static final Logger logger = LoggerFactory.getLogger(MigrationDao.class);

    public void executeMigration(List<MigrationDto> allMigration) throws SQLException {

        Collections.reverse(allMigration);
        Connection conn;
        PreparedStatement ps = null;

        try {
            for (MigrationDto migrationDto : allMigration) {
                for (String primaryKey : migrationDto.getPrimaryKeys()) {

                    List<TableDataDto> allTableDataDto = getDataTableByPrimaryKey(
                        migrationDto.getTableName(),
                        migrationDto.getBasicTableStructureDto().getPrimaryKeyName(), primaryKey
                    );

                    String sqlUpdateData = SqlUtils.getStringSqlUpdateData(
                        migrationDto.getTableName(),
                        migrationDto.getBasicTableStructureDto().getPrimaryKeyName(),
                        allTableDataDto
                    );
                    logger.info(sqlUpdateData);

                    conn = connectionOracleJDBC.getConnection(false);
                    ps = conn.prepareStatement(sqlUpdateData);

                    if (!ps.executeQuery().next()) {
                        String sqlInsertData = SqlUtils.getStringSqlInsertData(migrationDto.getTableName(), allTableDataDto);
                        logger.info(sqlInsertData);

                        ps = conn.prepareStatement(sqlInsertData);
                        ps.executeQuery();
                    }
                }
            }
        } finally {
            connectionOracleJDBC.close(ps, null);
        }
    }

    public List<TableDataDto> getDataTableByPrimaryKey(
        String tableName, String primaryKeyName, String primaryKey
    ) throws SQLException {

        PreparedStatement ps = null;
        ResultSet rs = null;
        List<TableDataDto> allTableDataDto = new ArrayList<>();

        try {
            String sql = String.format(
                MigrationQueryConstraint.GET_DATA_TABLE_BY_PRIMARY_KEY,
                ownerUtils.getOwner(true),
                tableName,
                primaryKeyName
            );

            ps = connectionOracleJDBC.getConnection(true).prepareStatement(sql);
            ps.setString(1, primaryKey);
            rs = ps.executeQuery();

            HashMap<String, String> allColumnsTable = tableReferencesDao.getAllNamesAndTypeColumnsTableFromTableName(
                tableName, false
            );

            while (rs.next()) {
                for (Map.Entry<String, String> column : allColumnsTable.entrySet()) {
                    TableDataDto tableDataDto = TableDataDto.builder()
                        .fieldName(column.getKey())
                        .filedData(correctFieldData(column, rs))
                        .filedType(column.getValue())
                        .build();

                    allTableDataDto.add(tableDataDto);
                }
            }
        } finally {
            connectionOracleJDBC.close(ps, rs);
        }

        return allTableDataDto;
    }

    private String correctFieldData(Map.Entry<String, String> column, ResultSet rs) throws SQLException {
        return column.getValue().equals("BLOB") ?
            null :
            rs.getString(column.getKey());
    }
}
