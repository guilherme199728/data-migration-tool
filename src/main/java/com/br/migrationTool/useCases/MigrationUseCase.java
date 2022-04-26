package com.br.migrationTool.useCases;

import com.br.migrationTool.datas.daos.MigrationDao;
import com.br.migrationTool.datas.daos.TableReferencesDao;
import com.br.migrationTool.dtos.migration.MigrationDto;
import com.br.migrationTool.dtos.migration.ParentTableDto;
import com.br.migrationTool.dtos.migration.BasicTableStructureDto;
import com.br.migrationTool.validations.MigrationValidation;
import com.br.migrationTool.vos.MigrationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Component
public class MigrationUseCase {

    @Autowired
    TableReferencesDao tableReferencesDao;

    @Autowired
    MigrationDao migrationDao;

    @Autowired
    MigrationValidation migrationValidation;

    public void process(String tableName, List<String> ids) throws SQLException {
        addInitialTableToMigrationListByRange(tableName, ids);
        createMigrationList();
        migrationDao.executeMigration();
    }

    private void addInitialTableToMigrationListByRange(String initialTableName, List<String> primaryKeyList) throws SQLException {

        BasicTableStructureDto basicTableStructureDto = tableReferencesDao.getBasicTableStructureFromConstraint(initialTableName, true);

        MigrationDto migrationDto = MigrationDto.builder().tableName(initialTableName).basicTableStructureDto(basicTableStructureDto).isSearchedReference(false).build();

        List<String> primaryKeysExistingInHomolog = tableReferencesDao.getPrimaryKeys(migrationDto.getTableName(), migrationDto.getBasicTableStructureDto().getPrimaryKeyName(), migrationDto.getBasicTableStructureDto().getPrimaryKeyName(), primaryKeyList, false);

        List<String> primaryKeysExistingInProd = tableReferencesDao.getPrimaryKeys(migrationDto.getTableName(), migrationDto.getBasicTableStructureDto().getPrimaryKeyName(), migrationDto.getBasicTableStructureDto().getPrimaryKeyName(), primaryKeyList, true);

        migrationValidation.isNoItemsFound(primaryKeysExistingInProd);
        migrationDto.setPrimaryKeys(primaryKeysExistingInProd);
        MigrationVo.setListMigration(migrationDto);
        MigrationVo.removePrimaryKeysListMigrationByTableName(migrationDto.getTableName(), primaryKeysExistingInHomolog);
        migrationValidation.isAllMigratedItems(MigrationVo.getListMigration());
    }

    private void createMigrationList() throws SQLException {

        while (MigrationVo.isAllReferencesSearched()) {

            List<MigrationDto> migrationsDto = MigrationVo.cloneMigration();

            for (MigrationDto migrationDto : migrationsDto) {

                if (!migrationDto.isSearchedReference()) {
                    List<ParentTableDto> parentTableDtos = tableReferencesDao.getParentTablesFromConstraint(migrationDto.getTableName(), true);

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

            List<String> primaryKeysProd = tableReferencesDao.getPrimaryKeysByParentTable(migrationDto, parentTableDto, true);

            if (!primaryKeysProd.isEmpty()) {
                BasicTableStructureDto newBasicTableStructureDto = new BasicTableStructureDto();
                newBasicTableStructureDto.setTableName(parentTableDto.getTableName());
                newBasicTableStructureDto.setPrimaryKeyName(parentTableDto.getPrimaryKeyName());

                MigrationDto newMigrationDto = MigrationDto.builder().tableName(parentTableDto.getTableName()).basicTableStructureDto(newBasicTableStructureDto).primaryKeys(primaryKeysProd).isSearchedReference(false).build();

                MigrationVo.setListMigration(newMigrationDto);
            }
        }
    }
}
