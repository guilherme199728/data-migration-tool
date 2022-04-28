package com.br.migrationTool.useCases;

import com.br.migrationTool.configs.MessagePropertiesReader;
import com.br.migrationTool.datas.daos.DataTableDao;
import com.br.migrationTool.datas.daos.MigrationDao;
import com.br.migrationTool.datas.daos.TableReferencesDao;
import com.br.migrationTool.dtos.migration.*;
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

@Component
public class MigrationUseCase {

    @Autowired
    TableReferencesDao tableReferencesDao;
    @Autowired
    MigrationDao migrationDao;
    @Autowired
    DataTableDao dataTableDao;
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
        addTableToMigrationList(tableName, ids);
        migrationValidation.isAllMigratedItems(migrationVo.getListMigration());
        createMigrationList();
        migrationDao.executeMigration(migrationVo);
    }

    private void addTableToMigrationList(
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
    }

    private void createMigrationList() throws SQLException {

        while (migrationVo.isAllReferencesSearched()) {

            List<MigrationDto> migrationsDto = migrationVo.cloneMigration();

            for (MigrationDto migrationDto : migrationsDto) {

                if (!migrationDto.isSearchedReference()) {
                    List<BasicTableStructureDto> parentTables = tableReferencesDao.getParentTablesFromConstraint(
                        migrationDto.getTableName(), true
                    );

                    if (searchFieldsWithoutReference) {
                        searchFieldsWithoutReference(migrationDto, parentTables);
                    }

                    if (parentTables.size() > 0) {
                        addParentsToMigrationList(parentTables, migrationDto);
                        migrationVo.setSearchedReferenceByTableName(migrationDto.getTableName(), true);
                    } else {
                        migrationVo.setSearchedReferenceByTableName(migrationDto.getTableName(), true);
                    }
                }
            }
        }
    }

    private void searchFieldsWithoutReference(
        MigrationDto migrationDto, List<BasicTableStructureDto> parentTables
    ) throws SQLException {

        List<NamesTypesFieldsTableDto> namesTypesFieldsTableDtos =
            tableReferencesDao.getAllNamesAndTypeColumnsTableFromTableName(
                migrationDto.getTableName(), false
            );

        namesTypesFieldsTableDtos = namesTypesFieldsTableDtos.stream().filter(
            namesTypesFieldsTableDto ->  !(namesTypesFieldsTableDto.getFieldName().equals(
                migrationDto.getBasicTableStructureDto().getPrimaryKeyName())
            )
        ).toList();

        for (NamesTypesFieldsTableDto namesTypesFieldsTableDto : namesTypesFieldsTableDtos) {

            if (namesTypesFieldsTableDto.getFieldName().startsWith(prefixId)) {
                String tableNameByField = namesTypesFieldsTableDto.getFieldName().replace(prefixId, prefixTable);

                int tableShared = parentTables.stream()
                    .filter(parentTableDto -> parentTableDto.getTableName().equals(tableNameByField))
                    .toList()
                    .size();

                if (tableShared == 0) {

                    try {
                        List<NamesTypesFieldsTableDto> namesTypesFieldsTableDtosWithoutReference =
                            tableReferencesDao.getAllNamesAndTypeColumnsTableFromTableName(
                                tableNameByField, false
                            );

                        if (isFieldWithoutReference(namesTypesFieldsTableDto, namesTypesFieldsTableDtosWithoutReference)) {
                            BasicTableStructureDto parentTable = new BasicTableStructureDto();
                            parentTable.setTableName(tableNameByField);
                            parentTable.setPrimaryKeyName(namesTypesFieldsTableDto.getFieldName());
                            parentTable.setForeignKeyName(namesTypesFieldsTableDto.getFieldName());
                            parentTables.add(parentTable);
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

    private boolean isFieldWithoutReference(NamesTypesFieldsTableDto namesTypesFieldsTableDto, List<NamesTypesFieldsTableDto> namesTypesFieldsTableDtosWithoutReference) {
        return namesTypesFieldsTableDtosWithoutReference
            .stream()
            .map(NamesTypesFieldsTableDto::getFieldName)
            .toList()
            .contains(namesTypesFieldsTableDto.getFieldName());
    }

    private void addParentsToMigrationList(
        List<BasicTableStructureDto> parentTables, MigrationDto migrationDto
    ) throws SQLException {

        for (BasicTableStructureDto parentTable : parentTables) {

            List<String> primaryKeysProd = dataTableDao.getPrimaryKeysByParentTable(
                migrationDto, parentTable, true
            );

            if (!primaryKeysProd.isEmpty()) {
                BasicTableStructureDto newBasicTableStructureDto = new BasicTableStructureDto();
                newBasicTableStructureDto.setTableName(parentTable.getTableName());
                newBasicTableStructureDto.setPrimaryKeyName(parentTable.getPrimaryKeyName());

                MigrationDto newMigrationDto = MigrationDto.builder()
                    .tableName(parentTable.getTableName())
                    .basicTableStructureDto(newBasicTableStructureDto)
                    .primaryKeys(primaryKeysProd)
                    .isSearchedReference(false)
                    .level(migrationVo.getListMigration().size())
                    .build();

                migrationVo.setListMigration(newMigrationDto);
            }
        }
    }
}
