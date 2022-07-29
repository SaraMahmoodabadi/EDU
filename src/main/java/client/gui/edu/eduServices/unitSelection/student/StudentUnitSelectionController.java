package client.gui.edu.eduServices.unitSelection.student;

import client.gui.EDU;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import shared.model.university.college.University;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;

import java.net.URL;
import java.util.ResourceBundle;

public class StudentUnitSelectionController implements Initializable {
    @FXML
    protected Button back;
    @FXML
    protected ComboBox<String> collegeBox;
    @FXML
    protected TableView<?> table1;
    @FXML
    protected TableColumn<?, ?> nameColumn1;
    @FXML
    protected TableColumn<?, ?> groupColumn1;
    @FXML
    protected TableColumn<?, ?> gradeColumn1;
    @FXML
    protected TableColumn<?, ?> examColumn1;
    @FXML
    protected TableColumn<?, ?> markColumn1;
    @FXML
    protected TableColumn<?, ?> optionsColumn1;
    @FXML
    protected TableColumn<?, ?> receivedColumn1;
    @FXML
    protected RadioButton alphabeticButton;
    @FXML
    protected RadioButton examTimeButton;
    @FXML
    protected RadioButton gradeButton;
    @FXML
    protected Button show;
    @FXML
    protected TableView<?> table2;
    @FXML
    protected TableColumn<?, ?> nameColumn2;
    @FXML
    protected TableColumn<?, ?> groupColumn2;
    @FXML
    protected TableColumn<?, ?> gradeColumn2;
    @FXML
    protected TableColumn<?, ?> examColumn2;
    @FXML
    protected TableColumn<?, ?> markColumn2;
    @FXML
    protected TableColumn<?, ?> optionsColumn2;
    @FXML
    protected TableColumn<?, ?> receivedColumn2;
    private ToggleGroup group;
    private Request request;


    public void show(ActionEvent event) {
        if (isNull()) return;
        request = new Request(RequestType.GET_LESSONS_IN_UNIT_SELECTION);
        request.addData("college", collegeBox.getValue());
        request.addData("sort", setSort());
        showResponse(request);
    }


    public void back(ActionEvent event) {
        EDU.sceneSwitcher.switchScene(event, "mainPage");
    }

    private boolean isNull() {
        return collegeBox.getValue() == null || group.getSelectedToggle() == null;
    }

    private void makeTable1() {

    }

    private void makeTable2() {

    }

    private void addToggle() {
        alphabeticButton.setToggleGroup(group);
        gradeButton.setToggleGroup(group);
        examTimeButton.setToggleGroup(group);
    }

    private void setBox() {
        collegeBox.getItems().addAll(University.getUniversity().getCollegeName());
    }

    private String setSort() {
        if (group.getSelectedToggle() == alphabeticButton) return "alphabetic";
        else if (group.getSelectedToggle() == examTimeButton) return "examTime";
        else return "grade";
    }

    //TODO
    private void showResponse(Request request) {
        Response response = EDU.serverController.sendRequest(request);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addToggle();
        setBox();
        makeTable1();
        makeTable2();
        request = new Request(RequestType.SHOW_STUDENT_UNIT_SELECTION_PAGE);
        showResponse(request);
    }
}
