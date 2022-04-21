package com.br.migrationTool.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OwnerUtils {

    @Value("${megastore.prod.datasource}")
    String ownerProd;

    @Value("${megastore.hml.datasource}")
    String ownerHomolog;

    public String getOwner(boolean isProd) {
        if (isProd) {
            return ownerProd;
        }

        return ownerHomolog;

    }
}
