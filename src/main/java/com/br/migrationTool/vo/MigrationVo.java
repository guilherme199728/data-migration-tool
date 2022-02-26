package com.br.migrationTool.vo;

import com.br.migrationTool.dto.MigrationDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class MigrationVo {
    private static List<MigrationDto> listMigrationVo = new ArrayList<>();

    public static List<MigrationDto> getListMigration() {
        return listMigrationVo;
    }

    public static List<String> getPrimaryKeysMigrationByTableName(String tableName) {
        return getMigrationByTableName(tableName).getPrimaryKeys();
    }

    public static List<String> getAllTableInMigrationList() {
        return MigrationVo.getListMigration().stream().map(MigrationDto::getTableName)
                .collect(Collectors.toList());
    }

    public static MigrationDto getMigrationByTableName(String tableName) {
        return listMigrationVo.stream().filter(migrationVo -> migrationVo.getTableName().equals(tableName)).findAny().orElse(null);
    }

    public static void removePrimaryKeysListMigrationByTableName(String tableName, List<String> primaryKeys) {
        MigrationDto migrationDto = getMigrationByTableName(tableName);

        primaryKeys.forEach(primaryKeyForRemove -> migrationDto.getPrimaryKeys().remove(primaryKeyForRemove));

        if(migrationDto.getPrimaryKeys().size() == 0) {
            MigrationVo.getListMigration().remove(migrationDto);
        }

    }

    public static void setListMigration(MigrationDto migrationDto) {
        MigrationDto migrationDtoExisting = getMigrationByTableName(migrationDto.getTableName());
        if(migrationDtoExisting != null) {
            for (String primaryKey : migrationDto.getPrimaryKeys()) {
                if (!migrationDtoExisting.getPrimaryKeys().contains(primaryKey)) {
                    migrationDtoExisting.getPrimaryKeys().add(primaryKey);
                    migrationDtoExisting.setSearchedReference(migrationDto.isSearchedReference());
                }
            }
        } else {
            listMigrationVo.add(
                 MigrationDto.builder()
                .tableName(migrationDto.getTableName())
                .primaryKeys(migrationDto.getPrimaryKeys().stream().distinct().collect(Collectors.toList()))
                .tableDataDto(migrationDto.getTableDataDto())
                .build()
            );
        }
    }

    public static boolean isAllReferencesSearched() {
        return listMigrationVo.stream().filter(migrationDto -> ((Boolean) migrationDto.isSearchedReference()).equals(false)).findAny().orElse(null) != null;
    }

    public static void setSearchedReferenceByTableName(String tableName, boolean isSearchedReference) {
        getMigrationByTableName(tableName).setSearchedReference(isSearchedReference);
    }

    public static void clearMigrationList() {
        listMigrationVo = new ArrayList<>();
    }

    public static List<MigrationDto> cloneMigration() {
        return new ArrayList<>(listMigrationVo);
    }
}


