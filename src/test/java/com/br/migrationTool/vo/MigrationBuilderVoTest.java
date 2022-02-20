package com.br.migrationTool.vo;

import com.br.migrationTool.builders.PrimaryKeysBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.br.migrationTool.builders.PrimaryKeysBuilder.*;
import static org.junit.jupiter.api.Assertions.*;

class MigrationBuilderVoTest {
    private static final String TABLE_TEST = "TEST";
    private static final int INDEX_LIST_MIGRATION_INSERTED = 0;

    @Test
    void shouldNotInsertPrimaryKeysRepeatedInMigrationList() {
        // Arrange
        List<String> primaryKeys = onePrimaryKeysBuilder().builder();

        // Act
        MigrationVo.setListMigration(TABLE_TEST, primaryKeys);

        // Assert
        assertTrue(isItemsDuplicated(MigrationVo.getListMigration().get(INDEX_LIST_MIGRATION_INSERTED).getPrimaryKeys()));
    }

    @Test
    void shouldNotInsertTableNamesRepeatedInMigrationList() {
        // Arrange
        insertRepeatedTableName();
        // Act
        MigrationVo.setListMigration(TABLE_TEST, onePrimaryKeysBuilder().builder());

        // Assert
        assertTrue(
                isItemsDuplicated(MigrationVo.getListMigration().stream().map(MigrationVo::getTableName)
                        .collect(Collectors.toList())
                )
        );
    }

    private boolean isItemsDuplicated(List<String> primaryKeys) {
        Set<String> items = new HashSet<>();
        return primaryKeys.stream().filter(n -> !items.add(n)).collect(Collectors.toSet()).size() == 0;
    }

    private void insertRepeatedTableName() {
        for (int i = 0; i < 9; i++) {
            if (i < 4) {
                MigrationVo.setListMigration(
                        TABLE_TEST, onePrimaryKeysBuilder().builder()
                );
            } else {
                MigrationVo.setListMigration(
                        TABLE_TEST + i, onePrimaryKeysBuilder().builder()
                );
            }
        }
    }
}