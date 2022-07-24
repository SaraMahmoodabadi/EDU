package shared.util.config;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.Properties;

public class Config extends Properties {
    private static String configPath = "src/main/java/shared/util/config/config.properties";
    private static Config config;

    private Config(String address) {
        try {
            Reader fileReader = new FileReader(address);
            this.load(fileReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Config getConfig(ConfigType type) {
        Config config = getMainConfig();
        switch (type) {
            case CLIENT_IMAGE:
                return config.getProperty(Config.class, "clientImage");
            case FXML_FILE:
                return config.getProperty(Config.class, "fxmlFiles");
            case GUI_TEXT:
                return config.getProperty(Config.class, "guiText");
            case QUERY:
                return config.getProperty(Config.class, "query");
            case SERVER_MESSAGES:
                return config.getProperty(Config.class, "serverMessages");
            default:
        }
        return null;
    }

    public <E> E getProperty(Class<E> c, String propertyName) {
        return getObject(c, propertyName);
    }

    public <E> Optional<E> getOptionalProperty(Class<E> c, String propertyName) {
        if (containsKey(propertyName)) {
            return Optional.of(getObject(c, propertyName));
        } else {
            return Optional.empty();
        }
    }

    private <E> E getObject(Class<E> c, String value) {
        E e = null;
        try {
            Constructor<E> constructor = c.getConstructor(String.class);
            e = constructor.newInstance(value);
        } catch (ReflectiveOperationException reflectiveOperationException) {
            reflectiveOperationException.printStackTrace();
        }
        return e;
    }

    private static Config getMainConfig() {
        if (config == null) {
            config = new Config(configPath);
        }
        return config;
    }
}
