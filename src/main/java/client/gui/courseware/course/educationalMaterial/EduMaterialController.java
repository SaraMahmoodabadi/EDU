package client.gui.courseware.course.educationalMaterial;

import client.gui.AlertMonitor;
import client.gui.EDU;
import client.gui.courseware.course.course.CourseController;
import client.gui.courseware.minPage.CoursesController;
import client.network.ServerController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import shared.model.courseware.educationalMaterial.Item;
import shared.model.courseware.educationalMaterial.ItemType;
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

public class EduMaterialController implements Initializable {
    @FXML
    protected VBox itemsPane;
    @FXML
    protected Text eduMaterialName;
    @FXML
    protected Button addTextButton;
    @FXML
    protected Button deleteMaterial;
    @FXML
    protected Button addMediaButton;
    @FXML
    protected TextArea textArea;
    @FXML
    protected TextArea mediaAddress;
    @FXML
    protected Button registerTextButton;
    @FXML
    protected Button registerMediaButton;
    private boolean stop;
    private String courseCode;
    private String eduMaterialCode;
    private boolean isAssistant;
    private boolean isEditing;
    private String itemCode;

    @FXML
    public void addMedia(ActionEvent event) {
        isEditing = false;
        showMediaOption();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("select file");
        File file = fileChooser.showOpenDialog(ServerController.edu);
        if (file != null) {
            String path = file.getAbsolutePath();
            mediaAddress.setText(path);
        }
    }

    @FXML
    public void addText(ActionEvent event) {
        isEditing = false;
        showTextOption();
    }

