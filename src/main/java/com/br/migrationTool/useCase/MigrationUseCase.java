package com.br.migrationTool.useCase;

import com.br.migrationTool.data.connection.ConnetionOracleJDBC;
import com.br.migrationTool.data.dao.TableReferencesDao;
import com.br.migrationTool.dto.MigrationDto;
import com.br.migrationTool.dto.TableDataDto;
import com.br.migrationTool.vo.MigrationVo;

import java.sql.SQLException;
import java.util.List;

public class MigrationUseCase {
    public void start() throws SQLException {
        String initialTableName = "ACCOUNT";

        List<String> allNamesColumnsInitialTable = TableReferencesDao.getAllNamesColumnsTableFromTableName(initialTableName, ConnetionOracleJDBC.getConnectionHomolog());

        TableDataDto tableDataDto = TableDataDto.builder()
                .tableName(initialTableName)
                .primaryKeyName(allNamesColumnsInitialTable.get(0))
                .foreingKeyName(allNamesColumnsInitialTable.get(0))
                .build();

        MigrationDto migrationDto = MigrationDto.builder()
                .tableName(initialTableName)
                .tableDataDto(tableDataDto)
                .build();

        List<String> primaryKeysExistingInHomolog = TableReferencesDao.getPrimaryKeys(
                migrationDto.getTableName(),
                migrationDto.getTableDataDto().getPrimaryKeyName(),
                migrationDto.getTableDataDto().getPrimaryKeyName(),
                "1",
                "2",
                ConnetionOracleJDBC.getConnectionHomolog()
        );

        List<String> primaryKeysExistingInProd = TableReferencesDao.getPrimaryKeys(
                migrationDto.getTableName(),
                migrationDto.getTableDataDto().getPrimaryKeyName(),
                migrationDto.getTableDataDto().getPrimaryKeyName(),
                "1",
                "2",
                ConnetionOracleJDBC.getConnectionProd()
        );

        migrationDto.setPrimaryKeys(primaryKeysExistingInProd);

        MigrationVo.setListMigration(migrationDto);
        MigrationVo.removePrimaryKeysListMigrationByTableName(migrationDto.getTableName(), primaryKeysExistingInHomolog);
    }
}
