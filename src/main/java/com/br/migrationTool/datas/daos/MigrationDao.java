package com.br.migrationTool.datas.daos;

import com.br.migrationTool.datas.connections.ConnectionOracleJDBC;
import com.br.migrationTool.dtos.migration.MigrationDto;
import com.br.migrationTool.dtos.migration.TableDataDto;
import com.br.migrationTool.utils.SqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Component
public class MigrationDao {

    @Autowired
    DataTableDao dataTableDao;
    @Autowired
    ConnectionOracleJDBC connectionOracleJDBC;
    private static final Logger logger = LoggerFactory.getLogger(MigrationDao.class);

    public void executeMigration(List<MigrationDto> allMigration) throws SQLException {

        Collections.reverse(allMigration);
        Connection conn;
        PreparedStatement ps = null;

        try {
            for (MigrationDto migrationDto : allMigration) {
                for (String primaryKey : migrationDto.getPrimaryKeys()) {

                    List<TableDataDto> allTableDataDto = dataTableDao.getDataTableByPrimaryKey(
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
}
