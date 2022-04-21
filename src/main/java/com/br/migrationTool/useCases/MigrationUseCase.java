package com.br.migrationTool.useCases;

import com.br.migrationTool.datas.daos.MigrationDao;
import com.br.migrationTool.datas.daos.TableReferencesDao;
import com.br.migrationTool.dtos.migration.MigrationDto;
import com.br.migrationTool.dtos.migration.ParentTableDto;
import com.br.migrationTool.dtos.migration.TableStructureDto;
import com.br.migrationTool.dtos.rest.DataMigrationToolDto;
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

    public void start(DataMigrationToolDto dataMigrationToolDto) throws SQLException {
        addInitialTableToMigrationListByRange(dataMigrationToolDto.getTableName(), dataMigrationToolDto.getIds());
        createMigrationList();
        migrationDao.executeMigration();
    }

    private void addInitialTableToMigrationListByRange(String initialTableName, List<String> primaryKeyList) throws SQLException {
         TableStructureDto tableStructureDto = tableReferencesDao.getTableDtoFromConstraint(initialTableName, true);

        MigrationDto migrationDto = MigrationDto.builder()
                .tableName(initialTableName)
                .tableStructureDto(tableStructureDto)
                .isSearchedReference(false)
                .build();

        List<String> primaryKeysExistingInHomolog = tableReferencesDao.getPrimaryKeysByRange(
                migrationDto.getTableName(),
                migrationDto.getTableStructureDto().getPrimaryKeyName(),
                migrationDto.getTableStructureDto().getPrimaryKeyName(),
                primaryKeyList,
                false
        );

        List<String> primaryKeysExistingInProd = tableReferencesDao.getPrimaryKeysByRange(
                migrationDto.getTableName(),
                migrationDto.getTableStructureDto().getPrimaryKeyName(),
                migrationDto.getTableStructureDto().getPrimaryKeyName(),
                primaryKeyList,
                true
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
                    List<ParentTableDto> parentTableDtos = tableReferencesDao.getParentTablesFromConstraint(
                            migrationDto.getTableName(), true
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

            List<String> primaryKeysProd = tableReferencesDao.getPrimaryKeysByParentTable(
                    migrationDto, parentTableDto, true
            );

            if (!primaryKeysProd.isEmpty()) {
                TableStructureDto newTableStructureDto = new TableStructureDto();
                newTableStructureDto.setTableName(parentTableDto.getTableName());
                newTableStructureDto.setPrimaryKeyName(parentTableDto.getPrimaryKeyName());

                MigrationDto newMigrationDto = MigrationDto.builder()
                        .tableName(parentTableDto.getTableName())
                        .tableStructureDto(newTableStructureDto)
                        .primaryKeys(primaryKeysProd)
                        .isSearchedReference(false)
                        .build();

                MigrationVo.setListMigration(newMigrationDto);
            }

        }
    }
}
