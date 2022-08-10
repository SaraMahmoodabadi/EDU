package client.gui.message.messages;

import client.gui.AlertMonitor;
import client.gui.EDU;
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
import shared.model.message.chatMessages.Message;
import shared.model.user.UserType;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;
import shared.util.media.MediaHandler;


import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class MessagesController implements Initializable {
    @FXML
    protected TextField messageField;
    @FXML
    protected Button accept;
    @FXML
    protected Button reject;
    @FXML
    protected VBox messagePane;
    @FXML
    protected Pane informationPane;
    @FXML
    protected Label nameLabel;
    @FXML
    protected Button back;
    @FXML
    protected VBox AllMessagesPane;
    protected MessageLabel user;
    private boolean stop;

    @FXML
    public void accept(ActionEvent event) {
        sendRequestAnswer(true);
    }

    @FXML
    public void back(ActionEvent event) {
        stop = true;
        if (EDU.userType == UserType.EDU_ADMIN)
            EDU.sceneSwitcher.switchScene(event, "loginPage");
        else EDU.sceneSwitcher.switchScene(event, "mainPage");
    }

    @FXML
    public void reject(ActionEvent event) {
        sendRequestAnswer(false);
    }

    @FXML
    public void sendMessage(KeyEvent keyEvent) {
        if (messageField.getText() == null) return;
        if (user == null) return;
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            Request request = new Request(RequestType.SEND_MESSAGE_ADMIN);
            request.addData("answer", messageField.getText());
            request.addData("username", user.user);
            request.addData("userMessage", user.message);
            request.addData("messageTime", user.time);
            Response response = EDU.serverController.sendRequest(request);
            if (response.getStatus() == ResponseStatus.OK) {
                makeTextMessageInPage(messageField.getText(), true);
                messageField.setText(null);
            }
            else AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        }
    }

    private void sendRequestAnswer(boolean result) {
        Request request = new Request(RequestType.SEND_REQUEST_ANSWER);
        request.addData("result", result);
        request.addData("username", user.user);
        request.addData("date", user.time);
        request.addData("message", user.message);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK)
            AlertMonitor.showAlert(Alert.AlertType.INFORMATION, response.getNotificationMessage());
        else AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
    }

    private void makeTextMessageInPage(String message, boolean isSender) {
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
        FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();
        double width = fontLoader.computeStringWidth(label.getText(), label.getFont());
        if (isSender) label.setLayoutX(540 - width);
        else label.setLayoutX(0);
        Pane pane = new Pane();
        pane.getChildren().add(label);
        messagePane.getChildren().add(pane);
    }

    private void makeMediaMessage(String message) {
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
        button.setLayoutY(290);
        pane.getChildren().add(button);
        messagePane.getChildren().add(pane);
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

    private void showData(Map<String, Object> data) {
        AllMessagesPane.getChildren().clear();
        int t = 0;
        for (int i = 0; i < data.size(); i++) {
            Message newMessage = (Message) data.get("message" + i);
            if (newMessage == null) continue;
            t++;
            if (AllMessagesPane.getHeight() < 90 * t)
                AllMessagesPane.setPrefHeight(90 * t);
            String name = newMessage.getName();
            String message = newMessage.getMessageText();
            String user = newMessage.getUser();
            String type = newMessage.getType();
            String time = newMessage.getSendMessageTime();
            MessageLabel label = new MessageLabel(message, name, user, type, time);
            AllMessagesPane.getChildren().add(label);
        }
    }

    private void showMessages(Map<String, Object> data) {
        String name = (String) data.get("name");
        nameLabel.setText(name);
        for (int i = 0; i < data.size(); i++) {
            Message newMessage = (Message) data.get("message" + i);
            if (newMessage == null) continue;
            boolean isSender = newMessage.isTransmitter();
            if (newMessage.isMedia()) makeMediaMessage(newMessage.getMessageText());
            else makeTextMessageInPage(newMessage.getMessageText(), isSender);
        }
    }

    private void showTextField() {
        messageField.setVisible(true);
    }

    private void showButtons() {
        accept.setVisible(true);
        accept.setDisable(false);
        reject.setVisible(true);
        reject.setDisable(false);
    }

    private void hideButtons() {
        accept.setVisible(false);
        accept.setDisable(true);
        reject.setVisible(false);
        reject.setDisable(true);
    }

    private void updateData() {
        Thread loop = new Thread(() -> {
            while (!stop) {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() -> {
                        Request request;
                        if (EDU.userType == UserType.EDU_ADMIN)  request = new Request(RequestType.SHOW_ALL_ADMIN_MESSAGES);
                        else request = new Request(RequestType.SHOW_ALL_MESSAGES);
                        Response response = EDU.serverController.sendRequest(request);
                        if (response.getStatus() == ResponseStatus.OK) {
                            for (int i = 0; i < response.getData().size(); i++) {
                                showData(response.getData());
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
        Request request;
        if (EDU.userType == UserType.EDU_ADMIN)  request = new Request(RequestType.SHOW_ALL_ADMIN_MESSAGES);
        else request = new Request(RequestType.SHOW_ALL_MESSAGES);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            showData(response.getData());
        }
        else AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        updateData();
    }

    class MessageLabel extends Label{
        String message;
        String name;
        String user;
        String type;
        String time;

        public MessageLabel(String message, String name, String user, String type, String time) {
            this.message = message;
            this.name = name;
            this.user = user;
            this.type = type;
            this.time = time;
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
                hideButtons();
                messagePane.getChildren().clear();
                Request request;
                if (EDU.userType == UserType.EDU_ADMIN)  request = new Request(RequestType.SHOW_ADMIN_MESSAGE);
                else request = new Request(RequestType.SHOW_MESSAGE);
                request.addData("user", user);
                request.addData("name", name);
                request.addData("time", time);
                request.addData("message", message);
                request.addData("type", type);
                Response response = EDU.serverController.sendRequest(request);
                if (response.getStatus() == ResponseStatus.OK) {
                    MessagesController.this.user = this;
                    if (EDU.userType == UserType.EDU_ADMIN) {
                        showTextField();
                    }
                    Message message = (Message) response.getData("message");
                    if (message != null) {
                        if (type.equals("request")) {
                            showButtons();
                        }
                        else hideButtons();
                        makeTextMessageInPage(message.getMessageText(), false);
                        String name = (String) response.getData("name");
                        nameLabel.setText(name);
                    }
                    else showMessages(response.getData());
                }
                else AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
            });
        }
    }
}
