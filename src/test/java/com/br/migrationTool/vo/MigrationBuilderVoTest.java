package com.br.migrationTool.vo;

import com.br.migrationTool.builders.PrimaryKeysBuilder;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.br.migrationTool.builders.PrimaryKeysBuilder.*;
import static com.br.migrationTool.builders.PrimaryKeysBuilder.oneListPrimaryKeysBuilder;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MigrationBuilderVoTest {
    private static final String TABLE_TEST = "TEST";

    @Test
    void shouldNotInsertTableNamesRepeatedInMigrationList() {
        // Arrange
        insertTableNames();

        // Act
        MigrationVo.setListMigration(TABLE_TEST, oneListPrimaryKeysBuilder().build());

        // Assert
        assertTrue(isItemsNotDuplicated(MigrationVo.getAllTableInMigrationList()));
    }

    @Test
    void shouldNotInsertPrimaryKeysRepeatedInMigrationList() {
        // Arrange
        List<String> primaryKeys = oneListPrimaryKeysBuilder().build();

        // Act
        MigrationVo.setListMigration(TABLE_TEST, primaryKeys);
        MigrationVo.setListMigration(TABLE_TEST, primaryKeys);

        // Assert
        assertTrue(isItemsNotDuplicated(MigrationVo.getPrimaryKeysMigrationByTableName(TABLE_TEST)));
    }

    @Test
    void shouldInsertPrimaKeysInExistingTableIfTableExistingInMigrationList() {
        // Arrange
        MigrationVo.setListMigration(TABLE_TEST, oneListPrimaryKeysBuilder().build());
        List<String> primaryKeys = onListPrimaryKeysOneElementBuilder("55").build();

        // Act
        MigrationVo.setListMigration(TABLE_TEST, primaryKeys);

        // Assert
        assertTrue(MigrationVo.getMigrationByTableName(TABLE_TEST).getPrimaryKeys().contains("55"));
    }

    private boolean isItemsNotDuplicated(List<String> items) {
        Set<String> itemsAxu = new HashSet<>();
        return items.stream().filter(n -> !itemsAxu.add(n)).collect(Collectors.toSet()).size() == 0;
    }

    private void insertTableNames() {
        for (int i = 0; i < 9; i++) {
            if (i < 4) {
                MigrationVo.setListMigration(
                        TABLE_TEST, oneListPrimaryKeysBuilder().build()
                );
            } else {
                MigrationVo.setListMigration(
                        TABLE_TEST + i, oneListPrimaryKeysBuilder().build()
                );
            }
        }
    }
}