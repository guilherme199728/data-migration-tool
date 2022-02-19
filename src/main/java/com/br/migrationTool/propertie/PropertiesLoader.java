package com.br.migrationTool.propertie;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

    private final Properties properties;

    protected PropertiesLoader(){
        properties = new Properties();
        InputStream in = this.getClass().getResourceAsStream("resource/configuracao.properties");
        try{
            properties.load(in);
            in.close();
        }
        catch(IOException | NullPointerException e){
            System.out.println("Propertied not found.");
        }
    }

    protected String getValue(String key){
        return properties.getProperty(key);
    }
}
