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
        if (isTableNameExistInListMigration(tableName)) {
            setPrimaryKeysInTableExistingInListMigration(tableName, primaryKeys);
        } else {
            setNewTableNameAndPrimaryKeyInListMigration(tableName, primaryKeys);
        }
    }

    public static List<MigrationVo> getListMigration() {
        return listMigrationVo;
    }

    private static void setNewTableNameAndPrimaryKeyInListMigration(String tableName, List<String> primaryKeys) {
        listMigrationVo.add(
                MigrationVo.builder()
                        .tableName(tableName)
                        .primaryKeys(primaryKeys.stream().distinct().collect(Collectors.toList()))
                        .build()
        );
    }

    private static void setPrimaryKeysInTableExistingInListMigration(String tableName, List<String> primaryKeys) {
        for (MigrationVo migrationVo : listMigrationVo) {
            if (migrationVo.getTableName().equals(tableName)) {
                for (String primaryKey : primaryKeys) {
                    if (!primaryKeys.contains(primaryKey)) {
                        migrationVo.getPrimaryKeys().add(primaryKey);
                    }
                }
            }
        }
    }

    private static boolean isTableNameExistInListMigration(String tableName) {
        return listMigrationVo.stream().map(
                MigrationVo::getTableName
        ).collect(Collectors.toList()).contains(tableName);
    }
}


