package client.network;

import client.gui.EDU;
import client.network.offlineClient.OfflineClientHandler;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import shared.model.message.chatMessages.Message;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.util.Jackson;
import shared.util.config.Config;
import shared.util.config.ConfigType;
import shared.util.media.MediaHandler;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ServerController {
    private PrintStream printStream;
    private Scanner scanner;
    private final int port;
    private final ObjectMapper objectMapper;
    private String token;
    public static EDU edu;
    public static Request request;
    public OfflineClientHandler offlineClientHandler;

    public ServerController(int port) {
        this.port = port;
        this.objectMapper = Jackson.getNetworkObjectMapper();
        this.offlineClientHandler = new OfflineClientHandler();
    }

    //TODO : set socket address
    public void connectToServer() {
        try {
            Socket socket = new Socket(InetAddress.getLocalHost(), port);
            this.printStream = new PrintStream(socket.getOutputStream());
            this.scanner = new Scanner(socket.getInputStream());
            getToken();
            OfflineClientHandler.requestGetData();
            edu = new EDU(this);
        } catch (IOException e) {
            edu = new EDU(this);
            EDU.isOnline = false;
        }
    }

    public synchronized Response sendRequest(Request request) {
        if (EDU.isOnline) {
            try {
                request.addData("token", this.token);
                String requestString = this.objectMapper.writeValueAsString(request);
                this.printStream.println(requestString);
                this.printStream.flush();
            } catch (IOException e) {
                EDU.isOnline = false;
            }
            return getResponse();
        }
        else return offlineClientHandler.handleRequest(request);
    }

   public Response getResponse() {
        if (EDU.isOnline) {
            Response response = new Response();
            try {
                response = this.objectMapper.readValue(this.scanner.nextLine(), Response.class);
            } catch (IOException e) {
                EDU.isOnline = false;
            }
            return response;
        }
        else return offlineClientHandler.handleRequest(request);
   }

   private void getToken() {
        Response response = getResponse();
        this.token = (String) response.getData("token");
   }

   public static void sendAdminMessages() {
       String path = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "adminMessagesPath");
       try {
           Object obj = new JSONParser().parse(new FileReader(path));
           JSONObject jo = (JSONObject) obj;
           JSONArray jsonArray = (JSONArray) jo.get(EDU.username + "Messages");
           if (jsonArray != null) {
               for (Object o : jsonArray) {
                   Message message = (Message) o;
                   Request request = new Request(RequestType.SEND_MESSAGE_TO_ADMIN);
                   if (message.getFile() != null) {
                       String file = new MediaHandler().encode(message.getFile());
                       request.addData("file", file);
                       request.addData("fileFormat", message.getFileFormat());
                   }
                   if (message.getMessageText() != null) {
                       request.addData("message", message.getMessageText());
                   }
                   EDU.serverController.sendRequest(request);
               }
           }
       } catch (Exception ignored) {}
   }

}
