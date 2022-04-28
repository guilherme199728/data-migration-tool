package com.br.migrationTool.validations;

import com.br.migrationTool.configs.MessagePropertiesReader;
import com.br.migrationTool.dtos.migration.MigrationDto;
import com.br.migrationTool.exceptions.AllItemsMigratedException;
import com.br.migrationTool.exceptions.ItemLimitMigrationExceededException;
import com.br.migrationTool.exceptions.ItemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;

@Component
public class MigrationValidation {
    @Value("${item.limit.migration}")
    private int itemLimitMigration;

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

    public void isAllMigratedItems(List<MigrationDto> allMigrationDto) {
        if (allMigrationDto.size() == 0) {
            throw new AllItemsMigratedException(messagePropertiesReader.getMessage("all.migrated.items"));
        }
    }
}
