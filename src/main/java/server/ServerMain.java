package server;

import shared.util.config.Config;
import shared.util.config.ConfigType;

public class ServerMain {
    public static void main(String[] args) {
        int port = Integer.parseInt(Config.getConfig(ConfigType.NETWORK).getProperty("serverPort"));
        Server server = new Server(port);
        server.start();
    }
}
