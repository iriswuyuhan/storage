package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyReader {
    private final String path="src/main/resources/config.properties";
    private Properties properties;

    public PropertyReader(){
        properties=new Properties();

        try{
            //TODO:insert exceptions
            FileInputStream is=new FileInputStream(path);
            properties.load(is);
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    public String getProperty(String key){
        return properties.getProperty(key);
    }
}
