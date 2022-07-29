package server;

import shared.util.config.Config;
import shared.util.config.ConfigType;

public class ServerMain {
    public static void main(String[] args) {
        int port = Config.getConfig(ConfigType.NETWORK).getProperty(Integer.class, "serverPort");
        Server server = new Server(port);
        server.start();
    }
}
