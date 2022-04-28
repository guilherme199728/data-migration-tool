package com.br.migrationTool.datas.daos;

import com.br.migrationTool.datas.connections.ConnectionOracleJDBC;
import com.br.migrationTool.dtos.migration.MigrationDto;
import com.br.migrationTool.dtos.migration.TableDataDto;
import com.br.migrationTool.utils.SqlUtils;
import oracle.jdbc.proxy.annotation.Pre;
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

    public void executeMigration(List<MigrationDto> migrationDtos) throws SQLException {

        // corrige a ordem da lista de migração para que inserção seja feita corretamente
        Collections.reverse(migrationDtos);
        Connection conn;
        PreparedStatement ps = null;

        for (MigrationDto migrationDto : migrationDtos) {
            for (String primaryKey : migrationDto.getPrimaryKeys()) {
                try {
                    List<TableDataDto> tableDataDtos = dataTableDao.getDataTableByPrimaryKey(
                        migrationDto.getTableName(),
                        migrationDto.getBasicTableStructureDto().getPrimaryKeyName(), primaryKey
                    );

                    conn = connectionOracleJDBC.getConnection(false);
                    ps = executeUpdate(conn, migrationDto, tableDataDtos);

                    if (isUpdateNotExecuted(ps)) {
                        executeInsert(ps, conn, migrationDto, tableDataDtos);
                    }
                } finally {
                    connectionOracleJDBC.close(ps, null);
                }
            }
        }
    }

    private PreparedStatement executeUpdate(
        Connection conn, MigrationDto migrationDto, List<TableDataDto> tableDataDtos
    ) throws SQLException {

        String sqlUpdateData = SqlUtils.getStringSqlUpdateData(
            migrationDto.getTableName(),
            migrationDto.getBasicTableStructureDto().getPrimaryKeyName(),
            tableDataDtos
        );
        logger.info(sqlUpdateData);

        return conn.prepareStatement(sqlUpdateData);
    }

    private void executeInsert(
        PreparedStatement ps, Connection conn, MigrationDto migrationDto, List<TableDataDto> tableDataDtos
    ) throws SQLException {

        String sqlInsertData = SqlUtils.getStringSqlInsertData(migrationDto.getTableName(), tableDataDtos);
        logger.info(sqlInsertData);

        ps = conn.prepareStatement(sqlInsertData);
        ps.executeQuery();
    }

    private boolean isUpdateNotExecuted(PreparedStatement ps) throws SQLException {
        return !ps.executeQuery().next();
    }

}
