package client.gui.message.messenger.chat;

import client.gui.AlertMonitor;
import client.gui.EDU;
import client.network.ServerController;
import client.network.offlineClient.OfflineClientHandler;
import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;
import javafx.application.Platform;
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
import shared.model.user.UserType;
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
    public Label offlineLabel;
    @FXML
    public Button offlineButton;
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
    boolean stop;
    double size;

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
            else {
                textMessage.clear();
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
            int n = path.split("\\.").length;
            String fileFormat = path.split("\\.")[n-1];
            request.addData("fileFormat", fileFormat);
            request.addData("username", user);
            Response response = EDU.serverController.sendRequest(request);
            if (response.getStatus() == ResponseStatus.ERROR) {
                AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
            }
        }
    }

    @FXML
    public void showNewChatPage(ActionEvent event) {
        stop = true;
        EDU.sceneSwitcher.switchScene(event, "newChat");
    }

    @FXML
    public void back(ActionEvent actionEvent) {
        stop = true;
        EDU.sceneSwitcher.switchScene(actionEvent, "mainPage");
    }

    @FXML
    public void connectToServer(ActionEvent actionEvent) {
        OfflineClientHandler.connectToServer();
    }

    private void showOfflineMood() {
        this.offlineLabel.setVisible(true);
        this.offlineButton.setVisible(true);
        this.offlineButton.setDisable(false);
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
        if (image != null) {
            this.profilePicture.setImage(new ImageHandler().getImage(String.valueOf(image)));
        }
        String name = (String) data.get("name");
        nameLabel.setText(name);
        int t = 0;
        for (int i = 0; i < data.size(); i++) {
            Message newMessage = (Message) data.get("message" + i);
            if (newMessage == null) continue;
            boolean isSender = newMessage.isTransmitter();
            Pane pane;
            if (newMessage.isMedia()) {
                pane = makeMediaMessage(newMessage.getMessageText(), newMessage.getSendMessageTime(), isSender);
            }
            else {
                pane = makeTextMessageInPage(newMessage.getMessageText(), newMessage.getSendMessageTime(), isSender);
            }
            t += size + 15;
            if (t > chatPane.getHeight()) chatPane.setPrefHeight(t);
            chatPane.getChildren().add(pane);
        }
    }

    private Pane makeTextMessageInPage(String message, String messageTime, boolean isSender) {
        Pane pane = new Pane();
        Label label = new Label();
        label.setStyle("-fx-background-radius: 10");
        label.setAlignment(Pos.TOP_LEFT);
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
        FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();
        double width = fontLoader.computeStringWidth(label.getText(), label.getFont());
        if (width < 180) width = 180;
        label.setPrefWidth(width);
        if (isSender) label.setLayoutX(540 - width);
        else label.setLayoutX(0);
        pane.getChildren().add(label);
        Label time = new Label();
        time.setAlignment(Pos.CENTER);
        String date = (messageTime.substring(0, messageTime.length() - 7)).replace("T", " ");
        time.setText(date);
        Font newFont = Font.font("System", FontWeight.BOLD,10);
        time.setFont(newFont);
        time.setWrapText(true);
        time.setMaxWidth(100);
        if (isSender) time.setLayoutX(430);
        else time.setLayoutX(width - 100);
        double height = label.getHeight() + 30;
        label.setPrefHeight(height + 15);
        pane.setPrefHeight(height + 15);
        time.setLayoutY(height);
        pane.getChildren().add(time);
        this.size = label.getPrefHeight();
        return pane;
    }

    private Pane makeMediaMessage(String message, String messageTime, boolean isSender) {
        Pane pane = new Pane();
        pane.setPrefHeight(70);
        Rectangle rectangle = new Rectangle();
        rectangle.setStyle("-fx-background-radius: 10");
        rectangle.setHeight(70);
        rectangle.setWidth(350);
        rectangle.setFill(Color.valueOf("#b151b8"));
        rectangle.setStroke(Color.valueOf("#b151b8"));
        if (isSender) rectangle.setLayoutX(200);
        else rectangle.setLayoutX(0);
        rectangle.setLayoutY(0);
        pane.getChildren().add(rectangle);
        Button button = new Button();
        String fileImage = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "fileIconPath");
        ImageView file = new ImageView("file:" + fileImage);
        file.setFitHeight(50);
        file.setFitWidth(50);
        button.setGraphic(file);
        button.setStyle("-fx-background-color: #b151b8");
        button.setOnAction(event -> open(message));
        button.setLayoutY(0);
        if (isSender) button.setLayoutX(210);
        else button.setLayoutX(270);
        Label time = new Label();
        time.setAlignment(Pos.CENTER);
        String date = (messageTime.substring(0, messageTime.length() - 7)).replace("T", " ");
        time.setText(date);
        Font newFont = Font.font("System", FontWeight.BOLD,10);
        time.setFont(newFont);
        time.setWrapText(true);
        time.setMaxWidth(100);
        if (isSender) time.setLayoutX(430);
        else time.setLayoutX(rectangle.getWidth() - 115);
        time.setLayoutY(rectangle.getHeight() - 15);
        pane.getChildren().add(button);
        pane.getChildren().add(time);
        this.size = rectangle.getHeight();
        return pane;
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

    private void updateAllChats() {
        Thread loop = new Thread(() -> {
            while (!stop) {
                try {
                    if (!EDU.isOnline) break;
                    Thread.sleep(2000);
                    Platform.runLater(() -> {
                        if (!EDU.isOnline)
                            showOfflineMood();
                        else {
                            Request request = new Request(RequestType.SHOW_ALL_CHATS);
                            Response response = EDU.serverController.sendRequest(request);
                            if (response.getStatus() == ResponseStatus.OK) {
                                allChatsPane.getChildren().clear();
                                showAllChats(response.getData());
                            }
                        }
                    });
                } catch (InterruptedException ignored) {}
            }
        });
        loop.start();
    }

    private void updateChat() {
        Thread loop = new Thread(() -> {
            while (!stop) {
                try {
                    if (!EDU.isOnline) break;
                    Thread.sleep(2000);
                    Platform.runLater(() -> {
                        if (!EDU.isOnline)
                            showOfflineMood();
                        else {
                            Request request = new Request(RequestType.SHOW_CHAT);
                            request.addData("user", user);
                            Response response = EDU.serverController.sendRequest(request);
                            if (response.getStatus() == ResponseStatus.OK) {
                                chatPane.getChildren().clear();
                                showChat(response.getData());
                            }
                        }
                    });
                } catch (InterruptedException ignored) {}
            }
        });
        loop.start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        stop = false;
        if (!EDU.isOnline) showOfflineMood();
        Request request = new Request(RequestType.SHOW_ALL_CHATS);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) showAllChats(response.getData());
        else
            AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        updateAllChats();
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
                ChatController.this.user = user;
                chatPane.getChildren().clear();
                Request request = new Request(RequestType.SHOW_CHAT);
                request.addData("user", user);
                request.addData("name", name);
                Response response = EDU.serverController.sendRequest(request);
                if (response.getStatus() == ResponseStatus.OK) {
                    showChat(response.getData());
                    updateChat();
                }
                else AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
            });
        }
    }
}
