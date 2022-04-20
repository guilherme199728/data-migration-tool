package com.br.migrationTool.useCase;

import com.br.migrationTool.data.dao.MigrationDao;
import com.br.migrationTool.data.dao.TableReferencesDao;
import com.br.migrationTool.dto.MigrationDto;
import com.br.migrationTool.dto.ParentTableDto;
import com.br.migrationTool.dto.TableStructureDto;
import com.br.migrationTool.vo.MigrationVo;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class MigrationUseCase {
    public void start() throws SQLException {
        String initialTableName = "T_VENDA";
        List<String> list = Arrays.asList(new String[] { "23257752" });

        addInitialTableToMigrationListByRange(initialTableName, list);
        createMigrationList();
        MigrationDao.executeMigration();
    }

    private void addInitialTableToMigrationListByRange(String initialTableName, List<String> primaryKeyList) throws SQLException {
         TableStructureDto tableStructureDto = TableReferencesDao.getTableDtoFromConstraint(initialTableName, true);

        MigrationDto migrationDto = MigrationDto.builder()
                .tableName(initialTableName)
                .tableStructureDto(tableStructureDto)
                .isSearchedReference(false)
                .build();

        List<String> primaryKeysExistingInHomolog = TableReferencesDao.getPrimaryKeysByRange(
                migrationDto.getTableName(),
                migrationDto.getTableStructureDto().getPrimaryKeyName(),
                migrationDto.getTableStructureDto().getPrimaryKeyName(),
                primaryKeyList,
                false
        );

        List<String> primaryKeysExistingInProd = TableReferencesDao.getPrimaryKeysByRange(
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
                    List<ParentTableDto> parentTableDtos = TableReferencesDao.getParentTablesFromConstraint(
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

            List<String> primaryKeysProd = TableReferencesDao.getPrimaryKeysByParentTable(
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
