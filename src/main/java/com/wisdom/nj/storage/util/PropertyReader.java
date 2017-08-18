package com.wisdom.nj.storage.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyReader {
    private static final Logger logger = LoggerFactory.getLogger(PropertyReader.class);

    private static PropertyReader propertyReader;

    private String path;

    private Properties properties;

    //根据路径，初始化Properties
    private PropertyReader(){
        String url=PropertyReader.class.getResource("").getPath().replaceAll("%20"," ");
        path = url.substring(0, url.indexOf("storage")) + "storage/src/main/resources/storage.properties";
        properties=new Properties();

        try{
            FileInputStream is=new FileInputStream(path);
            properties.load(is);
        }catch (IOException ioe){
            logger.error(ioe.getMessage(), ioe);
        }
    }

    //获取PropertyReader
    public static PropertyReader getInstance(){
        if(propertyReader==null){
            propertyReader=new PropertyReader();
        }
        return propertyReader;
    }

    public String getProperty(String key){
        return properties.getProperty(key);
    }
}
