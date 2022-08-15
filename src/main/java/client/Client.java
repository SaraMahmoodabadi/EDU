package client;

import client.network.ServerController;

public class Client {
    private final int port;
    public static ServerController serverController;

    public Client(int port) {
        this.port = port;
    }

    public void start() {
        serverController = new ServerController(this.port);
        serverController.connectToServer();
    }
}
