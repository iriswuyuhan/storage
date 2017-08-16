package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyReader {
    private final String path="config.properties";
    Properties properties;

    public PropertyReader(){
        properties=new Properties();
        InputStream is=getClass().getResourceAsStream(path);
        try{
            properties.load(is);
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    public String getProperty(String key){
        return properties.getProperty(key);
    }
}
