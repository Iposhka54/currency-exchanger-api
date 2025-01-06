package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
    private static final Properties PROPERTIES = new Properties();
    private static final String PROPERTIES_FILE = "db.properties";
    static {
        loadProperties();
    }

    public static String getProperty(String key) {
        return PROPERTIES.getProperty(key);
    }

    private static void loadProperties() {
        ClassLoader loader = PropertiesUtil.class.getClassLoader();
        try(InputStream is = loader.getResourceAsStream(PROPERTIES_FILE)){
            PROPERTIES.load(is);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
