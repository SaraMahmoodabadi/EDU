package client.gui.edu.eduServices.unitSelection.eduAssistant;

import client.gui.AlertMonitor;
import client.gui.EDU;
import client.network.offlineClient.OfflineClientHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import shared.model.user.student.Grade;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;

import java.net.URL;
import java.util.ResourceBundle;

public class AssistantUnitSelectionController implements Initializable {
    @FXML
    public Label offlineLabel;
    @FXML
    public Button offlineButton;
    @FXML
    protected Button back;
    @FXML
    protected Button register;
    @FXML
    protected ComboBox<String> gradeBox;
    @FXML
    protected ComboBox<String> yearBox;
    @FXML
    protected ComboBox<String> hour;
    @FXML
    protected DatePicker date;
    @FXML
    protected ComboBox<Integer> hourCollege;
    @FXML
    protected ComboBox<Integer> minuteCollege;
    @FXML
    protected DatePicker dateCollege;
    private boolean stop;


    public void register(ActionEvent actionEvent) {
        if (isNull()) return;
        Request request = new Request(RequestType.SET_UNIT_SELECTION_TIME);
        request.addData("grade", gradeBox.getValue());
        request.addData("year", yearBox.getValue());
        request.addData("time", setDate());
        request.addData("collegeCode", EDU.collegeCode);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            AlertMonitor.showAlert(Alert.AlertType.INFORMATION, response.getNotificationMessage());
        }
        else {
            AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        }
    }

    public void registerEndTime(ActionEvent actionEvent) {
        if (dateCollege.getValue() == null || hourCollege.getValue() == null ||
                minuteCollege.getValue() == null) return;
        String time = dateCollege.getValue() + "-" + hourCollege.getValue() + ":" + minuteCollege.getValue();
        Request request = new Request(RequestType.SET_END_UNIT_SELECTION_TIME);
        request.addData("collegeCode", EDU.collegeCode);
        request.addData("time", time);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.OK) {
            AlertMonitor.showAlert(Alert.AlertType.INFORMATION, response.getNotificationMessage());
        }
        else {
            AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        }
    }

    public void back(ActionEvent actionEvent) {
        stop = true;
        EDU.sceneSwitcher.switchScene(actionEvent, "mainPage");
    }

    public void connectToServer(ActionEvent actionEvent) {
        OfflineClientHandler.connectToServer();
    }

    private void showOfflineMood() {
        this.offlineLabel.setVisible(true);
        this.offlineButton.setVisible(true);
        this.offlineButton.setDisable(false);
    }

    private boolean isNull() {
        return date.getValue() == null || hour.getValue() == null
                || gradeBox.getValue() == null || yearBox.getValue() == null;
    }

    private void makeBoxes() {
        yearBox.getItems().addAll("1400", "99", "98", "97", "96 and before");
        for (Grade grade : Grade.values()) {
            gradeBox.getItems().add(String.valueOf(grade));
        }
        for (Hour time : Hour.values()) {
            hour.getItems().addAll(String.valueOf(time));
        }
        for (int i = 8; i <= 20; i++) {
            hourCollege.getItems().add(i);
        }
        for (int i = 0; i < 60; i++) {
            minuteCollege.getItems().add(i);
        }
    }

    private String setDate() {
        String dateValue = date.getValue().toString();
        String time = "8:0-9:0";
        switch (Hour.valueOf(hour.getValue())) {
            case _8_9:
                time = "8:0-9:0";
                break;
            case _9_10:
                time = "9:0-10:0";
                break;
            case _10_11:
                time = "10:0-11:0";
                break;
            case _11_12:
                time = "11:0-12:0";
                 break;
            case _12_13:
                time = "12:0-13:0";
                break;
            case _13_14:
                time = "13:0-14:0";
                break;
            case _14_15:
                time = "14:0-15:0";
                break;
            case _15_16:
                time = "15:0-16:0";
                break;
            case _16_17:
                time = "16:0-17:0";
                break;
            case _17_18:
                time = "17:0-18:0";
                break;
            case _18_19:
                time = "18:0-19:0";
                break;
        }
        return dateValue + "-" + time;
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
        makeBoxes();
        updateData();
    }
}
