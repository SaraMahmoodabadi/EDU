package client.gui.courseware.course.exercise.addExercise;

import client.gui.AlertMonitor;
import client.gui.EDU;
import client.network.ServerController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import shared.model.courseware.educationalMaterial.ItemType;
import shared.model.courseware.exercise.Exercise;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;
import shared.util.media.MediaHandler;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class NewExerciseController implements Initializable{
    @FXML
    protected TextField nameField;
    @FXML
    protected ComboBox<String> openHour;
    @FXML
    protected ComboBox<String> openMinute;
    @FXML
    protected ComboBox<String> closeHour;
    @FXML
    protected ComboBox<String> closeMinute;
    @FXML
    protected ComboBox<String> uploadHour;
    @FXML
    protected ComboBox<String> uploadMinute;
    @FXML
    protected TextArea descriptionArea;
    @FXML
    protected RadioButton textType;
    @FXML
    protected RadioButton mediaType;
    @FXML
    protected TextField fileField;
    @FXML
    protected DatePicker openDate;
    @FXML
    protected DatePicker closeDate;
    @FXML
    protected DatePicker uploadDate;
    private ToggleGroup exerciseType;
    private String courseCode;
    private String exerciseCode;

    @FXML
    public void addFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("select file");
        File file = fileChooser.showOpenDialog(ServerController.edu);
        if (file != null) {
            String path = file.getAbsolutePath();
            fileField.setText(path);
        }
    }

    @FXML
    public void register(ActionEvent event) {
        if (isNull()) {
            String error = Config.getConfig(ConfigType.GUI_TEXT).getProperty(String.class, "nullFieldsError");
            AlertMonitor.showAlert(Alert.AlertType.ERROR, error);
            return;
        }
        String name = nameField.getText();
        String openTime = openDate.getValue() + "-" + openHour.getValue() + ":" + openMinute.getValue();
        String closeTime = closeDate.getValue() + "-" + closeHour.getValue() + ":" + closeMinute.getValue();
        String updateTime = uploadDate.getValue() + "-" + uploadHour.getValue() + ":" + uploadMinute.getValue();
        ItemType type = getValidType();
        String description = descriptionArea.getText();
        String path = fileField.getText();
        MediaHandler handler = new MediaHandler();
        String file = handler.encode(path);
        int n = path.split("\\.").length;
        String fileFormat = path.split("\\.")[n-1];
        Exercise exercise = new Exercise(courseCode, exerciseCode, name, openTime,
                closeTime, updateTime, file, description, type);
        Request request = new Request(RequestType.ADD_EXERCISE_DESCRIPTION);
        request.addData("exercise", exercise);
        request.addData("fileFormat", fileFormat);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            AlertMonitor.showAlert(Alert.AlertType.INFORMATION, response.getNotificationMessage());
            back(event);
        }
        else AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
    }

    @FXML
    public void back(ActionEvent event) {
        Request request = new Request(RequestType.SHOW_COURSE);
        request.addData("courseCode", this.courseCode);
        ServerController.request = request;
        EDU.sceneSwitcher.switchScene(new ActionEvent(), "course");
    }

    private ItemType getValidType() {
        if (exerciseType.getSelectedToggle() == textType) return ItemType.TEXT;
        else return ItemType.MEDIA_FILE;
    }

    private boolean isNull() {
        return openHour.getValue() == null || openMinute.getValue() == null ||
                openDate.getValue() == null || closeHour.getValue() == null ||
                closeMinute.getValue() == null || closeDate.getValue() == null ||
                uploadHour.getValue() == null || uploadMinute.getValue() == null ||
                uploadDate.getValue() == null || exerciseType.getSelectedToggle() == null ||
                descriptionArea.getText() == null || fileField.getText() == null;
    }

    private void makeBoxes() {
        for (int i = 0; i < 24; i++) {
            String t = String.valueOf(i);
            if (i < 10) t = 0 + "" + i;
            openHour.getItems().add(t);
            closeHour.getItems().add(t);
            uploadHour.getItems().add(t);
        }
        for (int i = 0; i < 60; i++) {
            String t = String.valueOf(i);
            if (i < 10) t = 0 + "" + i;
            openMinute.getItems().add(t);
            closeMinute.getItems().add(t);
            uploadMinute.getItems().add(t);
        }
    }

    private void makeToggle() {
        this.exerciseType = new ToggleGroup();
        textType.setToggleGroup(exerciseType);
        mediaType.setToggleGroup(exerciseType);
        textType.setSelected(true);
        exerciseType.selectToggle(textType);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Request request = ServerController.request;
        this.courseCode = (String) request.getData("courseCode");
        this.nameField.setText((String) request.getData("name"));
        this.nameField.setEditable(false);
        this.exerciseCode = courseCode + "-" + this.nameField.getText();
        makeBoxes();
        makeToggle();
    }
}
