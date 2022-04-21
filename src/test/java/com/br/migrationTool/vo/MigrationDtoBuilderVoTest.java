package com.br.migrationTool.vo;

import com.br.migrationTool.vos.MigrationVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.br.migrationTool.builders.MigrationDtoBuilder.oneMigration;
import static com.br.migrationTool.builders.PrimaryKeysBuilder.oneListPrimaryKeysBuilder;
import static com.br.migrationTool.builders.PrimaryKeysBuilder.oneListPrimaryKeysOneElementBuilder;
import static org.junit.jupiter.api.Assertions.*;

class MigrationDtoBuilderVoTest {
    private static final String TABLE_NAME_TEST = "TEST";

    @BeforeEach
    void setup() {
        MigrationVo.clearMigrationList();
    }

    @Test
    void shouldNotInsertTableNamesRepeatedInMigrationList() {
        // Arrange
        insertTableNames();

        // Act
        MigrationVo.setListMigration(oneMigration().build());

        // Assert
        assertTrue(isItemsNotDuplicated(MigrationVo.getAllTableInMigrationList()));
    }

    @Test
    void shouldNotInsertPrimaryKeysRepeatedInMigrationList() {
        // Arrange
        List<String> primaryKeys = oneListPrimaryKeysBuilder().build();

        // Act
        MigrationVo.setListMigration(oneMigration().build());
        MigrationVo.setListMigration(oneMigration().build());

        // Assert
        assertTrue(isItemsNotDuplicated(MigrationVo.getPrimaryKeysMigrationByTableName(TABLE_NAME_TEST)));
    }

    @Test
    void shouldInsertPrimaKeysInExistingTableIfTableExistingInMigrationList() {
        // Arrange
        MigrationVo.setListMigration(oneMigration().build());
        List<String> primaryKeys = oneListPrimaryKeysOneElementBuilder("55").build();

        // Act
        MigrationVo.setListMigration(oneMigration().withPrimaryKeys(primaryKeys).build());

        // Assert
        assertTrue(MigrationVo.getMigrationByTableName(TABLE_NAME_TEST).getPrimaryKeys().contains("55"));
    }

    @Test
    void shouldSearchTableNameInMigrationList() {
        // Arrange | Act
        insertTableNames();

        // Assert
        assertEquals(TABLE_NAME_TEST, MigrationVo.getMigrationByTableName(TABLE_NAME_TEST).getTableName());
    }

    @Test
    void shouldRemovePrimaryKeysInSpecificTable() {
        // Arrange
        MigrationVo.setListMigration(oneMigration().build());

        // Act
        MigrationVo.removePrimaryKeysListMigrationByTableName(TABLE_NAME_TEST, oneListPrimaryKeysOneElementBuilder("1").build());

        // Assert
        assertFalse(MigrationVo.getMigrationByTableName(TABLE_NAME_TEST).getPrimaryKeys().contains("1"));
    }

    @Test
    void shouldRemoveMigrationIfPrimaryKeysEmpty() {
        // Arrange
        List<String> primaryKeys = oneListPrimaryKeysBuilder().build();
        MigrationVo.setListMigration(oneMigration().withPrimaryKeys(primaryKeys).build());

        // Act
        MigrationVo.removePrimaryKeysListMigrationByTableName(TABLE_NAME_TEST, primaryKeys);

        // Assert
        assertEquals(0, MigrationVo.getListMigration().size());
    }

    private boolean isItemsNotDuplicated(List<String> items) {
        Set<String> itemsAxu = new HashSet<>();
        return items.stream().filter(n -> !itemsAxu.add(n)).collect(Collectors.toSet()).size() == 0;
    }

    private void insertTableNames() {
        for (int i = 0; i < 9; i++) {
            if (i < 4) {
                MigrationVo.setListMigration(oneMigration().withTableName(TABLE_NAME_TEST).build());
            } else {
                MigrationVo.setListMigration(oneMigration().withTableName(TABLE_NAME_TEST + 1).build());
            }
        }
    }
}