package com.br.migrationTool.validations;

import com.br.migrationTool.configs.MessagePropertiesReader;
import com.br.migrationTool.dtos.migration.BasicTableStructureDto;
import com.br.migrationTool.dtos.migration.MigrationDto;
import com.br.migrationTool.exceptions.AllItemsMigratedException;
import com.br.migrationTool.exceptions.ItemLimitMigrationExceededException;
import com.br.migrationTool.exceptions.ItemNotFoundException;
import com.br.migrationTool.utils.OwnerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

@Component
public class MigrationValidation {

    @Value("${item.limit.migration}")
    private int itemLimitMigration;
    @Autowired
    private OwnerUtils ownerUtils;
    @Autowired
    MessagePropertiesReader messagePropertiesReader;

    public void isIdsLimitValid(List<String> ids) {
        if (ids.size() > itemLimitMigration) {
            throw new ItemLimitMigrationExceededException(
                MessageFormat.format(
                    messagePropertiesReader.getMessage("limit.Exceeded"), itemLimitMigration
                )
            );
        }
    }

    public void isNoItemsFound(List<String> primaryKeysExistingInProd) {
        if (primaryKeysExistingInProd.size() == 0) {
            throw new ItemNotFoundException(messagePropertiesReader.getMessage("item.not.found"));
        }
    }

    public void isAllMigratedItems(List<MigrationDto> MigrationDtos) {
        if (MigrationDtos.size() == 0) {
            throw new AllItemsMigratedException(messagePropertiesReader.getMessage("all.migrated.items"));
        }
    }

    public void isTableExists(BasicTableStructureDto basicTableStructureDto, String tableName) {
        if (basicTableStructureDto == null) {
            throw new ItemNotFoundException(
                MessageFormat.format(
                    messagePropertiesReader.getMessage("table.not.found"),
                    tableName.toLowerCase(Locale.ROOT),
                    ownerUtils.getOwner(true).toLowerCase(Locale.ROOT)
                )
            );
        }
    }
}
