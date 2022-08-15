package client.network.offlineClient;

import client.Client;
import client.network.ServerController;
import shared.request.Request;
import shared.response.Response;
import shared.util.config.Config;
import shared.util.config.ConfigType;

public class OfflineClientHandler {

    public Response handleRequest(Request request) {
        return null;
    }

    public static void connectToServer() {
        ServerController.edu.close();
        int port = Integer.parseInt(Config.getConfig(ConfigType.NETWORK).getProperty(String.class, "clientPort"));
        Client client = new Client(port);
        client.start();
    }

}
