package com.br.migrationTool.propertie;

import com.br.migrationTool.exception.PropertyNotFoundException;

public class PropertiesLoaderImpl {

    private static final PropertiesLoader loader = new PropertiesLoader();

    public static String getValue(String key) {

        String propertyValue = loader.getValue(key);
        if (propertyValue == null) {
            throw new PropertyNotFoundException(key + " not found.");
        } else {
            return loader.getValue(key);
        }
    }

}
