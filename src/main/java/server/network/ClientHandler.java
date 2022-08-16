package server.network;

import org.codehaus.jackson.map.ObjectMapper;
import server.Server;
import server.database.MySQLHandler;
import server.logic.captcha.CaptchaHandler;
import shared.model.user.UserType;
import shared.request.Request;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.Jackson;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ClientHandler {
    private final MySQLHandler dataHandler;
    private PrintStream printStream;
    private Scanner scanner;
    private ObjectMapper objectMapper;
    private final Server server;
    private final Socket socket;
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
        this.socket = socket;
        try {
            this.printStream = new PrintStream(socket.getOutputStream());
            this.scanner = new Scanner(socket.getInputStream());
            this.objectMapper = Jackson.getNetworkObjectMapper();
            sendToken();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dataHandler = new MySQLHandler(this);
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
    public void makeListenerThread() {
        while (true) {
            String requestString;
            try {
                requestString = this.scanner.nextLine();
            } catch (NoSuchElementException e) {
                break;
            }
            try {
                Request request = this.objectMapper.readValue(requestString, Request.class);
                if (request.getData("token").equals(this.token))
                    handleRequest(request);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.server.disconnect(this);
        try {
            this.socket.close();
        } catch (IOException ignored) {}
    }

    private void sendToken() {
        Response response = new Response(ResponseStatus.OK);
        response.addData("token", this.token);
        sendResponse(response);
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

    public void disconnect() {
        String message = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty
                (String.class, "disconnectionMessage");
        Response response = new Response(ResponseStatus.ERROR);
        response.setErrorMessage(message);
        sendResponse(response);
        this.server.disconnect(this);
        try {
            this.socket.close();
        } catch (IOException ignored) {}
        this.server.looseDatabaseConnection();
    }

}
