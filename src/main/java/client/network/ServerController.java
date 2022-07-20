package client.network;

import org.codehaus.jackson.map.ObjectMapper;
import shared.request.Request;
import shared.util.Jackson;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class ServerController {
    private PrintStream printStream;
    private Scanner scanner;
    private final int port;
    private final ObjectMapper objectMapper;

    public ServerController(int port) {
        this.port = port;
        this.objectMapper = Jackson.getNetworkObjectMapper();
    }

    //TODO : set socket address
    public void connectToServer() {
        try {
            Socket socket = new Socket(InetAddress.getLocalHost(), port);
            this.printStream = new PrintStream(socket.getOutputStream());
            this.scanner = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendRequest(Request request) {
        try {
            String requestString = this.objectMapper.writeValueAsString(request);
            this.printStream.println(requestString);
            this.printStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
