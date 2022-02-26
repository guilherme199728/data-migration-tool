package com.br.migrationTool.useCase;

import com.br.migrationTool.data.connection.ConnetionOracleJDBC;
import com.br.migrationTool.data.dao.TableReferencesDao;
import com.br.migrationTool.dto.ChildrenTableDto;
import com.br.migrationTool.dto.MigrationDto;
import com.br.migrationTool.dto.ParentTableDto;
import com.br.migrationTool.dto.TableDataDto;
import com.br.migrationTool.propertie.PropertiesLoaderImpl;
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

        ChildrenTableDto childrenTableDto = new ChildrenTableDto();
        childrenTableDto.setTableName(initialTableName);
        childrenTableDto.setPrimaryKeyName(allNamesColumnsInitialTable.get(0));
        childrenTableDto.setForeingKeyName(allNamesColumnsInitialTable.get(0));


        MigrationDto migrationDto = MigrationDto.builder()
                .tableName(initialTableName)
                .tableDataDto(tableDataDto)
                .childrenTableDto(childrenTableDto)
                .isSearchedReference(false)
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

        createMigration();
    }

    private void createMigration() throws SQLException {

        int count = 0;
        while (count < 50) {

            List<MigrationDto> migrationsDto = MigrationVo.cloneMigration();

            for (MigrationDto migrationDto : migrationsDto) {

                if (!migrationDto.isSearchedReference()) {
                    List<ParentTableDto> parentTableDtos = TableReferencesDao.getParentTablesFromConstrant(
                            PropertiesLoaderImpl.getValue("database.homolog.owner"),
                            migrationDto.getTableName(),
                            ConnetionOracleJDBC.getConnectionProd()
                    );

                    if (parentTableDtos.size() > 0) {
                        addParentsToMigrationList(parentTableDtos, migrationDto.getTableDataDto());
                        MigrationVo.setSearchedReferenceByTableName(migrationDto.getTableName(), true);
                    }
                }
            }

            count++;
        }

    }

    private void addParentsToMigrationList(List<ParentTableDto> parentTableDtos, TableDataDto tableDataDto) throws SQLException {

        for (ParentTableDto parentTableDto : parentTableDtos) {

            List<String> primaryKeysProd = TableReferencesDao.getPrimaryKeys(
                    tableDataDto.getTableName(),
                    parentTableDto.getForeingKeyName(),
                    tableDataDto.getPrimaryKeyName(),
                    "1",
                    "2",
                    ConnetionOracleJDBC.getConnectionProd()
            );

            List<String> primaryKeysHomolog = TableReferencesDao.getPrimaryKeys(
                    tableDataDto.getTableName(),
                    parentTableDto.getForeingKeyName(),
                    tableDataDto.getPrimaryKeyName(),
                    "1",
                    "2",
                    ConnetionOracleJDBC.getConnectionHomolog()
            );



            TableDataDto newTableDataDto = TableDataDto.builder()
                    .tableName(parentTableDto.getTableName())
                    .primaryKeyName(parentTableDto.getPrimaryKeyName())
                    .foreingKeyName(parentTableDto.getForeingKeyName())
                    .build();

            MigrationDto newMigrationDto = MigrationDto.builder()
                    .tableName(parentTableDto.getTableName())
                    .tableDataDto(newTableDataDto)
                    .primaryKeys(primaryKeysProd)
                    .isSearchedReference(false)
                    .build();

            MigrationVo.setListMigration(newMigrationDto);
            MigrationVo.removePrimaryKeysListMigrationByTableName(parentTableDto.getTableName(), primaryKeysHomolog);

        }
    }
}
