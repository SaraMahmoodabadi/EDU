package client.gui.message.messenger.chat;

import client.gui.AlertMonitor;
import client.gui.EDU;
import client.gui.message.messages.MessagesController;
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
import javafx.scene.image.Image;
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
import shared.model.user.UserType;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.io.File;
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
    protected AnchorPane allChatsPane;
    @FXML
    protected Button newChatButton;
    @FXML
    protected AnchorPane chatPane;
    protected String user;

    @FXML
    public void sendMessage(KeyEvent keyEvent) {
        if (textMessage.getText() == null) return;
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            Request request = new Request(RequestType.SEND_MESSAGE_CHAT);
            request.addData("message", textMessage.getText());
            request.addData("username", user);
            request.addData("isMedia", "false");
            Response response = EDU.serverController.sendRequest(request);
            if (response.getStatus() == ResponseStatus.ERROR) {
                AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
            }
        }
    }

    @FXML
    public void chooseMedia(ActionEvent event) {
        FileChooser imageChooser = new FileChooser();
        imageChooser.setTitle("select file");
        File file = imageChooser.showOpenDialog(ServerController.edu);
        if(file != null) {
            String path = file.getAbsolutePath();
            Request request = new Request(RequestType.SEND_MESSAGE_CHAT);
            String message = ""; //TODO
            request.addData("message", message);
            request.addData("isMedia", "true");
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
            if (t > 7) allChatsPane.setPrefHeight(allChatsPane.getHeight() + 90);
            String name = newChat.getName();
            String message = newChat.getMessageText();
            String user = newChat.getUser();
            ChatLabel label = new ChatLabel(message, name, user);
            allChatsPane.getChildren().addAll(label);
        }
    }

    private void showChat(Map<String, Object> data) {
        int t = 0;
        for (int i = 0; i < data.size(); i++) {
            Message newMessage = (Message) data.get("message" + i);
            if (newMessage == null) continue;
            t++;
            //TODO
        }
    }

    private void makeTextMessageInPage(String message, String time) {
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
        chatPane.getChildren().add(label);
    } //TODO : ADD TIME, ALSO IN MESSAGES

    private void makeMediaMessage(String message, String time) {
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
        String fileImage = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "fileIconPath");
        ImageView file = new ImageView(fileImage);
        file.setFitHeight(50);
        file.setFitWidth(50);
        file.setLayoutX(10);
        file.setLayoutY(10);
        pane.getChildren().add(file);
        Button button = new Button();
        String downloadImage = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "downloadIconPath");
        ImageView download = new ImageView(downloadImage);
        download.setFitHeight(50);
        download.setFitWidth(50);
        button.setGraphic(download);
        button.setOnAction(event -> download(message));
        button.setLayoutX(10);
        button.setLayoutY(290);
        pane.getChildren().add(button);
        chatPane.getChildren().add(pane);
    } //TODO : ADD TIME, ALSO IN MESSAGES

    private void download(String message) {
        //TODO
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
            Label label = new Label(data);
            label.setPrefWidth(533);
            label.setPrefHeight(75);
            Font font = Font.font("System", FontWeight.BOLD,15);
            label.setFont(font);
            addActionEvent(label);
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
