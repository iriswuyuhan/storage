package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyReader {
    //TODO:make path reusable
    private final String path="src/main/resources/config.properties";
    private Properties properties;

    public PropertyReader(){
        properties=new Properties();

        try{
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
