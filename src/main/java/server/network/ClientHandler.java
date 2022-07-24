package server.network;

import org.codehaus.jackson.map.ObjectMapper;
import server.Server;
import server.database.MySQLHandler;
import server.logic.captcha.CaptchaHandler;
import shared.model.user.UserType;
import shared.request.Request;
import shared.response.Response;
import shared.util.Jackson;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler {
    private MySQLHandler dataHandler;
    private PrintStream printStream;
    private Scanner scanner;
    private ObjectMapper objectMapper;
    private final Server server;
    private final int clientID;
    private final String token;
    private final CaptchaHandler captchaHandler;
    private UserType userType;
    private String userName;

    public ClientHandler(int clientID, Server server, Socket socket, Token token) {
        this.clientID = clientID;
        this.server = server;
        this.token = token.generateToken();
        this.captchaHandler = new CaptchaHandler();
        try {
            this.printStream = new PrintStream(socket.getOutputStream());
            this.scanner = new Scanner(socket.getInputStream());
            this.objectMapper = Jackson.getNetworkObjectMapper();
            makeListenerThread();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dataHandler = new MySQLHandler();
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UserType getUserType() {
        return userType;
    }

    public String getUserName() {
        return userName;
    }

    public MySQLHandler getDataHandler() {
        return dataHandler;
    }

    public CaptchaHandler getCaptchaHandler() {
        return captchaHandler;
    }

    //TODO : handle end of connection
    private void makeListenerThread() {
        Thread thread = new Thread(() -> {
            while (true) {
                String requestString = this.scanner.nextLine();
                try {
                    Request request = this.objectMapper.readValue(requestString, Request.class);
                    handleRequest(request);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void handleRequest(Request request) {
        new RequestHandler().handleRequests(this, request);
    }

    public void sendResponse(Response response) {
        try {
            String responseString = this.objectMapper.writeValueAsString(response);
            this.printStream.println(responseString);
            this.printStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
