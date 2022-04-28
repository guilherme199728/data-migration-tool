package com.br.migrationTool.datas.daos;

import com.br.migrationTool.datas.connections.ConnectionOracleJDBC;
import com.br.migrationTool.dtos.migration.MigrationDto;
import com.br.migrationTool.dtos.migration.TableDataDto;
import com.br.migrationTool.utils.SqlUtils;
import com.br.migrationTool.vos.MigrationVo;
import oracle.jdbc.proxy.annotation.Pre;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class MigrationDao {

    @Autowired
    DataTableDao dataTableDao;
    @Autowired
    ConnectionOracleJDBC connectionOracleJDBC;
    private static final Logger logger = LoggerFactory.getLogger(MigrationDao.class);

    public void executeMigration(MigrationVo migrationVo) throws SQLException {

        migrationVo.organizeListMigration();
        List<MigrationDto> migrationDtos = migrationVo.getListMigration();
        Connection conn = connectionOracleJDBC.getConnection(false);
        conn.setAutoCommit(false);

        try (Statement statement = conn.createStatement()) {
            for (MigrationDto migrationDto : migrationDtos) {
                for (String primaryKey : migrationDto.getPrimaryKeys()) {
                    List<TableDataDto> tableDataDtos = dataTableDao.getDataTableByPrimaryKey(
                        migrationDto.getTableName(),
                        migrationDto.getBasicTableStructureDto().getPrimaryKeyName(), primaryKey
                    );

                    int linesUpdated = executeUpdate(statement, migrationDto, tableDataDtos);

                    if (linesUpdated == 0) {
                        executeInsert(statement, migrationDto, tableDataDtos);
                    }
                }
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }finally {
            conn.setAutoCommit(true);
            migrationVo.clearMigrationList();
        }
    }

    private int executeUpdate(
        Statement statement, MigrationDto migrationDto, List<TableDataDto> tableDataDtos
    ) throws SQLException {

        String sqlUpdateData = SqlUtils.getStringSqlUpdateData(
            migrationDto.getTableName(),
            migrationDto.getBasicTableStructureDto().getPrimaryKeyName(),
            tableDataDtos
        );
        logger.info(sqlUpdateData);

        return statement.executeUpdate(sqlUpdateData);
    }

    private void executeInsert(
        Statement statement, MigrationDto migrationDto, List<TableDataDto> tableDataDtos
    ) throws SQLException {

        String sqlInsertData = SqlUtils.getStringSqlInsertData(migrationDto.getTableName(), tableDataDtos);
        logger.info(sqlInsertData);

        statement.execute(sqlInsertData);
    }
}
