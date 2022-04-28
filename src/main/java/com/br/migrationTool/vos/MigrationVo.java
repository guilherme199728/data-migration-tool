package com.br.migrationTool.vos;

import com.br.migrationTool.dtos.migration.MigrationDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@Component
public class MigrationVo {
    private List<MigrationDto> listMigrationVo;

    public List<MigrationDto> getListMigration() {
        return listMigrationVo;
    }

    public List<String> getPrimaryKeysMigrationByTableName(String tableName) {
        return getFirstMigrationByTableName(tableName).getPrimaryKeys();
    }

    public List<String> getAllTableInMigrationList() {
        return getListMigration().stream().map(MigrationDto::getTableName)
            .collect(Collectors.toList());
    }

    public List<MigrationDto> getAllMigrationByTableName(String tableName) {
        return listMigrationVo.stream().filter(migrationVo -> migrationVo.getTableName().equals(tableName)).toList();
    }

    public MigrationDto getFirstMigrationByTableName(String tableName) {
        return listMigrationVo.stream().filter(migrationVo -> migrationVo.getTableName().equals(tableName)).findAny().orElse(null);
    }

    public void removePrimaryKeysListMigrationByTableName(String tableName, List<String> primaryKeys) {
        MigrationDto migrationDto = getFirstMigrationByTableName(tableName);

        primaryKeys.forEach(primaryKeyForRemove -> migrationDto.getPrimaryKeys().remove(primaryKeyForRemove));

        if (migrationDto.getPrimaryKeys().size() == 0) {
            getListMigration().remove(migrationDto);
        }

    }

    public void setListMigration(MigrationDto newMigrationDto) {

        List<MigrationDto> migrationDtosExisting = getAllMigrationByTableName(newMigrationDto.getTableName());
        List<String> allPrimaryKeysExisting = migrationDtosExisting.stream().map(MigrationDto::getPrimaryKeys).flatMap(List::stream).toList();
        List<String> newPrimaryKeys = newMigrationDto.getPrimaryKeys();
        List<String> primaryKeysForAdded = newPrimaryKeys.stream().filter(newPrimaryKey -> !allPrimaryKeysExisting.contains(newPrimaryKey)).toList();

        if (migrationDtosExisting.size() == 0) {
            newMigrationDto.setPrimaryKeys(newMigrationDto.getPrimaryKeys().stream().distinct().collect(Collectors.toList()));
            listMigrationVo.add(newMigrationDto);
        } else if (primaryKeysForAdded.size() != 0) {
            newMigrationDto.setPrimaryKeys(primaryKeysForAdded.stream().distinct().collect(Collectors.toList()));
            listMigrationVo.add(newMigrationDto);
        }
    }

    public boolean isAllReferencesSearched() {
        return listMigrationVo.stream().filter(migrationDto -> ((Boolean) migrationDto.isSearchedReference()).equals(false)).findAny().orElse(null) != null;
    }

    public void setSearchedReferenceByTableName(String tableName, boolean isSearchedReference) {
        getAllMigrationByTableName(tableName).forEach(
            migrationDto -> migrationDto.setSearchedReference(isSearchedReference)
        );
    }

    public void organizeListMigration() {

        listMigrationVo.stream().map(MigrationDto::getTableName).toList();

        Collections.reverse(listMigrationVo);
    }

    public void clearMigrationList() {
        listMigrationVo = new ArrayList<>();
    }

    public List<MigrationDto> cloneMigration() {
        return new ArrayList<>(listMigrationVo);
    }
}


