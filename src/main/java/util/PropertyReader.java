package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyReader {
    private static PropertyReader propertyReader;

    private String path;

    private Properties properties;

    private PropertyReader(){
        String url=PropertyReader.class.getResource("").getPath().replaceAll("%20"," ");
        path=url.substring(0,url.indexOf("storage"))+"storage/src/main/resources/config.properties";
        properties=new Properties();

        try{
            FileInputStream is=new FileInputStream(path);
            properties.load(is);
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

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
