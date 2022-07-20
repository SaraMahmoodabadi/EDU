package server;

public class ServerMain {
    public static void main(String[] args) {
        int port = 0;
        Server server = new Server(port);
        server.start();
    }
}
