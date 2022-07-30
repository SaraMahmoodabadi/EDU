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
    protected RadioButton gradeButton;
    @FXML
    protected RadioButton yearButton;
    @FXML
    protected DatePicker date;
    protected ToggleGroup group;


    public void register(ActionEvent actionEvent) {
        if (isNull()) return;
        Request request = new Request(RequestType.SET_UNIT_SELECTION_TIME);
        request.addData("filter", setFilter());
        request.addData("value", setValue());
        request.addData("date", date.getValue());
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
        return date.getValue() == null || group.getSelectedToggle() == null
                || gradeBox.getValue() == null || yearBox.getValue() == null;
    }

    private void makeBoxes() {
        yearBox.getItems().addAll("1400", "99", "98", "97", "96 and before");
        gradeBox.getItems().addAll(Arrays.toString(Grade.values()));
    }

    private String setFilter() {
        if (group.getSelectedToggle() == gradeButton) return "grade";
        else return "year";
    }

    private String setValue() {
        if (group.getSelectedToggle() == gradeButton) return gradeBox.getValue();
        else return yearBox.getValue();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        makeBoxes();
        gradeButton.setToggleGroup(group);
        yearButton.setToggleGroup(group);
    }
}
