package client;

import client.network.ServerController;

public class Client {
    private final int port;
    private ServerController serverController;

    public Client(int port) {
        this.port = port;
    }

    //TODO : START GUI
    protected void start() {
        this.serverController = new ServerController(this.port);
        serverController.connectToServer();
    }
}
