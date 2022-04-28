package com.br.migrationTool.vo;

import com.br.migrationTool.vos.MigrationVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.br.migrationTool.builders.MigrationDtoBuilder.oneMigration;
import static com.br.migrationTool.builders.PrimaryKeysBuilder.oneListPrimaryKeysBuilder;
import static com.br.migrationTool.builders.PrimaryKeysBuilder.oneListPrimaryKeysOneElementBuilder;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MigrationDtoBuilderVoTest {
    private static final String TABLE_NAME_TEST = "TEST";

    @Autowired
    MigrationVo migrationVo;

    @BeforeEach
    void setup() {
        migrationVo.clearMigrationList();
    }

    @Test
    void shouldNotInsertTableNamesRepeatedInMigrationList() {
        // Arrange
        insertTableNames();

        // Act
        migrationVo.setListMigration(oneMigration().build());

        // Assert
        assertTrue(isItemsNotDuplicated(migrationVo.getAllTableInMigrationList()));
    }

    @Test
    void shouldNotInsertPrimaryKeysRepeatedInMigrationList() {
        // Arrange
        List<String> primaryKeys = oneListPrimaryKeysBuilder().build();

        // Act
        migrationVo.setListMigration(oneMigration().build());
        migrationVo.setListMigration(oneMigration().build());

        // Assert
        assertTrue(isItemsNotDuplicated(migrationVo.getPrimaryKeysMigrationByTableName(TABLE_NAME_TEST)));
    }

    @Test
    void shouldInsertPrimaKeysInExistingTableIfTableExistingInMigrationList() {
        // Arrange
        migrationVo.setListMigration(oneMigration().build());
        List<String> primaryKeys = oneListPrimaryKeysOneElementBuilder("55").build();

        // Act
        migrationVo.setListMigration(oneMigration().withPrimaryKeys(primaryKeys).build());

        // Assert
        assertTrue(migrationVo.getMigrationByTableName(TABLE_NAME_TEST).getPrimaryKeys().contains("55"));
    }

    @Test
    void shouldSearchTableNameInMigrationList() {
        // Arrange | Act
        insertTableNames();

        // Assert
        assertEquals(TABLE_NAME_TEST, migrationVo.getMigrationByTableName(TABLE_NAME_TEST).getTableName());
    }

    @Test
    void shouldRemovePrimaryKeysInSpecificTable() {
        // Arrange
        migrationVo.setListMigration(oneMigration().build());

        // Act
        migrationVo.removePrimaryKeysListMigrationByTableName(TABLE_NAME_TEST, oneListPrimaryKeysOneElementBuilder("1").build());

        // Assert
        assertFalse(migrationVo.getMigrationByTableName(TABLE_NAME_TEST).getPrimaryKeys().contains("1"));
    }

    @Test
    void shouldRemoveMigrationIfPrimaryKeysEmpty() {
        // Arrange
        List<String> primaryKeys = oneListPrimaryKeysBuilder().build();
        migrationVo.setListMigration(oneMigration().withPrimaryKeys(primaryKeys).build());

        // Act
        migrationVo.removePrimaryKeysListMigrationByTableName(TABLE_NAME_TEST, primaryKeys);

        // Assert
        assertEquals(0, migrationVo.getListMigration().size());
    }

    private boolean isItemsNotDuplicated(List<String> items) {
        Set<String> itemsAxu = new HashSet<>();
        return items.stream().filter(n -> !itemsAxu.add(n)).collect(Collectors.toSet()).size() == 0;
    }

    private void insertTableNames() {
        for (int i = 0; i < 9; i++) {
            if (i < 4) {
                migrationVo.setListMigration(oneMigration().withTableName(TABLE_NAME_TEST).build());
            } else {
                migrationVo.setListMigration(oneMigration().withTableName(TABLE_NAME_TEST + 1).build());
            }
        }
    }
}