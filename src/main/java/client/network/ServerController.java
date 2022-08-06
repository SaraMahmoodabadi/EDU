package client.network;

import client.gui.EDU;
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
    private String token;
    public static EDU edu;
    public static Request request;

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
            getToken();
            edu = new EDU(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Response sendRequest(Request request) {
        try {
            request.addData("token", this.token);
            String requestString = this.objectMapper.writeValueAsString(request);
            this.printStream.println(requestString);
            this.printStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getResponse();
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

   private void getToken() {
        Response response = getResponse();
        this.token = (String) response.getData("token");
   }

}
