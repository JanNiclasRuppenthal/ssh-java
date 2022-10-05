import java.io.IOException;
import java.util.Properties;

public class Main
{
    private String username;
    private String password;

    public static void main(String[] args)
    {
        Main main = new Main();

        main.extractProperties();
    }


    private void extractProperties()
    {
        PropertiesFileLoader propertiesLoader = PropertiesFileLoader.createPropertiesFileLoader();

        Properties properties;
        try
        {
            properties = propertiesLoader.loadProperties("ssh.properties");
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        this.username = properties.getProperty("username");
        this.password = properties.getProperty("password");
    }

}
