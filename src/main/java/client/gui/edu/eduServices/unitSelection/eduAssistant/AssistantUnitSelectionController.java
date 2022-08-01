package client.gui.edu.eduServices.unitSelection.eduAssistant;

import client.gui.AlertMonitor;
import client.gui.EDU;
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
import java.util.Arrays;
import java.util.ResourceBundle;

public class AssistantUnitSelectionController implements Initializable {
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

    public void back(ActionEvent actionEvent) {
        EDU.sceneSwitcher.switchScene(actionEvent, "mainPage");
    }

    private boolean isNull() {
        return date.getValue() == null || hour.getValue() == null
                || gradeBox.getValue() == null || yearBox.getValue() == null;
    }

    private void makeBoxes() {
        yearBox.getItems().addAll("1400", "99", "98", "97", "96 and before");
        gradeBox.getItems().addAll(Arrays.toString(Grade.values()));
        hour.getItems().addAll(Arrays.toString(Hour.values()));
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        makeBoxes();
    }
}
