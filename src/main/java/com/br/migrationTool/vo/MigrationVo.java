package com.br.migrationTool.vo;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class MigrationVo {

    private String tableName;
    private List<String> primaryKeys;
    private static List<MigrationVo> listMigrationVo = new ArrayList<>();

    public static void setListMigration(String tableName, List<String> primaryKeys) {
        if (isExistTableNameInListMigration(tableName)) {
            setPrimaryKeysInExistingTableListMigration(tableName, primaryKeys);
        } else {
            setNewTableNameAndPrimaryKeyInListMigration(tableName, primaryKeys);
        }
    }

    public static List<MigrationVo> getListMigration() {
        return listMigrationVo;
    }

    public static List<String> getPrimaryKeysMigrationByTableName(String tableName) {
        return getMigrationByTableName(tableName).getPrimaryKeys();
    }

    public static List<String> getAllTableInMigrationList() {
        return MigrationVo.getListMigration().stream().map(MigrationVo::getTableName)
                .collect(Collectors.toList());
    }

    public static MigrationVo getMigrationByTableName(String tableName) {
        return listMigrationVo.stream().filter(migrationVo -> migrationVo.getTableName().equals(tableName)).findAny().orElse(null);
    }

    private static void setNewTableNameAndPrimaryKeyInListMigration(String tableName, List<String> primaryKeys) {
        listMigrationVo.add(
                MigrationVo.builder()
                        .tableName(tableName)
                        .primaryKeys(primaryKeys.stream().distinct().collect(Collectors.toList()))
                        .build()
        );
    }

    private static void setPrimaryKeysInExistingTableListMigration(String tableName, List<String> primaryKeys) {
        for (MigrationVo migrationVo : listMigrationVo) {
            if (migrationVo.getTableName().equals(tableName)) {
                for (String primaryKey : primaryKeys) {
                    if (!migrationVo.getPrimaryKeys().contains(primaryKey)) {
                        migrationVo.getPrimaryKeys().add(primaryKey);
                    }
                }
            }
        }
    }

    private static boolean isExistTableNameInListMigration(String tableName) {
        return listMigrationVo.stream().map(
                MigrationVo::getTableName
        ).collect(Collectors.toList()).contains(tableName);
    }
}


