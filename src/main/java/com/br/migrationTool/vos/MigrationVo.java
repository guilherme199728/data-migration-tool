package com.br.migrationTool.vos;

import com.br.migrationTool.dtos.migration.MigrationDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
        return getMigrationByTableName(tableName).getPrimaryKeys();
    }

    public List<String> getAllTableInMigrationList() {
        return getListMigration().stream().map(MigrationDto::getTableName)
            .collect(Collectors.toList());
    }

    public MigrationDto getMigrationByTableName(String tableName) {
        return listMigrationVo.stream().filter(migrationVo -> migrationVo.getTableName().equals(tableName)).findAny().orElse(null);
    }

    public void removePrimaryKeysListMigrationByTableName(String tableName, List<String> primaryKeys) {
        MigrationDto migrationDto = getMigrationByTableName(tableName);

        primaryKeys.forEach(primaryKeyForRemove -> migrationDto.getPrimaryKeys().remove(primaryKeyForRemove));

        if (migrationDto.getPrimaryKeys().size() == 0) {
            getListMigration().remove(migrationDto);
        }

    }

    public void setListMigration(MigrationDto migrationDto) {
        MigrationDto migrationDtoExisting = getMigrationByTableName(migrationDto.getTableName());
        if (migrationDtoExisting != null) {
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
                    .basicTableStructureDto(migrationDto.getBasicTableStructureDto())
                    .build()
            );
        }
    }

    public boolean isAllReferencesSearched() {
        return listMigrationVo.stream().filter(migrationDto -> ((Boolean) migrationDto.isSearchedReference()).equals(false)).findAny().orElse(null) != null;
    }

    public void setSearchedReferenceByTableName(String tableName, boolean isSearchedReference) {
        getMigrationByTableName(tableName).setSearchedReference(isSearchedReference);
    }

    public void clearMigrationList() {
        listMigrationVo = new ArrayList<>();
    }

    public List<MigrationDto> cloneMigration() {
        return new ArrayList<>(listMigrationVo);
    }
}


