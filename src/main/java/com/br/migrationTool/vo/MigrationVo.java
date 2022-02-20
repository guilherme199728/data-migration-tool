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

    public static void setListMigration(String tableName, List<String> primaryKeys) {
        MigrationVo migrationVo = getMigrationByTableName(tableName);
        if(migrationVo != null) {
            for (String primaryKey : primaryKeys) {
                if (!migrationVo.getPrimaryKeys().contains(primaryKey)) {
                    migrationVo.getPrimaryKeys().add(primaryKey);
                }
            }
        } else {
            listMigrationVo.add(
                MigrationVo.builder()
                .tableName(tableName)
                .primaryKeys(primaryKeys.stream().distinct().collect(Collectors.toList()))
                .build()
            );
        }
    }
}


