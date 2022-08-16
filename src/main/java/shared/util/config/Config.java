package shared.util.config;

import java.io.*;
import java.util.Optional;
import java.util.Properties;

public class Config extends Properties {
    private static String configPath = "src/main/java/shared/util/config/config.properties";
    private static Config config;
    private Properties properties;

    public Config(String address) {
        try {
            properties = new Properties();
            FileInputStream ip = new FileInputStream(address);
            properties.load(ip);
           /** Reader fileReader = new FileReader(address);
            this.load(fileReader);*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Properties getProperties() {
        return properties;
    }

    public static Config getConfig(ConfigType type) {
        Config config = getMainConfig();
        switch (type) {
            case CLIENT_IMAGE:
                return new Config(config.getProperty(String.class, "clientImage"));
            case FXML_FILE:
                return new Config(config.getProperty(String.class, "fxmlFiles"));
            case GUI_TEXT:
                return new Config(config.getProperty(String.class, "guiText"));
            case QUERY:
                return new Config(config.getProperty(String.class, "query"));
            case SERVER_MESSAGES:
                return new Config(config.getProperty(String.class, "serverMessages"));
            case SERVER_PATH:
                return new Config(config.getProperty(String.class, "serverPath"));
            case NETWORK:
                return new Config(config.getProperty(String.class, "network"));
            case CLIENT_DATA:
                return new Config(config.getProperty(String.class, "clientData"));
            default:
                return getMainConfig();
        }
    }

    public <E> E getProperty(Class<E> c, String propertyName) {
        return (E) properties.getProperty(propertyName);
        //return getObject(c, propertyName);
    }

    public <E> Optional<E> getOptionalProperty(Class<E> c, String propertyName) {
        if (containsKey(propertyName)) {
            return Optional.of(getObject(c, propertyName));
        } else {
            return Optional.empty();
        }
    }

    private <E> E getObject(Class<E> c, String value) {
        /**E e = null;
        try {
            Constructor<E> constructor = c.getConstructor(String.class);
            e = constructor.newInstance(value);
        } catch (ReflectiveOperationException reflectiveOperationException) {
            reflectiveOperationException.printStackTrace();
        }
        return e;*/
        return null;
    }

    private static Config getMainConfig() {
        if (config == null) {
            config = new Config(configPath);
        }
        return config;
    }

    public void write(String key, String value) {
       properties.put(key, value);
    }
}
