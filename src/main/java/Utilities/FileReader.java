package Utilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class FileReader {

    public static String readProperty(String property) throws IOException {

        String filePath = System.getProperty("user.dir")+"/src/test/resources/Data.Properties";
        FileInputStream file = new FileInputStream(filePath);
        Properties p = new Properties();
        p.load(file);
        return p.getProperty(property);
    }
}
