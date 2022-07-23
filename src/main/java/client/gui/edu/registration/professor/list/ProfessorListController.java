package client.gui.edu.registration.professor.list;

import client.gui.AlertMonitor;
import client.gui.EDU;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import shared.model.university.college.University;
import shared.model.user.UserType;
import shared.model.user.professor.MasterDegree;
import shared.model.user.professor.Professor;
import shared.model.user.professor.Type;
import shared.request.Request;
import shared.request.RequestType;
import shared.response.Response;
import shared.response.ResponseStatus;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ProfessorListController implements Initializable {

    @FXML
    protected Pane pane;
    @FXML
    protected Rectangle rectangle;
    @FXML
    protected TextField codeField;
    @FXML
    protected ComboBox<String> degreeBox;
    @FXML
    protected ComboBox<String> collegeBox;
    @FXML
    protected Button show;
    @FXML
    protected Button edit;
    @FXML
    protected Button back;
    @FXML
    protected ImageView backImage;
    @FXML
    protected TableView<Professor> list;
    @FXML
    protected TableColumn<Professor, String> nameColumn;
    @FXML
    protected TableColumn<Professor, String> collegeColumn;
    @FXML
    protected TableColumn<Professor, String> codeColumn;
    @FXML
    protected TableColumn<Professor, String> degreeColumn;
    @FXML
    protected TableColumn<Professor, String> postColumn;

    public void show(ActionEvent actionEvent) {
        Request request = new Request(RequestType.SHOW_DESIRED_PROFESSORS_LIST);
        request.addData("collegeName", collegeBox.getValue());
        request.addData("degree", degreeBox.getValue());
        request.addData("professorCode", codeField.getText());
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.ERROR) {
            AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
        }
        else {
            List<Professor> desiredProfessors = new ArrayList<>();
            response.getData().forEach((K, V) -> {
                if (K.startsWith("professor")) {
                    desiredProfessors.add((Professor) V);
                }
            });
            list.getItems().clear();
            list.getItems().addAll(desiredProfessors);
        }
    }

    public void edit(ActionEvent actionEvent) {
        EDU.sceneSwitcher.switchScene(actionEvent, "editProfessorPage");
    }

    public void back(ActionEvent actionEvent) {
        EDU.sceneSwitcher.switchScene(actionEvent, "mainPage");
    }

    private void hide() {
        if (EDU.userType != UserType.PROFESSOR ||
                EDU.professorType != Type.DEAN) {
            this.edit.setDisable(true);
            this.edit.setVisible(false);
        }
    }

    private List<Professor> getData() {
        Request request = new Request(RequestType.SHOW_PROFESSORS_LIST_PAGE);
        Response response = EDU.serverController.sendRequest(request);
        if (response.getStatus() == ResponseStatus.ERROR) {
            AlertMonitor.showAlert(Alert.AlertType.ERROR, response.getErrorMessage());
            return null;
        }
        List<Professor> professors = new ArrayList<>();
        response.getData().forEach((K, V) -> {
            if (K.startsWith("professor")) {
                professors.add((Professor) V);
            }
        });
        return professors;
    }

    private void setTableData(List<Professor> professors) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("professorCode"));
        collegeColumn.setCellValueFactory(new PropertyValueFactory<>("collegeCode"));
        degreeColumn.setCellValueFactory(new PropertyValueFactory<>("degree"));
        postColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        list.getItems().addAll(professors);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hide();
        List<Professor> professors = getData();
        collegeBox.getItems().add("-");
        collegeBox.getItems().addAll(University.getUniversity().getCollegeName());
        degreeBox.getItems().add(MasterDegree.ASSISTANT_PROFESSOR.toString());
        degreeBox.getItems().add(MasterDegree.ASSOCIATE_PROFESSOR.toString());
        degreeBox.getItems().add(MasterDegree.FULL_PROFESSOR.toString());
        if (professors != null) {
            setTableData(professors);
        }
    }
}
