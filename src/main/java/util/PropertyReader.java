package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyReader {
    private String path="src/main/resources/config.properties";
    private Properties properties;

    public PropertyReader(){
        String url=PropertyReader.class.getResource("").getPath().replaceAll("%20"," ");
        System.out.println(PropertyReader.class.getResource("").getPath());
        path=url.substring(0,url.indexOf("storage"))+"storage/src/main/resources/config.properties";
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
