package client.network;

import org.codehaus.jackson.map.ObjectMapper;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
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
            sendRequest(new Request(RequestType.START_CONNECTION));
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

   public Response getResponse() {
       Response response = new Response();
       try {
           response = this.objectMapper.readValue(this.scanner.nextLine(), Response.class);
       } catch (IOException e) {
           e.printStackTrace();
       }
       return response;
   }

}
