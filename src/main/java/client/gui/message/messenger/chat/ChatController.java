package client.gui.message.messenger.chat;

import client.gui.AlertMonitor;
import client.gui.EDU;
import client.network.ServerController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import shared.model.message.chatMessages.Message;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;
import shared.util.media.ImageHandler;
import shared.util.media.MediaHandler;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class ChatController implements Initializable {

    @FXML
    protected TextField textMessage;
    @FXML
    protected ImageView profilePicture;
    @FXML
    protected Label nameLabel;
    @FXML
    protected Button mediaButton;
    @FXML
    protected Button back;
    @FXML
    protected VBox allChatsPane;
    @FXML
    protected Button newChatButton;
    @FXML
    protected VBox chatPane;
    protected String user;

    @FXML
    public void sendMessage(KeyEvent keyEvent) {
        if (textMessage.getText() == null) return;
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            Request request = new Request(RequestType.SEND_MESSAGE_CHAT);
            request.addData("message", textMessage.getText());
            request.addData("username", user);
            request.addData("isMedia", false);
            Response response = EDU.serverController.sendRequest(request);
            if (response.getStatus() == ResponseStatus.ERROR) {
                AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
            }
        }
    }

    @FXML
    public void chooseMedia(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("select file");
        File file = fileChooser.showOpenDialog(ServerController.edu);
        if (file != null) {
            MediaHandler handler = new MediaHandler();
            String path = file.getAbsolutePath();
            String message = handler.encode(path);
            Request request = new Request(RequestType.SEND_MESSAGE_CHAT);
            request.addData("message", message);
            request.addData("isMedia", true);
            request.addData("username", user);
            Response response = EDU.serverController.sendRequest(request);
            if (response.getStatus() == ResponseStatus.ERROR) {
                AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
            }
        }
    }

    @FXML
    public void showNewChatPage(ActionEvent event) {
        EDU.sceneSwitcher.switchScene(event, "newChat");
    }

    @FXML
    public void back(ActionEvent actionEvent) {
        EDU.sceneSwitcher.switchScene(actionEvent, "mainPage");
    }

    private void showAllChats(Map<String, Object> data) {
        int t = 0;
        for (int i = 0; i < data.size(); i++) {
            Message newChat = (Message) data.get("message" + i);
            if (newChat == null) continue;
            t++;
            if (allChatsPane.getHeight() < 90 * t)
                allChatsPane.setPrefHeight(90 * t);
            String name = newChat.getName();
            String message = newChat.getMessageText();
            String user = newChat.getUser();
            ChatLabel label = new ChatLabel(message, name, user);
            allChatsPane.getChildren().addAll(label);
        }
    }

    private void showChat(Map<String, Object> data) {
        Object image = data.get("profileImage");
        this.profilePicture.setImage(new ImageHandler().getImage(String.valueOf(image)));
        String name = (String) data.get("name");
        nameLabel.setText(name);
        int t = 0;
        for (int i = 0; i < data.size(); i++) {
            Message newMessage = (Message) data.get("message" + i);
            if (newMessage == null) continue;
            boolean isSender = newMessage.isTransmitter();
            if (newMessage.isMedia())
                t += makeMediaMessage(newMessage.getMessageText(), newMessage.getSendMessageTime(), isSender);
            else t += makeTextMessageInPage(newMessage.getMessageText(), newMessage.getSendMessageTime(), isSender);
            if (t > chatPane.getHeight()) chatPane.setPrefHeight(t);
        }
    }

    private double makeTextMessageInPage(String message, String messageTime, boolean isSender) {
        Pane pane = new Pane();
        Label label = new Label();
        label.setAlignment(Pos.CENTER);
        label.setText(message);
        Font font = Font.font("System", FontWeight.BOLD,15);
        label.setFont(font);
        label.setTextFill(Color.valueOf("#b151b8"));
        label.setStyle(String.valueOf(Color.valueOf("#ffd100")));
        label.setBackground(new Background(new BackgroundFill
                (Color.valueOf("#ffd100"), CornerRadii.EMPTY, Insets.EMPTY)));
        label.setWrapText(true);
        label.setMaxWidth(450);
        label.setLayoutY(0);
        if (isSender) label.setLayoutX(540 - label.getWidth());
        else label.setLayoutX(0);
        chatPane.getChildren().add(pane);
        Label time = new Label();
        time.setAlignment(Pos.CENTER);
        time.setText(messageTime);
        Font newFont = Font.font("System", FontWeight.BOLD,10);
        time.setFont(newFont);
        time.setWrapText(true);
        time.setMaxWidth(20);
        if (isSender) time.setLayoutX(430);
        else time.setLayoutX(label.getWidth() - 115);
        time.setLayoutY(label.getHeight() - 15);
        pane.setPrefHeight(label.getHeight());
        chatPane.getChildren().add(pane);
        return pane.getHeight();
    }

    private double makeMediaMessage(String message, String messageTime, boolean isSender) {
        Pane pane = new Pane();
        pane.setPrefHeight(70);
        Rectangle rectangle = new Rectangle();
        rectangle.setHeight(70);
        rectangle.setWidth(350);
        rectangle.setFill(Color.valueOf("#b151b8"));
        rectangle.setStroke(Color.valueOf("#b151b8"));
        rectangle.setLayoutX(0);
        rectangle.setLayoutY(0);
        pane.getChildren().add(rectangle);
        Button button = new Button();
        String fileImage = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "fileIconPath");
        ImageView file = new ImageView(fileImage);
        file.setFitHeight(50);
        file.setFitWidth(50);
        button.setGraphic(file);
        button.setOnAction(event -> open(message));
        button.setLayoutX(10);
        if (isSender) button.setLayoutY(10);
        else button.setLayoutY(290);
        Label time = new Label();
        time.setAlignment(Pos.CENTER);
        time.setText(messageTime);
        Font newFont = Font.font("System", FontWeight.BOLD,10);
        time.setFont(newFont);
        time.setWrapText(true);
        time.setMaxWidth(20);
        if (isSender) time.setLayoutX(430);
        else time.setLayoutX(rectangle.getWidth() - 115);
        time.setLayoutY(rectangle.getHeight() - 15);
        pane.getChildren().add(button);
        chatPane.getChildren().add(pane);
        return pane.getHeight();
    }

    private void open(String message) {
        MediaHandler handler = new MediaHandler();
        byte[] file = handler.decode(message);
        String path = "src/main/java/client/resource/sentFiles/" + handler.getName();
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(file);
            Desktop d = Desktop.getDesktop();
            d.open(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Request request = new Request(RequestType.SHOW_ALL_CHATS);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) showAllChats(response.getData());
        else
            AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
    }

    class ChatLabel extends Label{
        String message;
        String name;
        String user;

        public ChatLabel(String message, String name, String user) {
            this.message = message;
            this.name = name;
            this.user = user;
            makeLabel(name + "\n" + message);
        }

        private void makeLabel(String data) {
            this.setText(data);
            this.setPrefWidth(533);
            this.setPrefHeight(75);
            Font font = Font.font("System", FontWeight.BOLD,15);
            this.setFont(font);
            this.setTextFill(Color.valueOf("#b151b8"));
            this.setStyle(String.valueOf(Color.valueOf("#ffd100")));
            this.setBackground(new Background(new BackgroundFill
                    (Color.valueOf("#ffd100"), CornerRadii.EMPTY, Insets.EMPTY)));
            addActionEvent(this);
        }

        private void addActionEvent(Label label) {
            label.setOnMouseClicked(event -> {
                Request request = new Request(RequestType.SHOW_CHAT);
                request.addData("user", user);
                Response response = EDU.serverController.sendRequest(request);
                if (response.getStatus() == ResponseStatus.OK) {
                    showChat(response.getData());
                }
                else AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
            });
        }
    }
}
