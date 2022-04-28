package com.br.migrationTool.useCases;

import com.br.migrationTool.configs.MessagePropertiesReader;
import com.br.migrationTool.datas.daos.MigrationDao;
import com.br.migrationTool.datas.daos.TableReferencesDao;
import com.br.migrationTool.dtos.migration.MigrationDto;
import com.br.migrationTool.dtos.migration.ParentTableDto;
import com.br.migrationTool.dtos.migration.BasicTableStructureDto;
import com.br.migrationTool.dtos.migration.TableDataDto;
import com.br.migrationTool.validations.MigrationValidation;
import com.br.migrationTool.vos.MigrationVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class MigrationUseCase {

    @Autowired
    TableReferencesDao tableReferencesDao;
    @Autowired
    MigrationDao migrationDao;
    @Autowired
    MigrationValidation migrationValidation;
    @Autowired
    MessagePropertiesReader messagePropertiesReader;
    @Autowired
    MigrationVo migrationVo;
    @Value("${search.fields.without.reference}")
    private boolean searchFieldsWithoutReference;
    @Value("${prefix.table}")
    private String prefixTable;
    @Value("${prefix.id}")
    private String prefixId;

    private static final Logger logger = LoggerFactory.getLogger(MigrationUseCase.class);

    public void process(String tableName, List<String> ids) throws SQLException {
        addInitialTableToMigrationListByRange(tableName, ids);
        createMigrationList();
        migrationDao.executeMigration(migrationVo.getListMigration());
    }

    private void addInitialTableToMigrationListByRange(
        String initialTableName, List<String> primaryKeyList
    ) throws SQLException {

        BasicTableStructureDto basicTableStructureDto = tableReferencesDao.getBasicTableStructureFromConstraint(
            initialTableName, true
        );

        MigrationDto migrationDto = MigrationDto.builder()
            .tableName(initialTableName)
            .basicTableStructureDto(basicTableStructureDto)
            .isSearchedReference(false)
            .build();

        List<String> primaryKeysExistingInHomolog = tableReferencesDao.getPrimaryKeys(
            migrationDto.getTableName(),
            migrationDto.getBasicTableStructureDto().getPrimaryKeyName(),
            migrationDto.getBasicTableStructureDto().getPrimaryKeyName(),
            primaryKeyList,
            false
        );

        List<String> primaryKeysExistingInProd = tableReferencesDao.getPrimaryKeys(
            migrationDto.getTableName(),
            migrationDto.getBasicTableStructureDto().getPrimaryKeyName(),
            migrationDto.getBasicTableStructureDto().getPrimaryKeyName(),
            primaryKeyList,
            true
        );

        migrationValidation.isNoItemsFound(primaryKeysExistingInProd);
        migrationDto.setPrimaryKeys(primaryKeysExistingInProd);
        migrationVo.setListMigration(migrationDto);
        migrationVo.removePrimaryKeysListMigrationByTableName(migrationDto.getTableName(), primaryKeysExistingInHomolog);
        migrationValidation.isAllMigratedItems(migrationVo.getListMigration());
    }

    private void createMigrationList() throws SQLException {

        while (migrationVo.isAllReferencesSearched()) {

            List<MigrationDto> migrationsDto = migrationVo.cloneMigration();

            for (MigrationDto migrationDto : migrationsDto) {

                if (!migrationDto.isSearchedReference()) {
                    List<ParentTableDto> parentTableDtos = tableReferencesDao.getParentTablesFromConstraint(
                        migrationDto.getTableName(), true
                    );

                    if (searchFieldsWithoutReference) {
                        searchFieldsWithoutReference(migrationDto, parentTableDtos);
                    }

                    if (parentTableDtos.size() > 0) {
                        addParentsToMigrationList(parentTableDtos, migrationDto);
                        migrationVo.setSearchedReferenceByTableName(migrationDto.getTableName(), true);
                    } else {
                        migrationVo.setSearchedReferenceByTableName(migrationDto.getTableName(), true);
                    }
                }
            }
        }
    }

    private void searchFieldsWithoutReference(
        MigrationDto migrationDto, List<ParentTableDto> parentTableDtos
    ) throws SQLException {

        HashMap<String, String> allNamesAndTypeColumns = tableReferencesDao.getAllNamesAndTypeColumnsTableFromTableName(
            migrationDto.getTableName(), false
        );

        allNamesAndTypeColumns.remove(migrationDto.getBasicTableStructureDto().getPrimaryKeyName());

        for (Map.Entry<String, String> set : allNamesAndTypeColumns.entrySet()) {

            String fieldTable = set.getKey();

            if (fieldTable.startsWith(prefixId)) {
                String tableNameByField = fieldTable.replace(prefixId, prefixTable);

                int tableShared = parentTableDtos.stream()
                    .filter(parentTableDto -> parentTableDto.getTableName().equals(tableNameByField))
                    .toList()
                    .size();

                if (tableShared == 0) {

                    try {
                        HashMap<String, String> allNamesAndTypeColumnsWithoutReference = tableReferencesDao.getAllNamesAndTypeColumnsTableFromTableName(
                            tableNameByField, false
                        );

                        if (allNamesAndTypeColumnsWithoutReference.containsKey(fieldTable)) {
                            ParentTableDto parentTableDto = new ParentTableDto();
                            parentTableDto.setTableName(tableNameByField);
                            parentTableDto.setPrimaryKeyName(fieldTable);
                            parentTableDto.setForeingKeyName(fieldTable);
                            parentTableDtos.add(parentTableDto);
                        }
                    } catch (SQLException e) {
                        logger.error(messagePropertiesReader.getMessage(
                                MessageFormat.format("table.without.reference.item.not.found", tableNameByField)
                            )
                        );
                    }
                }
            }
        }
    }

    private void addParentsToMigrationList(
        List<ParentTableDto> parentTableDtos, MigrationDto migrationDto
    ) throws SQLException {

        for (ParentTableDto parentTableDto : parentTableDtos) {

            List<String> primaryKeysProd = tableReferencesDao.getPrimaryKeysByParentTable(
                migrationDto, parentTableDto, true
            );

            if (!primaryKeysProd.isEmpty()) {
                BasicTableStructureDto newBasicTableStructureDto = new BasicTableStructureDto();
                newBasicTableStructureDto.setTableName(parentTableDto.getTableName());
                newBasicTableStructureDto.setPrimaryKeyName(parentTableDto.getPrimaryKeyName());

                MigrationDto newMigrationDto = MigrationDto.builder()
                    .tableName(parentTableDto.getTableName())
                    .basicTableStructureDto(newBasicTableStructureDto)
                    .primaryKeys(primaryKeysProd)
                    .isSearchedReference(false)
                    .build();

                migrationVo.setListMigration(newMigrationDto);
            }
        }
    }
}