    @FXML
    public void deleteEduMaterial(ActionEvent event) {
        Request request = new Request(RequestType.DELETE_EDUCATIONAL_MATERIAL);
        request.addData("educationalMaterialCode", eduMaterialCode);
        request.addData("courseCode", courseCode);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.ERROR) {
            AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        }
        else {
            AlertMonitor.showAlert(Alert.AlertType.INFORMATION, response.getNotificationMessage());
            back(event);
        }
    }

    @FXML
    public void registerMedia(ActionEvent event) {
        if (mediaAddress.getText() == null) return;
        String path = mediaAddress.getText();
        MediaHandler handler = new MediaHandler();
        String media = handler.encode(path);
        int n = path.split("\\.").length;
        String fileFormat = path.split("\\.")[n-1];
        Request request;
        if (isEditing){
            request = new Request(RequestType.EDIT_ITEM_MEDIA);
            request.addData("itemCode", itemCode);
        }
        else
            request = new Request(RequestType.SEND_MEDIA_ITEM);
        request.addData("educationalMaterialCode", eduMaterialCode);
        request.addData("media", media);
        request.addData("fileFormat", fileFormat);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.ERROR) {
            AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        }
        else AlertMonitor.showAlert(Alert.AlertType.INFORMATION, response.getNotificationMessage());
        hideMediaOption();
    }

    @FXML
    public void registerText(ActionEvent event) {
        if (textArea.getText() == null) return;
        Request request;
        if (isEditing){
            request = new Request(RequestType.EDIT_ITEM_TEXT);
            request.addData("itemCode", itemCode);
        }
        else
            request = new Request(RequestType.SEND_TEXT_ITEM);
        request.addData("educationalMaterialCode", eduMaterialCode);
        request.addData("text", textArea.getText());
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.ERROR) {
            AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        }
        else AlertMonitor.showAlert(Alert.AlertType.INFORMATION, response.getNotificationMessage());
        hideTextOption();
    }

    @FXML
    public void back(ActionEvent event) {
        Request request = new Request(RequestType.SHOW_COURSE);
        request.addData("courseCode", this.courseCode);
        ServerController.request = request;
        this.stop = true;
        EDU.sceneSwitcher.switchScene(new ActionEvent(), "course");
    }

    private void showData(Map<String, Object> data) {
        int t = 0;
        double height = 10;
        for (int i = 0; i < data.size() ; i++) {
            Item item = (Item) data.get("item" + i);
            if (item == null) continue;
            t++;
            ItemPane itemPane = new ItemPane(item.getItemCode(), item.getText(), item.getItemType());
            height += itemPane.getPrefHeight() + 15;
            if (height > itemsPane.getHeight()) this.itemsPane.setPrefHeight(height);
            this.itemsPane.getChildren().add(itemPane);
            if (t == 5) {
                addTextButton.setDisable(true);
                addMediaButton.setDisable(true);
            }
            else if (t < 5) {
                addTextButton.setDisable(false);
                addMediaButton.setDisable(false);
            }
        }
    }

    private void hideMediaOption() {
        mediaAddress.clear();
        mediaAddress.setVisible(false);
        registerMediaButton.setVisible(false);
        registerMediaButton.setDisable(true);
    }

    private void showMediaOption() {
        mediaAddress.setVisible(true);
        registerMediaButton.setVisible(true);
        registerMediaButton.setDisable(false);
    }

    private void hideTextOption() {
        textArea.clear();
        textArea.setVisible(false);
        registerTextButton.setVisible(false);
        registerTextButton.setDisable(true);
    }

    private void showTextOption() {
        textArea.setVisible(true);
        registerTextButton.setVisible(true);
        registerTextButton.setDisable(false);
    }

    private void hide() {
        hideMediaOption();
        hideTextOption();
        deleteMaterial.setVisible(false);
        deleteMaterial.setDisable(true);
        addTextButton.setVisible(false);
        addTextButton.setDisable(true);
        addMediaButton.setVisible(false);
        addMediaButton.setDisable(true);
    }

    private void updateData() {
        Thread loop = new Thread(() -> {
            while (!stop) {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() -> {
                        Request request = new Request(RequestType.SHOW_EDUCATIONAL_MATERIAL);
                        request.addData("courseCode", this.courseCode);
                        request.addData("educationalMaterialCode", this.eduMaterialCode);
                        Response response = EDU.serverController.sendRequest(request);
                        if (response.getStatus() == ResponseStatus.OK) {
                            itemsPane.getChildren().clear();
                            showData(response.getData());
                        }
                    });
                } catch (InterruptedException ignored) {}
            }
        });
        loop.start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        isAssistant = false;
        this.stop = false;
        this.isEditing = false;
        if (EDU.userType == UserType.PROFESSOR) {
            hideTextOption();
            hideMediaOption();
        }
        else hide();
        Request request = ServerController.request;
        this.courseCode = (String) request.getData("courseCode");
        this.eduMaterialCode = (String) request.getData("educationalMaterialCode");
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            this.isAssistant = (boolean) response.getData("isAssistant");
            this.eduMaterialName.setText((String) response.getData("name"));
            if (request.getRequestType() == RequestType.SHOW_EDUCATIONAL_MATERIAL)
                showData(response.getData());
        }
        else AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        updateData();
    }

    class ItemPane extends Pane {
        String itemCode;
        String item;
        ItemType itemType;

        public ItemPane(String itemCode, String item, ItemType itemType) {
            this.itemCode = itemCode;
            this.item = item;
            this.itemType = itemType;
            if (itemType == ItemType.TEXT) makeTextPane(item);
            else makeMediaPane(item);
            if (isAssistant || EDU.userType == UserType.PROFESSOR)
                addEditButton();
            if (EDU.userType == UserType.PROFESSOR) addDeleteButton();
        }

        private void makeTextPane(String data) {
            this.setPrefWidth(500);
            this.setPrefHeight(150);
            this.setStyle(String.valueOf(Color.valueOf("#ffd100")));
            this.setBackground(new Background(new BackgroundFill
                    (Color.valueOf("#ffd100"), CornerRadii.EMPTY, Insets.EMPTY)));
            TextArea textArea = new TextArea(data);
            textArea.setWrapText(true);
            textArea.setPrefHeight(140);
            textArea.setPrefWidth(300);
            textArea.setLayoutX(5);
            textArea.setLayoutY(5);
            this.getChildren().add(textArea);
        }

        private void makeMediaPane(String data) {
            this.setPrefWidth(500);
            this.setPrefHeight(60);
            this.setStyle(String.valueOf(Color.valueOf("#ffd100")));
            this.setBackground(new Background(new BackgroundFill
                    (Color.valueOf("#ffd100"), CornerRadii.EMPTY, Insets.EMPTY)));
            Rectangle rectangle = new Rectangle();
            rectangle.setStyle("-fx-background-radius: 10");
            rectangle.setHeight(50);
            rectangle.setWidth(300);
            rectangle.setFill(Color.valueOf("#b151b8"));
            rectangle.setStroke(Color.valueOf("#b151b8"));
            rectangle.setLayoutX(5);
            rectangle.setLayoutY(5);
            this.getChildren().add(rectangle);
            Button button = new Button();
            String fileImage = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "fileIconPath");
            ImageView file = new ImageView("file:" + fileImage);
            file.setFitHeight(30);
            file.setFitWidth(30);
            button.setGraphic(file);
            button.setStyle("-fx-background-color: #b151b8");
            button.setOnAction(event -> open(data));
            button.setLayoutY(5);
            button.setLayoutX(220);
            this.getChildren().add(button);
        }

        private void open(String data) {
            MediaHandler handler = new MediaHandler();
            byte[] file = handler.decode(data);
            String path = "src/main/java/client/resource/sentFiles/" + handler.getName();
            try (FileOutputStream fos = new FileOutputStream(path)) {
                fos.write(file);
                Desktop d = Desktop.getDesktop();
                d.open(new File(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
            File newFile = new File(path);
            newFile.deleteOnExit();
        }

        private void addDeleteButton() {
            Button button = new Button();
            String removeIcn = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "removeIcnPath");
            ImageView removeImage = new ImageView("file:" + removeIcn);
            removeImage.setFitHeight(30);
            removeImage.setFitWidth(30);
            button.setGraphic(removeImage);
            button.setStyle("-fx-background-color: #b151b8");
            button.setLayoutY(10);
            button.setLayoutX(330);
            button.setOnAction(event -> remove());
            this.getChildren().add(button);
        }

        private void remove() {
            Request request = new Request(RequestType.REMOVE_ITEM);
            request.addData("educationalMaterialCode", eduMaterialCode);
            request.addData("itemCode", itemCode);
            Response response = EDU.serverController.sendRequest(request);
            if (response.getStatus() == ResponseStatus.ERROR) {
                AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
            }
            else {
                AlertMonitor.showAlert(Alert.AlertType.INFORMATION, response.getNotificationMessage());
            }
        }

        private void addEditButton() {
            Button button = new Button();
            String editIcn = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "editIconPath");
            ImageView editImage = new ImageView("file:" + editIcn);
            editImage.setFitHeight(30);
            editImage.setFitWidth(30);
            button.setGraphic(editImage);
            button.setStyle("-fx-background-color: #b151b8");
            button.setLayoutY(10);
            button.setLayoutX(430);
            button.setOnAction(event -> edit());
            this.getChildren().add(button);
        }

        private void edit() {
            isEditing = true;
            EduMaterialController.this.itemCode = this.itemCode;
            if (itemType == ItemType.TEXT) {
                showTextOption();
                textArea.setText(item);
            }
            else {
                showMediaOption();
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("select file");
                File file = fileChooser.showOpenDialog(ServerController.edu);
                if (file != null) {
                    String path = file.getAbsolutePath();
                    mediaAddress.setText(path);
                }
            }
        }
    }
}
