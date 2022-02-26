package com.br.migrationTool.useCase;

import com.br.migrationTool.data.connection.ConnetionOracleJDBC;
import com.br.migrationTool.data.dao.TableReferencesDao;
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
        String starRange = "1";
        String endRange = "8";

        addInitialTableToMigrationListByRange(initialTableName, starRange, endRange);
        createMigrationList();
        executeMigration();
    }

    private void addInitialTableToMigrationListByRange(String initialTableName, String starRange, String endRange) throws SQLException {
        List<String> allNamesColumnsInitialTable = TableReferencesDao.getAllNamesColumnsTableFromTableName(initialTableName, ConnetionOracleJDBC.getConnectionHomolog());

        TableDataDto tableDataDto = TableDataDto.builder()
                .tableName(initialTableName)
                .primaryKeyName(allNamesColumnsInitialTable.stream().findFirst().orElse(""))
                .foreingKeyName(allNamesColumnsInitialTable.stream().findFirst().orElse(""))
                .build();


        MigrationDto migrationDto = MigrationDto.builder()
                .tableName(initialTableName)
                .tableDataDto(tableDataDto)
                .isSearchedReference(false)
                .build();

        List<String> primaryKeysExistingInHomolog = TableReferencesDao.getPrimaryKeysByRange(
                migrationDto.getTableName(),
                migrationDto.getTableDataDto().getPrimaryKeyName(),
                migrationDto.getTableDataDto().getPrimaryKeyName(),
                starRange,
                endRange,
                ConnetionOracleJDBC.getConnectionHomolog()
        );

        List<String> primaryKeysExistingInProd = TableReferencesDao.getPrimaryKeysByRange(
                migrationDto.getTableName(),
                migrationDto.getTableDataDto().getPrimaryKeyName(),
                migrationDto.getTableDataDto().getPrimaryKeyName(),
                starRange,
                endRange,
                ConnetionOracleJDBC.getConnectionProd()
        );

        migrationDto.setPrimaryKeys(primaryKeysExistingInProd);

        MigrationVo.setListMigration(migrationDto);
        MigrationVo.removePrimaryKeysListMigrationByTableName(migrationDto.getTableName(), primaryKeysExistingInHomolog);
    }

    private void createMigrationList() throws SQLException {

        while (MigrationVo.isAllReferencesSearched()) {

            List<MigrationDto> migrationsDto = MigrationVo.cloneMigration();

            for (MigrationDto migrationDto : migrationsDto) {

                if (!migrationDto.isSearchedReference()) {
                    List<ParentTableDto> parentTableDtos = TableReferencesDao.getParentTablesFromConstraint(
                            PropertiesLoaderImpl.getValue("database.homolog.owner"),
                            migrationDto.getTableName(),
                            ConnetionOracleJDBC.getConnectionProd()
                    );

                    if (parentTableDtos.size() > 0) {
                        addParentsToMigrationList(parentTableDtos, migrationDto);
                        MigrationVo.setSearchedReferenceByTableName(migrationDto.getTableName(), true);
                    } else {
                        MigrationVo.setSearchedReferenceByTableName(migrationDto.getTableName(), true);
                    }
                }
            }
        }

    }

    private void addParentsToMigrationList(List<ParentTableDto> parentTableDtos, MigrationDto migrationDto) throws SQLException {

        for (ParentTableDto parentTableDto : parentTableDtos) {

            List<String> primaryKeysProd = TableReferencesDao.getPrimaryKeysByParentTable(
                    migrationDto,
                    parentTableDto,
                    ConnetionOracleJDBC.getConnectionProd()
            );

            List<String> primaryKeysHomolog = TableReferencesDao.getPrimaryKeysByParentTable(
                    migrationDto,
                    parentTableDto,
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

    private void executeMigration() {

    }
}
