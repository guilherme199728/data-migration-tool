package com.br.migrationTool.useCase;

import com.br.migrationTool.data.connection.ConnetionOracleJDBC;
import com.br.migrationTool.data.dao.TableReferencesDao;
import com.br.migrationTool.dto.ParentTableDto;
import com.br.migrationTool.propertie.PropertiesLoaderImpl;
import com.br.migrationTool.vo.MigrationVo;

import java.sql.SQLException;
import java.util.List;

public class MigrationUseCase {
    public void start() throws SQLException {
        String initialTableName = "ACCOUNT";

        List<ParentTableDto> listParentTableDto = TableReferencesDao.getParentTablesFromConstrant(
                PropertiesLoaderImpl.getValue("database.homolog.owner"),
                initialTableName,
                ConnetionOracleJDBC.getConnectionHomolog()
        );

        ParentTableDto initialParentTableDto = new ParentTableDto();
        List<String> allNamesColumnsInitialTable = TableReferencesDao.getAllNamesColumnsTableFromTableName(initialTableName, ConnetionOracleJDBC.getConnectionHomolog());
        initialParentTableDto.setTableName("ACCOUNT");
        initialParentTableDto.setPrimaryKey(allNamesColumnsInitialTable.get(0));
        initialParentTableDto.setForeingKey(allNamesColumnsInitialTable.get(0));

        listParentTableDto.add(initialParentTableDto);


        for(ParentTableDto parentTableDto : listParentTableDto) {

            createMigration(parentTableDto, initialTableName, initialParentTableDto.getPrimaryKey());
        }
    }

    private void createMigration(ParentTableDto parentTableDto, String initialTableName, String initialPrimaryKey) throws SQLException {
        List<String> primaryKeysExistingInHomolog = TableReferencesDao.getPrimaryKeys(
                initialTableName,
                parentTableDto.getForeingKey(),
                initialPrimaryKey,
                "1",
                "2",
                ConnetionOracleJDBC.getConnectionHomolog()
        );

        List<String> primaryKeysExistingInProd = TableReferencesDao.getPrimaryKeys(
                initialTableName,
                parentTableDto.getForeingKey(),
                initialPrimaryKey,
                "1",
                "2",
                ConnetionOracleJDBC.getConnectionProd()
        );

        MigrationVo.setListMigration(parentTableDto.getTableName(), primaryKeysExistingInProd);
        MigrationVo.removePrimaryKeysListMigrationByTableName(parentTableDto.getTableName(), primaryKeysExistingInHomolog);
    }
}
