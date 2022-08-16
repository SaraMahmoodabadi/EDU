package server;

import server.database.MySQLHandler;
import server.logic.managers.edu.unitSelection.UnitSelectionTimeManager;
import server.network.ClientHandler;
import server.network.Token;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private final int port;
    private final ArrayList<ClientHandler> clients;
    private static int clientCount = 0;
    private ServerSocket serverSocket;
    private boolean running;
    private final Token token;

    public Server(int port){
        this.port = port;
        this.clients = new ArrayList<>();
        this.token = new Token();
    }

    protected void start() {
        try {
            this.serverSocket = new ServerSocket(port);
            this.running = true;
            runImportantMethods();
            listenForNewConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void runImportantMethods() {
        UnitSelectionTimeManager.checkTime(new MySQLHandler());
    }

    private void listenForNewConnection() {
        while (this.running) {
            try {
                clientCount++;
                Socket socket = this.serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientCount, this, socket, this.token);
                new Thread(clientHandler::makeListenerThread).start();
                this.clients.add(clientHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect(ClientHandler clientHandler) {
        this.clients.remove(clientHandler);
    }


    public void stop() {
        try {
            this.serverSocket.close();
            this.running = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
