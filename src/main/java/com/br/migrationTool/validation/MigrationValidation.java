package com.br.migrationTool.validation;

import com.br.migrationTool.configs.MessagePropertiesReader;
import com.br.migrationTool.dtos.migration.MigrationDto;
import com.br.migrationTool.exception.AllItemsMigratedException;
import com.br.migrationTool.exception.ItemLimitMigrationExceededException;
import com.br.migrationTool.exception.ItemNotFoundException;
import com.br.migrationTool.vos.MigrationVo;
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
        if(ids.size() > itemLimitMigration) {
            throw new ItemLimitMigrationExceededException(
                    MessageFormat.format(
                            messagePropertiesReader.getMessage("limit.Exceeded"), itemLimitMigration
                    )
            );
        }
    }

    public void isNoItemsFound(List<String> primaryKeysExistingInProd) {
        if(primaryKeysExistingInProd.size() == 0) {
            throw new ItemNotFoundException(messagePropertiesReader.getMessage("item.not.found"));
        }
    }

    public void isAllMigratedItems(List<MigrationDto> allMigrationDto) {
        if (MigrationVo.getListMigration().size() == 0) {
            throw new AllItemsMigratedException(messagePropertiesReader.getMessage("all.migrated.items"));
        }
    }
}
