package client.gui.message.messages;

import client.gui.AlertMonitor;
import client.gui.EDU;
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
import shared.model.university.lesson.Lesson;
import shared.model.user.UserType;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
    protected String user;
    private boolean stop;

    @FXML
    public void accept(ActionEvent event) {
        sendRequestAnswer(true);
    }

    @FXML
    public void back(ActionEvent event) {
        EDU.sceneSwitcher.switchScene(event, "mainPage");
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
            request.addData("message", messageField.getText());
            request.addData("username", user);
            Response response = EDU.serverController.sendRequest(request);
            if (response.getStatus() == ResponseStatus.OK) {
                makeTextMessageInPage(messageField.getText());
            }
            else AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        }
    }

    private void sendRequestAnswer(boolean result) {
        Request request = new Request(RequestType.SEND_REQUEST_ANSWER);
        request.addData("result", result);
        request.addData("userCode", user);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK)
            AlertMonitor.showAlert(Alert.AlertType.INFORMATION, response.getNotificationMessage());
        else AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
    }

    private void makeTextMessageInPage(String message) {
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
        messagePane.getChildren().add(label);
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
        messagePane.getChildren().add(pane);
    }

    private void download(String message) {
        //TODO
    }

    private void showData(Map<String, Object> data) {
        int t = 0;
        for (int i = 0; i < data.size(); i++) {
            Message newMessage = (Message) data.get("message" + i);
            if (newMessage == null) continue;
            t++;
            if (t > 7) AllMessagesPane.setPrefHeight(AllMessagesPane.getHeight() + 90);
            String name = newMessage.getName();
            String message = newMessage.getMessageText();
            String user = newMessage.getUser();
            String type = newMessage.getType();
            MessageLabel label = new MessageLabel(message, name, user, type);
            AllMessagesPane.getChildren().add(label);
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
                        Request request = new Request(RequestType.SHOW_ALL_MESSAGES);
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
        Request request = new Request(RequestType.SHOW_ALL_MESSAGES);
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

        public MessageLabel(String message, String name, String user, String type) {
            this.message = message;
            this.name = name;
            this.user = user;
            this.type = type;
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
                Request request = new Request(RequestType.SHOW_MESSAGE);
                request.addData("user", user);
                request.addData("type", type);
                Response response = EDU.serverController.sendRequest(request);
                if (response.getStatus() == ResponseStatus.OK) {
                    MessagesController.this.user = user;
                    Message message = (Message) response.getData("message");
                    if (message.isMedia()) {
                        makeMediaMessage(message.getMessageText());
                    }
                    else {
                        makeTextMessageInPage(message.getMessageText());
                    }
                    if (EDU.userType == UserType.EDU_ADMIN) {
                        showTextField();
                    }
                    else if (message.getType().equals("request")) {
                        showButtons();
                    }
                    else hideButtons();
                }
                else AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
            });
        }
    }
}
