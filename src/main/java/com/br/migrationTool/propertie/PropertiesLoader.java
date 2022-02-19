package com.br.migrationTool.propertie;

import com.br.migrationTool.exception.PropertyNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

    private final Properties properties;

    protected PropertiesLoader(){
        properties = new Properties();
        String propertyFileName = "/config/config.properties";
        InputStream in = this.getClass().getResourceAsStream(propertyFileName);
        try{
            properties.load(in);
            in.close();
        }
        catch(IOException e){
            throw new PropertyNotFoundException("error in load property", e);
        }
        catch (NullPointerException e) {
            throw new PropertyNotFoundException("Property " + propertyFileName + " not found.");
        }
    }

    protected String getValue(String key){
        return properties.getProperty(key);
    }
}
