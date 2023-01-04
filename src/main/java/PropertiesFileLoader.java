import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesFileLoader
{

    private static PropertiesFileLoader propertiesFileLoader = null;

    private PropertiesFileLoader()
    {

    }


    public static PropertiesFileLoader createPropertiesFileLoader()
    {
        if (propertiesFileLoader == null)
        {
            return new PropertiesFileLoader();
        }

        return propertiesFileLoader;
    }

    public Properties loadProperties(String filename) throws IOException
    {
        Properties properties = new Properties();
        properties.load(new FileInputStream(filename));

        return properties;
    }

}
