package client.gui.edu.registration.lesson.edit;

import client.gui.AlertMonitor;
import client.gui.EDU;
import client.network.offlineClient.OfflineClientHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import shared.model.university.lesson.Day;
import shared.model.university.lesson.Group;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EditLessonController implements Initializable {
    @FXML
    public Label offlineLabel;
    @FXML
    public Button offlineButton;
    @FXML
    protected Rectangle bigRectangle;
    @FXML
    protected Rectangle rectangle1;
    @FXML
    protected Text editText;
    @FXML
    protected Text codeText2;
    @FXML
    protected TextField codeField2;
    @FXML
    protected Line line1;
    @FXML
    protected Text groupText1;
    @FXML
    protected Text professorText2;
    @FXML
    protected TextField professorField2;
    @FXML
    protected Text capacityText2;
    @FXML
    protected TextField capacityField2;
    @FXML
    protected Line line2;
    @FXML
    protected Text TimeOfClassText;
    @FXML
    protected Text Days2;
    @FXML
    protected CheckBox saturday2;
    @FXML
    protected CheckBox sunday2;
    @FXML
    protected CheckBox monday2;
    @FXML
    protected CheckBox tuesday2;
    @FXML
    protected CheckBox wednesday2;
    @FXML
    protected CheckBox thursday2;
    @FXML
    protected Text timeText2;
    @FXML
    protected Text examText2;
    @FXML
    protected DatePicker date2;
    @FXML
    protected Button edit;
    @FXML
    protected Text codeText3;
    @FXML
    protected TextField codeField3;
    @FXML
    protected Text groupText2;
    @FXML
    protected TextField groupField;
    @FXML
    protected Button removeLesson;
    @FXML
    protected Button removeGroup;
    @FXML
    protected Button back;
    @FXML
    protected TextField hour4;
    @FXML
    protected TextField minute4;
    @FXML
    protected TextField hour5;
    @FXML
    protected TextField minute5;
    @FXML
    protected TextField hour6;
    @FXML
    protected TextField minute6;
    private boolean stop;

    public void edit(ActionEvent actionEvent) {
        if (codeField2.getText() == null || codeField2.getText().equals("")) return;
        Request request = new Request(RequestType.EDIT_LESSON);
        int capacity = getCapacity(capacityField2.getText());
        if (capacity != -1) {
            Group group = new Group(codeField2.getText(), capacity, professorField2.getText());
            request.addData("group", group);
        }
        List<Day> days = setEditedPlan();
        if (days.size() != 0)
            request.addData("days", days);
        if (date2.getValue() != null && !date2.getValue().equals("")) {
            String examTime = date2.getValue() + "-" + hour6.getText() + ":" + minute6.getText();
            request.addData("examTime", examTime);
        }
        if (hour4.getText() != null && !hour4.getText().equals("")) {
            String classTime = hour4.getText() + ":" + minute4.getText() +
                    "-" + hour5.getText() + ":" + minute5.getText();
            request.addData("classTime", classTime);
        }
        request.addData("lesson", codeField2.getText());
        request.addData("collegeCode", EDU.collegeCode);
        showRequestResult(request);
    }

    public void removeLesson(ActionEvent actionEvent) {
        Request request = new Request(RequestType.REMOVE_LESSON);
        request.addData("lessonCode", codeField3.getText());
        request.addData("collegeCode", EDU.collegeCode);
        showRequestResult(request);
    }

    public void removeGroup(ActionEvent actionEvent) {
        Request request = new Request(RequestType.REMOVE_LESSON_GROUP);
        request.addData("lessonCode", codeField3.getText());
        request.addData("group", groupField.getText());
        request.addData("collegeCode", EDU.collegeCode);
        showRequestResult(request);
    }

    public void back(ActionEvent actionEvent) {
        stop = true;
        EDU.sceneSwitcher.switchScene(actionEvent, "lessonListPage");
    }

    public void connectToServer(ActionEvent actionEvent) {
        OfflineClientHandler.connectToServer();
    }

    private void showOfflineMood() {
        this.offlineLabel.setVisible(true);
        this.offlineButton.setVisible(true);
        this.offlineButton.setDisable(false);
    }

    private void showRequestResult(Request request) {
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.ERROR) {
            AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        }
        else {
            AlertMonitor.showAlert(Alert.AlertType.INFORMATION, response.getNotificationMessage());
        }
    }

    private int getCapacity(String capacityField) {
        int capacity = -1;
        try {
            capacity = Integer.parseInt(capacityField);
            if (capacity < 0) capacity = -1;
        } catch (NumberFormatException e) {
            String errorMessage = Config.getConfig(ConfigType.GUI_TEXT).
                    getProperty(String.class, "numberError");
            AlertMonitor.showAlert(Alert.AlertType.ERROR, errorMessage);
        }
        return capacity;
    }

    private List<Day> setEditedPlan() {
        List<Day> plan = new ArrayList<>();
        if (saturday2.isSelected()) {
            plan.add(Day.SATURDAY);
        }
        if (sunday2.isSelected()) {
            plan.add(Day.SUNDAY);
        }
        if (monday2.isSelected()) {
            plan.add(Day.MONDAY);
        }
        if (tuesday2.isSelected()) {
            plan.add(Day.TUESDAY);
        }
        if (wednesday2.isSelected()) {
            plan.add(Day.WEDNESDAY);
        }
        if (thursday2.isSelected()) {
            plan.add(Day.THURSDAY);
        }
        return plan;
    }

    private void updateData() {
        Thread loop = new Thread(() -> {
            while (!stop) {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() -> {
                        if (!EDU.isOnline) showOfflineMood();
                    });
                } catch (InterruptedException ignored) {}
                if (!EDU.isOnline) break;
            }
        });
        loop.start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        stop = false;
        updateData();
    }
}
