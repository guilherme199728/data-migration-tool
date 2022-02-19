package com.br.migrationTool.propertie;

public class PropertiesLoaderImpl {

    private static PropertiesLoader loader = new PropertiesLoader();

    public static String getValue(String key){
        return loader.getValue(key);
    }

}
