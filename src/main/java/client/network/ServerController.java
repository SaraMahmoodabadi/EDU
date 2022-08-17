package client.network;

import client.gui.AlertMonitor;
import client.gui.EDU;
import client.network.offlineClient.OfflineClientHandler;
import com.google.gson.Gson;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import shared.model.message.chatMessages.Message;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.Jackson;
import shared.util.config.Config;
import shared.util.config.ConfigType;
import shared.util.media.MediaHandler;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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
    private Socket socket;

    public ServerController(int port) {
        this.port = port;
        this.objectMapper = Jackson.getNetworkObjectMapper();
        this.offlineClientHandler = new OfflineClientHandler();
    }

    //TODO : set socket address
    public void connectToServer() {
        try {
            this.socket = new Socket(InetAddress.getLocalHost(), port);
            this.printStream = new PrintStream(socket.getOutputStream());
            this.scanner = new Scanner(socket.getInputStream());
            EDU.isOnline = true;
            getToken();
            edu = new EDU(this);
        } catch (IOException e) {
            EDU.isOnline = false;
            edu = new EDU(this);
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
            return getResponse(request);
        }
        else return offlineClientHandler.handleRequest(request);
    }

   public Response getResponse(Request request) {
        if (EDU.isOnline) {
            Response response;
            try {
                response = this.objectMapper.readValue(this.scanner.nextLine(), Response.class);
                checkDisconnection(response);
            } catch (NoSuchElementException | IOException e) {
                EDU.isOnline = false;
                return offlineClientHandler.handleRequest(request);
            }
            return response;
        }
        else return offlineClientHandler.handleRequest(request);
   }

   private void checkDisconnection(Response response) {
       String message = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty
               (String.class, "disconnectionMessage");
       if (response.getStatus() == ResponseStatus.ERROR &&
               response.getErrorMessage().equals(message)) {
           EDU.isOnline = false;
           AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
       }
   }

   private void getToken() {
        Response response = getResponse(new Request());
        this.token = (String) response.getData("token");
   }

    public void closeSocket(Stage stage) {
        stage.setOnCloseRequest(event -> {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.close();
        });
    }

   public static void sendAdminMessages() {
       String path = Config.getConfig(ConfigType.GUI_TEXT).getProperty
               (String.class, "adminMessagesPath") + "/user" + EDU.username + ".json";
       try {
           FileReader reader = new FileReader(path);
           Object obj = new JSONParser().parse(reader);
           JSONObject jo = (JSONObject) obj;
           JSONArray jsonArray = (JSONArray) jo.get(EDU.username + "Messages");
           if (jsonArray != null) {
               Gson gson = new Gson();
               for (Object o : jsonArray) {
                   JSONObject object = gson.fromJson(o.toString(), JSONObject.class);
                   boolean isMedia = (boolean) object.get("media");
                   boolean isTransmitter = (boolean) object.get("transmitter");
                   Message message = gson.fromJson(o.toString(), Message.class);
                   message.setMedia(isMedia);
                   message.setTransmitter(isTransmitter);
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
           reader.close();
       } catch (Exception ignored) {}
       try {
           Files.deleteIfExists(Paths.get(path));
       } catch (IOException e) {
           e.printStackTrace();
       }
   }

}
